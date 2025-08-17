package com.example.demo.model.cards;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Integer cardId;

    @Column(name = "title", nullable = false, length = 15)
    private String title;

    @Column(name = "description", length = 25)
    private String description;

    @Column(name = "ending_date")
    private LocalDate endingDate;

    @Column(name = "list_id", nullable = false)
    private Integer listId;

    @Column(name = "position", nullable = false)
    private Integer position;

    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    // Getters and Setters

    public Integer getCardId() {
        return cardId;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(LocalDate endingDate) {
        this.endingDate = endingDate;
    }

    public Integer getListId() {
        return listId;
    }

    public void setListId(Integer listId) {
        this.listId = listId;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
   }
}