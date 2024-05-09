package archivegarden.shop.dto.member;

import archivegarden.shop.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class FindIdResultDto {

    private String name;
    private String email;
    private String loginId;
    private String grade;
    private String createdAt;

    public FindIdResultDto(Member member) {
        this.name = member.getName();
        this.email = member.getEmail();

        String newLoginId = member.getLoginId().substring(0, 3);
        for(int i = 3; i < member.getLoginId().length(); i++) {
            newLoginId += "*";
        }
        this.loginId = newLoginId;
        this.grade = member.getGrade().getDescription();
        this.createdAt = DateTimeFormatter.ofPattern("yyyy년 M월 d일").format(member.getCreatedAt());
    }
}
