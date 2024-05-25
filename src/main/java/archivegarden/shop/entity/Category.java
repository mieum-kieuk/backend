package archivegarden.shop.entity;

import java.util.Arrays;

public enum Category {
    OCEAN("ocean", "Ocean"), SUNSET("sunset", "Sunset"), CAFE("cafe", "Cafe");

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
