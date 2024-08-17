package archivegarden.shop.dto.admin.admin;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class AdminListDto {

    public Long id;
    public String name;
    public String loginId;
    public String email;
    public String isAuthorized;
    public String createdAt;

    @QueryProjection
    public AdminListDto(Long id, String name, String loginId, String email, boolean isAuthorized, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.loginId = loginId;
        this.email = email;
        this.isAuthorized = isAuthorized ? "O" : "X";
        this.createdAt = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss").format(createdAt);
    }
}
