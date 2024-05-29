package archivegarden.shop.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    private int count;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;  //다대일 단방향

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id")
    private Product product;    //다대일 단방향

    //==비즈니스 로직==//
    public void addCount(int count) {
        this.count += count;
    }

    //==생성자==//
    @Builder
    public Cart(int count, Member member, Product product) {
        this.count = count;
        this.member = member;
        this.product = product;
    }
}
