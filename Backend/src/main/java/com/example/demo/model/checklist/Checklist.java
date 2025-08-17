package com.example.demo.model.checklist;

import jakarta.persistence.*;

@Entity
@Table(name = "checklist")
public class Checklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checklist_id")
    private Integer checklistId;

    @Column(name = "title", length = 15, nullable = false)
    private String title;

    @Column(name = "card_id", nullable = false)
    private Integer cardId;

    @Column(name = "position", nullable = false)
    private Integer position;

    // Getter ve Setter'lar
    public Integer getChecklistId() {
        return checklistId;
    }

    public void setChecklistId(Integer checklistId) {
        this.checklistId = checklistId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCardId() {
        return cardId;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
}
}