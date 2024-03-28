package archivegarden.shop.entity;

public enum Category {
    Masking_tape("Masking tape"), Sticker("Sticker"), Memo("Memo");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
