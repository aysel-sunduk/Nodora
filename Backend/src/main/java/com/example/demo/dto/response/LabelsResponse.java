package com.example.demo.dto.response;

public class LabelsResponse {

    private Integer labelId;
    private Integer boardId;
    private String labelName;
    private String color;

    // Getters & Setters
    public Integer getLabelId() { return labelId; }
    public void setLabelId(Integer labelId) { this.labelId = labelId; }

    public Integer getBoardId() { return boardId; }
    public void setBoardId(Integer boardId) { this.boardId = boardId; }

    public String getLabelName() { return labelName; }
    public void setLabelName(String labelName) { this.labelName = labelName; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color=color;}
}