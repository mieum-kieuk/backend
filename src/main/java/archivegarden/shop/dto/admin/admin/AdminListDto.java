package archivegarden.shop.dto.admin.admin;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class AdminListDto {

    public Integer id;
    public String name;
    public String loginId;
    public String email;
    public String isAuthorized;
    public String createdAt;

    @QueryProjection
    public AdminListDto(Integer id, String name, String loginId, String email, String isAuthorized, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.loginId = loginId;
        this.email = email;
        this.isAuthorized = Boolean.parseBoolean(isAuthorized) ? "O" : "X";
        this.createdAt = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss").format(createdAt);
    }
}
