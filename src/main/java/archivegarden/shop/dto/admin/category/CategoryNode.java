package archivegarden.shop.dto.admin.category;

import java.util.List;

public record CategoryNode(
        Long id,
        String name,
        Integer sortOrder,
        Long parentId,
        List<CategoryNode> children
) {}


