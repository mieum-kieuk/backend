package archivegarden.shop.entity;

public enum Grade {
    WHITE("일반"), VIP("VIP");

    private final String description;

    Grade(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
