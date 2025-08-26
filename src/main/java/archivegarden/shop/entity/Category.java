package archivegarden.shop.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "UQ_CATEGORY_NAME", columnNames = {"parent_id", "name"}),
       indexes = @Index(name = "IDX_CATEGORY_PARENT_SORT", columnList = "parent_id, sort_order"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "FK_CATEGORY_PARENT_ID"))
    private Category parent;

    @Column(name = "parent_id", insertable = false, updatable = false)
    private Long parentId;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    public static Category createCategory(Category parent, int sortOrder, String name) {
        Category c = new Category();
        c.parent = parent;
        c.sortOrder = sortOrder;
        c.name = name;
        return c;
    }

    /**
     * 카테고리명 수정
     *
     * @param newName 새로운 카테고리명
     */
    public void updateName(String newName) {
        this.name = newName;
    }

    /**
     * 대분류가 동일한 카테고리 내에서 순서 변경
     *
     * @param newSortOrder 새로운 카테고리 순서
     */
    public void updateSortOrder(int newSortOrder) {
        this.sortOrder = newSortOrder;
    }

    /**
     * 대분류 카테고리 변경
     *
     * @param newParent 새로운 대분류
     */
    public void updateParent(Category newParent) {
        this.parent = newParent;
    }
}


