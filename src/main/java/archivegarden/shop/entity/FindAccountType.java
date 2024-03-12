package archivegarden.shop.entity;

public enum FindAccountType {
    EMAIL("이메일"), SMS("휴대전화번호");

    private final String description;

    FindAccountType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
