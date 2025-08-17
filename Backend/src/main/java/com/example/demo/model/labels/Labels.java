package com.example.demo.model.labels;

import jakarta.persistence.*;

@Entity
@Table(name = "labels")
public class Labels {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "label_id")
    private Integer labelId;

    @Column(name = "board_id", nullable = false)
    private Integer boardId;

    @Column(name = "label_name", length = 15, nullable = false)
    private String labelName;

    @Column(name = "color", length = 10, nullable = false)
    private String color;

    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    public Integer getMemberId() { return memberId; }
    public void setMemberId(Integer memberId) { this.memberId = memberId; } 

    // Getter ve Setter'lar
    public Integer getLabelId() { return labelId; }
    public void setLabelId(Integer labelId) { this.labelId = labelId; }

    public Integer getBoardId() { return boardId; }
    public void setBoardId(Integer boardId) { this.boardId = boardId; }

    public String getLabelName() { return labelName; }
    public void setLabelName(String labelName) { this.labelName = labelName; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color=color;}
}