package archivegarden.shop.entity;

import archivegarden.shop.dto.community.notice.AddNoticeForm;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Notice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    @Column(length = 30, nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    private int hit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    //==비즈니스 로직==//
    /**
     * 조회수 증가
     */
    public void addHit() {
        hit++;
    }

    /**
     * 공지사항 수정
     */
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    //==생성자 메서드==//
    public static Notice createNotice(AddNoticeForm form, Member member) {
        Notice notice = new Notice();
        notice.title = form.getTitle();
        notice.content = form.getContent();
        notice.member = member;
        return notice;
    }
}
