package archivegarden.shop.exception;

import archivegarden.shop.entity.Admin;
import archivegarden.shop.entity.BaseTimeEntity;
import archivegarden.shop.entity.ProductInquiry;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;

    @Lob
    @Column(nullable = false)
    private String content;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_inquiry_id")
    private ProductInquiry productInquiry;  //일대일 단방향

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;    //다대일 단방향

    //==비즈니스 로직==//
    /**
     * 답변 내용 수정
     */
    public void update(String content) {
        this.content = content;
    }

    //==생성자 메서드==//
    public static Answer createAnswer(String content, ProductInquiry productInquiry, Admin admin) {
        Answer answer = new Answer();
        answer.content = content;
        answer.productInquiry = productInquiry;
        answer.admin = admin;
        return answer;
    }
}
