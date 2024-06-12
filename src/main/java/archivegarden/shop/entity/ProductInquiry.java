package archivegarden.shop.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name = "PRODUCT_INQUIRY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductInquiry extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_inquiry_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "is_secret", nullable = false)
    private String isSecret;

    @Column(name = "is_answered", nullable = false)
    private String isAnswered;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;  //다대일 단방향

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id")
    private Product product;  //다대일 단방향


    //==비즈니스 로직==//
    /**
     * 수정
     */
    public void update(String title, String content, Product product) {
        this.title = title;
        this.content = content;
        this.product = product;
    }

    @Builder
    public ProductInquiry(String title, String content, boolean isSecret, Member member, Product product) {
        this.title = title;
        this.content = content;
        this.isSecret = String.valueOf(isSecret).toUpperCase();
        this.isAnswered = Boolean.FALSE.toString().toUpperCase();
        this.member = member;
        this.product = product;
    }
}
