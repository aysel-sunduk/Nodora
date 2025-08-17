package com.example.demo.repository;

import com.example.demo.model.checklist_items.ChecklistItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional; // 🔥 Önemli: Modifying için gerekli
import java.util.List;
import java.util.Optional;

@Repository
public interface ChecklistItemRepository extends JpaRepository<ChecklistItems, Integer> {

    List<ChecklistItems> findByChecklistIdOrderByPositionAsc(Integer checklistId);

    boolean existsByChecklistId(Integer checklistId);

    int countByChecklistId(Integer checklistId);

    int countByChecklistIdAndIsCompletedTrue(Integer checklistId);

    @Query("SELECT MAX(ci.position) FROM ChecklistItems ci WHERE ci.checklistId = :checklistId")
    Optional<Integer> findMaxPositionByChecklistId(@Param("checklistId") Integer checklistId);

    @Modifying
    @Transactional
    @Query("UPDATE ChecklistItems ci SET ci.isCompleted = NOT ci.isCompleted WHERE ci.checklistItemsId = :itemId")
    int toggleCompletion(@Param("itemId") Integer itemId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ChecklistItems ci WHERE ci.checklistId = :checklistId")
    int deleteByChecklistId(@Param("checklistId") Integer checklistId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ChecklistItems ci WHERE ci.checklistId = :checklistId AND ci.isCompleted = true")
    int deleteByChecklistIdAndIsCompletedTrue(@Param("checklistId") Integer checklistId);
}
