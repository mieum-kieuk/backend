package archivegarden.shop.event;

public record TempPasswordIssuedEvent(String email, String name, String tempPassword) {}

