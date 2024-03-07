package archivegarden.shop.dto.member;

import archivegarden.shop.entity.Grade;
import archivegarden.shop.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class FindIdResultDto {

    private String name;
    private String email;
    private String loginId;
    private Grade grade;
    private LocalDateTime createdAt;

    public FindIdResultDto(Member member) {
        this.name = member.getName();
        this.email = member.getEmail();
        this.loginId = member.getLoginId();
        this.grade = member.getGrade();
        this.createdAt = member.getCreatedAt();
    }
}
