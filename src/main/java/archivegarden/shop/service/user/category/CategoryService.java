package archivegarden.shop.service.user.category;

import archivegarden.shop.dto.admin.category.CategoryNode;
import archivegarden.shop.repository.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Cacheable(cacheNames = "parentCategories")
    @Transactional(readOnly = true)
    public List<CategoryNode> getParentCategories() {
        return categoryRepository.findParentCategories()
                .stream()
                .map(c -> new CategoryNode(c.getId(), c.getName(), c.getSortOrder(), null, new ArrayList<>()))
                .toList();
    }
}
