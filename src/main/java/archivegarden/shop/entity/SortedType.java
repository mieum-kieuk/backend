package archivegarden.shop.entity;

import java.util.Arrays;

public enum SortedType {
    NEW("0"), HIT("1"), NAME("2"), LOW_PRICE("3"), HIGH_PRICE("4");

    private final String sortedCode;

    SortedType(String sortedCode) {
        this.sortedCode = sortedCode;
    }

    public String getSortedCode() {
        return sortedCode;
    }

    public static SortedType of(String sortedCode) {
        return Arrays.stream(SortedType.values())
                .filter(s -> s.getSortedCode().equals(sortedCode))
                .findFirst().orElse(null);
    }
}
