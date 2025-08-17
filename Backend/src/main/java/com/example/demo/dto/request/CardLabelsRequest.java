package com.example.demo.dto.request;

public class CardLabelsRequest {
    private Integer labelId;
    private Integer cardId;
    private Integer memberId;
    private String cardLabelName;

    public Integer getLabelId() {return labelId;}

    public void setLabelId(Integer labelId) {this.labelId = labelId;}

    public Integer getCardId() {return cardId;}

    public void setCardId(Integer cardId) {this.cardId = cardId;}

    public Integer getMemberId() {return memberId;}

    public void setMemberId(Integer memberId) {this.memberId = memberId;}

    public String getCardLabelName() {return cardLabelName;}

    public void setCardLabelName(String cardLabelName) {this.cardLabelName = cardLabelName;}
}