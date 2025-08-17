package com.example.demo.model.checklist_items;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "checklist_items")
public class ChecklistItems {
    // Getter ve Setter'lar
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checklist_items_id")
    private Integer checklistItemsId;

    @Column(name = "checklist_id", nullable = false)
    private Integer checklistId;

    @Column(name = "text", length = 25, nullable = false)
    private String text;

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted;

    @Column(name = "position", nullable = false)
    private Integer position;

}