package archivegarden.shop.entity;

import archivegarden.shop.dto.user.community.inquiry.EditInquiryForm;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "is_secret", nullable = false)
    private boolean isSecret;

    @Column(name = "is_answered", nullable = false)
    private boolean isAnswered;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;  //양방향

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Product product;  //단방향

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "answer_id")
    private Answer answer;  //단방향

    @Builder
    public Inquiry(String title, String content, boolean isSecret, Member member, Product product) {
        this.title = title;
        this.content = content;
        this.isSecret = isSecret;
        this.isAnswered = false;
        this.member = member;
        this.product = product;
    }

    /**
     * 상품 문의 수정
     */
    public void update(EditInquiryForm form, Product product) {
        this.title = form.getTitle();
        this.content = form.getContent();
        this.isSecret = form.getIsSecret();
        this.product = product;
    }

    /**
     * 답변 달림
     */
    public void writeAnswer(Answer answer) {
        this.answer = answer;
        updateAnswerStatus(true);
    }

    /**
     * 답변 상태 수정
     */
    public void updateAnswerStatus(boolean isAnswered) {
        this.isAnswered = isAnswered;
    }

}
