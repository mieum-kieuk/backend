package archivegarden.shop.entity;

import java.util.Arrays;

public enum Category {
    MASKING_TAPE("masking-tape", "Masking tape"),STICKER("sticker", "Sticker"),MEMO("memo", "Memo");

    private final String pathVariable;
    private final String displayName;

    Category(String pathVariable, String displayName) {
        this.pathVariable = pathVariable;
        this.displayName = displayName;
    }

    public static Category of(String pathVariable) {
        Category category = Arrays.stream(Category.values())
                .filter(c -> c.pathVariable.equals(pathVariable))
                .findFirst().orElse(null);
        System.out.println("category = " + category);
        return category;
    }

    public String getDisplayName() {
        return displayName;
    }
}
