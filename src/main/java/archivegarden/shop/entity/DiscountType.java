package archivegarden.shop.entity;

public enum DiscountType {
    RATE("정률 할인"), FIX("정액 할인");

    private final String description;

    DiscountType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
