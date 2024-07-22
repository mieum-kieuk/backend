package archivegarden.shop.entity;

import archivegarden.shop.dto.admin.help.notice.AddNoticeForm;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String title;

    @Lob
    @Column(length = 2000, nullable = false)
    private String content;

    @ColumnDefault(value = "0")
    private int hit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;    //다대일 단방향

    //==비즈니스 로직==//
    /**
     * 게시글 조회수 증가
     */
    public void addHit() {
        hit++;
    }

    /**
     * 게시글 수정
     */
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    //==생성자 메서드==//
    public static Notice createNotice(AddNoticeForm form, Admin admin) {
        Notice notice = new Notice();
        notice.title = form.getTitle();
        notice.content = form.getContent();
        notice.admin = admin;
        return notice;
    }
}
