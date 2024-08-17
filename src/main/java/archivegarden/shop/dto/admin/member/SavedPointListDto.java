package archivegarden.shop.dto.admin.member;

import archivegarden.shop.entity.SavedPointType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class SavedPointListDto {

    private String name;
    private String loginId;
    private String grade;
    private String description;
    private String createdAt;
    private String expiredAt;
    private String amount;
    private String balance;

    @QueryProjection
    public SavedPointListDto(String name, String loginId, SavedPointType savedPointType,
                             LocalDateTime createdAt, LocalDateTime expiredAt, int amount, int balance) {
        this.name = name;
        this.loginId = loginId;
        this.description = savedPointType.getDescription();
        this.createdAt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(createdAt);
        this.expiredAt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(expiredAt);
        this.amount = new DecimalFormat("###,###").format(amount);
        this.balance =  new DecimalFormat("###,###").format(balance);
    }
}
