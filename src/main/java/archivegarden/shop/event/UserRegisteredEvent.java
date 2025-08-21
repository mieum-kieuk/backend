package archivegarden.shop.event;

import java.time.LocalDateTime;

public record UserRegisteredEvent(String email, String name, LocalDateTime createdAt) {}

