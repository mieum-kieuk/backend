package archivegarden.shop.controller.user.advice;

import archivegarden.shop.dto.admin.category.CategoryNode;
import archivegarden.shop.service.user.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final CategoryService categoryService;

    @ModelAttribute("parentCategories")
    public List<CategoryNode> parentCategories() {
        return categoryService.getParentCategories();
    }
}
