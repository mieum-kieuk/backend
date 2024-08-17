package archivegarden.shop.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "SAVED_POINT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SavedPoint {

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

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
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
        savedPoint.createdAt = LocalDateTime.now();
        savedPoint.expiredAt = savedPoint.createdAt.plusYears(1);
        savedPoint.setMember(member);
        return savedPoint;
    }
}
