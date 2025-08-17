package com.example.demo.model.card_members;

import jakarta.persistence.*;

@Entity
@Table(name = "card_members", schema = "public")
public class Card_Members {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_member_id")
    private Integer cardMemberId;

    @Column(name = "card_id", nullable = false)
    private Integer cardId;

    @Column(name = "member_id", nullable = false)  //
    private Integer memberId;  //

    // Getter ve Setter metodları
    public Integer getCardMemberId() {
        return cardMemberId;
    }

    public void setCardMemberId(Integer cardMemberId) {
        this.cardMemberId = cardMemberId;
    }

    public Integer getCardId() {
        return cardId;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
}
}