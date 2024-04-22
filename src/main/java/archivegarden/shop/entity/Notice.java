package archivegarden.shop.entity;

import archivegarden.shop.dto.community.notice.AddNoticeForm;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("NOTICE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends Board {

    //==생성자==//
    @Builder
    public Notice(AddNoticeForm form, Member member) {
        super(form.getTitle(), form.getContent(), member);
    }
}
