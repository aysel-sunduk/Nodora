package com.example.demo.model.lists;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "lists")
public class Lists {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "list_id")
    private Integer listId;

    @Column(name = "title", length = 15)
    private String title;

    @Column(name = "position")
    private Integer position;

    @Column(name = "board_id")
    private Integer boardId;

    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    public Integer getMemberId() { return memberId; }
    public void setMemberId(Integer memberId) { this.memberId = memberId; }

    // Constructors
    public Lists() {
    }

    public Lists(Integer listId, String title, Integer position, Integer boardId) {
        this.listId = listId;
        this.title = title;
        this.position = position;
        this.boardId = boardId;
    }

    // Getters and Setters
    public Integer getListId() {
        return listId;
    }

    public void setListId(Integer listId) {
        this.listId = listId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getBoardId() {
        return boardId;
    }

    public void setBoardId(Integer boardId) {
        this.boardId = boardId;
    }
}