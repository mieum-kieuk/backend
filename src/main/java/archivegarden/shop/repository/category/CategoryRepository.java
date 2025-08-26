package archivegarden.shop.repository.category;

import archivegarden.shop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByParentIdAndName(Long parentId, String name);

    @Query("""
                SELECT c
                FROM Category c
                ORDER BY 
                    CASE WHEN c.parent IS NULL THEN 0 ELSE 1 END,
                    c.parentId,       
                    c.sortOrder ASC,
                    c.id ASC
            """)
    List<Category> findCategories();

    @Query("""
                SELECT COALESCE(MAX(c.sortOrder), -1) + 1
                FROM Category c
                WHERE (:parentId IS NULL AND c.parentId IS NULL)
                   OR (:parentId IS NOT NULL AND c.parentId = :parentId)
            """)
    int findNextOrder(@Param("parentId") Long parentId);

    @Query("""
              SELECT c
              FROM Category c
              WHERE (:parentId IS NULL AND c.parentId IS NULL)
                 OR (:parentId IS NOT NULL AND c.parentId = :parentId)
              ORDER BY c.sortOrder ASC, c.id ASC
            """)
    List<Category> findSiblings(@Param("parentId") Long parentId);

    @Modifying
    @Query("""
                UPDATE Category c
                SET c.sortOrder = c.sortOrder - 1
                WHERE ((:parentId IS NULL AND c.parentId IS NULL) OR (:parentId IS NOT NULL AND c.parentId = :parentId))
                    AND c.sortOrder BETWEEN :from AND :to
            """)
    int bulkDecrementSortOrder(@Param("parentId") Long parentId, @Param("from") int from, @Param("to") int to);

    @Modifying
    @Query("""
                UPDATE Category c
                   SET c.sortOrder = c.sortOrder + 1
                 WHERE ((:parentId IS NULL AND c.parentId IS NULL) OR (:parentId IS NOT NULL AND c.parentId = :parentId))
                   AND c.sortOrder BETWEEN :from AND :to
            """)
    int bulkIncrementSortOrder(@Param("parentId") Long parentId, @Param("from") int from, @Param("to") int to);
}
