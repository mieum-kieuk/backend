package archivegarden.shop.entity;

import java.util.Arrays;

public enum Category {
    STICKER("sticker", "스티커"), MASKINGTAPE("masking-tape", "마스킹 테이프"), PHONECASE("phone-case", "핸드폰 케이스");

    private final String pathVariable;
    private final String displayName;

    Category(String pathVariable, String displayName) {
        this.pathVariable = pathVariable;
        this.displayName = displayName;
    }

    public static Category of(String pathVariable) {
        return Arrays.stream(Category.values())
                .filter(c -> c.pathVariable.equals(pathVariable))
                .findFirst().orElse(null);
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPathVariable() {
        return pathVariable;
    }
}
