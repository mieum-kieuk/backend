package archivegarden.shop.service.admin.category;

import archivegarden.shop.dto.admin.category.CategoryNode;
import archivegarden.shop.entity.Category;
import archivegarden.shop.exception.api.ConflictApiException;
import archivegarden.shop.exception.api.EntityNotFoundApiException;
import archivegarden.shop.repository.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 카테고리 추가
     *
     * @param name     카테고리명
     * @param parentId 대분류 카테고리 ID
     * @return 저장된 카테고리 ID
     * @throws EntityNotFoundApiException 상위 카테고리가 존재하지 않는 경우
     */
    @CacheEvict(cacheNames = "parentCategories", allEntries = true)
    @Transactional
    public Long addCategory(String name, Long parentId) {
        if (categoryRepository.existsByParentIdAndName(parentId, name)) {
            throw new ConflictApiException("해당 상위 카테고리에 같은 이름의 카테고리가 있습니다.");
        }

        if (parentId != null && !categoryRepository.existsById(parentId)) {
            throw new EntityNotFoundApiException("상위 카테고리가 존재하지 않습니다.");
        }

        Category parent = null;
        if (parentId != null) {
            parent = categoryRepository.getReferenceById(parentId);
        }

        int nextOrder = categoryRepository.findNextOrder(parentId);
        Category category = Category.createCategory(parent, nextOrder, name);
        categoryRepository.save(category);
        return category.getId();
    }

    /**
     * 카테고리 조회
     *
     * 카테고리 목록을 조회한 뒤 트리 구조로 변환하여 반환합니다.
     *
     * @return 트리 형태의 카테고리 목록
     */
    @Transactional(readOnly = true)
    public List<CategoryNode> getCategories() {
        List<Category> categories = categoryRepository.findCategories();

        Map<Long, CategoryNode> categoryNodeMap = new LinkedHashMap<>();
        for (Category c : categories) {
            categoryNodeMap.put(c.getId(), new CategoryNode(
                    c.getId(),
                    c.getName(),
                    c.getSortOrder(),
                    c.getParentId(),
                    new ArrayList<>()
            ));
        }

        List<CategoryNode> categoryTree = new ArrayList<>();
        for (CategoryNode node : categoryNodeMap.values()) {
            if (node.parentId() == null) {
                categoryTree.add(node);
            } else {
                CategoryNode parentNode = categoryNodeMap.get(node.parentId());
                if (parentNode != null) {
                    parentNode.children().add(node);
                }
            }
        }

        return categoryTree;
    }

    /**
     * 대분류 카테고리 조회
     *
     * 대분류 카테고리 목록을 조회합니다.
     *
     * @return 카테고리 목록
     */
    @Transactional(readOnly = true)
    public List<CategoryNode> getParentCategories() {
        List<Category> categories = categoryRepository
                .findParentCategories();

        return categories.stream()
                .map(c -> new CategoryNode(
                        c.getId(),
                        c.getName(),
                        c.getSortOrder(),
                        null,
                        new ArrayList<>()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 소분류 카테고리 조회
     *
     * 소분류 카테고리 목록을 조회합니다.
     *
     * @return 카테고리 목록
     * @throws EntityNotFoundApiException 대분류 카테고리가 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public List<CategoryNode> getChildrenCategories(Long parentId) {
        if (!categoryRepository.existsById(parentId)) {
            throw new EntityNotFoundApiException("대분류 카테고리가 존재하지 않습니다.");
        }

        List<Category> categories = categoryRepository
                .findChildrenCategories(parentId);

        return categories.stream()
                .map(c -> new CategoryNode(
                        c.getId(),
                        c.getName(),
                        c.getSortOrder(),
                        parentId,
                        new ArrayList<>()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 카테고리명 수정
     *
     * @param categoryId 카테고리 ID
     * @param newName    새로운 카테고리명
     * @throws EntityNotFoundApiException 카테고리가 존재하지 않는 경우
     * @throws ConflictApiException       (parent_id, name) 중복 되는 경우
     */
    @CacheEvict(cacheNames = "parentCategories", allEntries = true)
    @Transactional
    public void updateCategoryName(Long categoryId, String newName) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundApiException("존재하지 않는 카테고리입니다."));

        //기존 카테고리명 == 새로운 카테고리명
        if (newName.equals(category.getName())) return;

        //같은 부모 내 중복 방지
        Long parentId = category.getParentId();
        if (categoryRepository.existsByParentIdAndName(parentId, newName)) {
            throw new ConflictApiException("해당 상위 카테고리에 같은 이름의 카테고리가 있습니다.");
        }

        category.updateName(newName);
    }

    /**
     * 카테고리 순서 변경
     *
     * @param id          카테고리 ID
     * @param newParentId 이동할 상위 카테고리 ID
     * @param newIndex    이동한 카테고리의 순서
     * @throws EntityNotFoundApiException 카테고리가 존재하지 않는 경우
     */
    @CacheEvict(cacheNames = "parentCategories", allEntries = true)
    @Transactional
    public void moveCategory(Long id, Long newParentId, int newIndex) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundApiException("존재하지 않는 카테고리입니다."));

        Long oldParentId = category.getParentId();

        Category newParent = null;
        if (newParentId != null) {
            if (!categoryRepository.existsById(newParentId)) {
                throw new EntityNotFoundApiException("상위 카테고리가 존재하지 않습니다.");
            }

            newParent = categoryRepository.getReferenceById(newParentId);
        }

        List<Category> oldSiblings = categoryRepository.findSiblings(oldParentId);
        int oldIndex = indexOf(oldSiblings, id);

        // 같은 부모 안에서만 순서 변경
        if (Objects.equals(oldParentId, newParentId)) {
            int size = oldSiblings.size();
            int clampedIndex = Math.max(0, Math.min(newIndex, size - 1)); // 같은 부모면 size-1까지
            if (clampedIndex == oldIndex) return;

            if (clampedIndex > oldIndex) {
                categoryRepository.bulkDecrementSortOrder(newParentId, oldIndex + 1, clampedIndex);
            } else {
                categoryRepository.bulkIncrementSortOrder(newParentId, clampedIndex, oldIndex - 1);
            }
            category.updateSortOrder(clampedIndex);
            return;
        }

        // 부모가 바뀌는 경우
        int newSize = categoryRepository.findSiblings(newParentId).size();
        int clampedIndex = Math.max(0, Math.min(newIndex, newSize));

        // 기존 부모에서 뒤쪽 땡기기
        categoryRepository.bulkDecrementSortOrder(oldParentId, oldIndex + 1, Integer.MAX_VALUE);
        // 새 부모에서 자리 만들기
        categoryRepository.bulkIncrementSortOrder(newParentId, clampedIndex, Integer.MAX_VALUE);

        category.updateParent(newParent);
        category.updateSortOrder(clampedIndex);
    }

    /**
     * 카테고리 삭제 후 sortOrder -1 당김
     *
     * 상위 카테고리 삭제 시 하위 카테고리 모두 같이 삭제됩니다.
     * 하위 카테고리 삭제 시 하위 카테고리 1개만 삭제됩니다.
     *
     * @throws EntityNotFoundApiException 카테고리가 존재하지 않는 경우
     */
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundApiException("카테고리가 존재하지 않습니다."));

        Long parentId = category.getParentId();
        int deleteIndex = category.getSortOrder();

        categoryRepository.delete(category);

        categoryRepository.bulkDecrementSortOrder(parentId, deleteIndex + 1, Integer.MAX_VALUE);
    }

    /**
     * 주어진 카테고리 목록에서 targetId를 가진 카테고리의 index를 찾습니다.
     *
     * @param categories 검색할 카테고리 목록
     * @param targetId   찾을 카테고리의 ID
     * @return 카테고리 목록 내의 index (0-based)
     * @throws EntityNotFoundApiException 해당 ID를 가진 카테고리가 목록에 없을 경우
     */
    private int indexOf(List<Category> categories, Long targetId) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId().equals(targetId)) {
                return i;
            }
        }

        throw new EntityNotFoundApiException("해당 부모 아래에 카테고리가 존재하지 않습니다.");
    }
}
