package archivegarden.shop.entity;

import archivegarden.shop.dto.community.qna.AddQnaForm;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("QNA")
public class Qna extends Board {

    @Column(length = 10)
    private String isSecret;

    @ManyToOne(fetch = FetchType.LAZY)  //다대일 양방향
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardImage> images = new ArrayList<>();    //다대일 양방향

    //==연관관계 메서드==//
    private void setProduct(Product product) {
        this.product = product;
        product.getQnas().add(this);
    }

    private void addBoardImage(BoardImage boardImage) {
        images.add(boardImage);
        boardImage.setBoard(this);
    }

    //==생성자==//
    @Builder
    public Qna(AddQnaForm form, Member member, Product product, List<BoardImage> images) {
        super(form.getTitle(), form.getContent(), member);

        this.isSecret = String.valueOf(form.isSecret()).toUpperCase();
        if(product != null) {
            setProduct(product);
        }
        if(!images.isEmpty()) {
            for (BoardImage image : images) {
                addBoardImage(image);
            }
        }
    }
}
