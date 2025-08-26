package archivegarden.shop.controller.admin.category;

import archivegarden.shop.dto.admin.category.CategoryNode;
import archivegarden.shop.dto.admin.category.CreateCategoryRequestDto;
import archivegarden.shop.dto.admin.category.MoveCategoryRequestDto;
import archivegarden.shop.dto.admin.category.UpdateCategoryNameRequestDto;
import archivegarden.shop.dto.common.ApiResponseDto;
import archivegarden.shop.service.admin.category.AdminCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "카테고리-관리자-API", description = "관리자 페이지에서 카테고리 관련 데이터를 처리하는 API입니다.")
@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryApiController {

    private final AdminCategoryService categoryService;

    @Operation(
            summary = "카테고리 조회",
            description = "카테고리를 조회합니다..",
            responses = {
                    @ApiResponse(responseCode = "200", description = "카테고리를 조회했습니다."),
                    @ApiResponse(responseCode = "403", description = "관리자 권한이 있는 사용자만 이용 가능합니다."),
            }
    )
    @GetMapping
    public List<CategoryNode> categories() {
        List<CategoryNode> categories = categoryService.getCategories();
        return categories;
    }

    @Operation(
            summary = "카테고리 소분류 조회",
            description = "카테고리 소분류를 조회합니다..",
            responses = {
                    @ApiResponse(responseCode = "200", description = "카테고리를 조회했습니다."),
                    @ApiResponse(responseCode = "403", description = "관리자 권한이 있는 사용자만 이용 가능합니다."),
                    @ApiResponse(responseCode = "404", description = "parent 카테고리가 존재하지 않습니다."),
            }
    )
    @GetMapping("/{parentId}/children")
    public List<CategoryNode> childCategories(@PathVariable("parentId") Long parentId) {
        List<CategoryNode> categories = categoryService.getChildrenCategories(parentId);
        return categories;
    }

    @Operation(
            summary = "카테고리 등록",
            description = "카테고리를 추가합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "카테고리를 등록했습니다."),
                    @ApiResponse(responseCode = "400", description = "요청 형식이 올바르지 않습니다."),
                    @ApiResponse(responseCode = "403", description = "관리자 권한이 있는 사용자만 이용 가능합니다."),
                    @ApiResponse(responseCode = "404", description = "parent 카테고리가 존재하지 않습니다."),
                    @ApiResponse(responseCode = "409", description = "중복되는 데이터가 존재합니다.")
            }
    )
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCategory(@Validated @RequestBody CreateCategoryRequestDto req) {
        Long categoryId = categoryService.addCategory(req.name(), req.parentId());
        return ResponseEntity.ok(Map.of("id", categoryId));
    }

    @Operation(
            summary = "카테고리 이름 수정",
            description = "카테고리 이름을 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "카테고리명을 수정했습니다."),
                    @ApiResponse(responseCode = "400", description = "요청 형식이 올바르지 않습니다."),
                    @ApiResponse(responseCode = "403", description = "관리자 권한이 있는 사용자만 이용 가능합니다."),
                    @ApiResponse(responseCode = "404", description = "카테고리가 존재하지 않습니다."),
                    @ApiResponse(responseCode = "409", description = "중복되는 데이터가 존재합니다.")
            }
    )
    @PatchMapping("/{categoryId}")
    public ResponseEntity<ApiResponseDto> updateCategoryName(
            @PathVariable("categoryId") Long categoryId,
            @Validated @RequestBody UpdateCategoryNameRequestDto req
    ) {
        categoryService.updateCategoryName(categoryId, req.name());
        return ResponseEntity.ok(new ApiResponseDto("OK", "카테고리명이 수정되었습니다."));
    }

    @Operation(
            summary = "카테고리 위치 수정",
            description = "카테고리 위치를 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "카테고리 위치를 수정했습니다."),
                    @ApiResponse(responseCode = "400", description = "요청 형식이 올바르지 않습니다."),
                    @ApiResponse(responseCode = "403", description = "관리자 권한이 있는 사용자만 이용 가능합니다."),
                    @ApiResponse(responseCode = "404", description = "카테고리가 존재하지 않습니다."),
                    @ApiResponse(responseCode = "409", description = "중복되는 데이터가 존재합니다.")
            }
    )
    @PatchMapping("/move")
    public ResponseEntity<ApiResponseDto> moveCategory(@RequestBody MoveCategoryRequestDto req) {
        categoryService.moveCategory(req.id(), req.newParentId(), req.newIndex());
        return ResponseEntity.ok(new ApiResponseDto("OK", "카테고리 위치가 변경되었습니다."));
    }

    @Operation(
            summary = "카테고리 삭제",
            description = "카테고리를 삭제합니다. 상위 카테고리 삭제 시 하위 카테고리 모두 같이 삭제됩니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "카테고리를 삭제했습니다."),
                    @ApiResponse(responseCode = "400", description = "요청 형식이 올바르지 않습니다."),
                    @ApiResponse(responseCode = "403", description = "관리자 권한이 있는 사용자만 이용 가능합니다."),
                    @ApiResponse(responseCode = "404", description = "카테고리가 존재하지 않습니다.")
            }
    )
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponseDto> deleteCategory(@PathVariable("categoryId") Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(new ApiResponseDto("OK", "카테고리가 삭제되었습니다."));
    }
}
