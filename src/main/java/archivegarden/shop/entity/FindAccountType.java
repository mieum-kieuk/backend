package archivegarden.shop.entity;

public enum FindAccountType {
    EMAIL("이메일"), PHONENUMBER("휴대폰 번호");

    private final String description;

    FindAccountType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
