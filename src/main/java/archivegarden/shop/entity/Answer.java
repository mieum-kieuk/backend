package archivegarden.shop.entity;

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

    @Column(nullable = false, length = 1000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;    //단방향

    public static Answer createAnswer(String content, Admin admin) {
        Answer answer = new Answer();
        answer.content = content;
        answer.admin = admin;
        return answer;
    }

    /**
     * 답변 내용 수정
     */
    public void update(String newAnswerContent) {
        this.content = newAnswerContent;
    }
}

