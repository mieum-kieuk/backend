package archivegarden.shop.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "SAVED_POINT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SavedPoint extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "saved_point_id")
    private Long id;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private int balance;

    @Enumerated(value = EnumType.STRING)
    private SavedPointType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;  // 다대일 양방향

    //==연관관계 메서드==//
    private void setMember(Member member) {
        this.member = member;
        member.getSavedPoints().add(this);
    }

    //==생성자 메서드==//
    public static SavedPoint createSavedPoint(int amount, SavedPointType type, Member member) {
        SavedPoint savedPoint = new SavedPoint();
        savedPoint.amount = amount;
        savedPoint.balance = amount;
        savedPoint.type = type;
        savedPoint.setMember(member);
        return savedPoint;
    }
}
