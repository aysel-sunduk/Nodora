package com.example.demo.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CardRequest {
    private String title;
    private String description;
    private LocalDate endingDate;
    private Integer listId;
    private Integer position;
    private Integer memberId;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public java.time.LocalDate getEndingDate() { return endingDate; }
    public void setEndingDate(java.time.LocalDate endingDate) { this.endingDate = endingDate; }
    public Integer getListId() { return listId; }
    public void setListId(Integer listId) { this.listId = listId; }
    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }
    public Integer getMemberId() { return memberId; }
    public void setMemberId(Integer memberId) { this.memberId = memberId; }
}