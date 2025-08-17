package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;


public class LabelsRequest {

    @NotNull(message = "Board ID cannot be null")
    private Integer boardId;

    @NotBlank(message = "Label name cannot be blank")
    @Size(max = 15, message = "Label name max 15 characters")
    private String labelName;

    @NotBlank(message = "Color cannot be blank")
    @Size(max = 10, message = "Color max 10 characters")
    private String color;


    @JsonProperty("memberId")
    @NotNull(message = "Member ID cannot be null")
    private Integer memberId;

    // Getters & Setters
    public Integer getBoardId() { return boardId; }
    public void setBoardId(Integer boardId) { this.boardId = boardId; }

    public String getLabelName() { return labelName; }
    public void setLabelName(String labelName) { this.labelName = labelName; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color=color;}

    public Integer getMemberId() { return memberId; }
    public void setMemberId(Integer memberId) { this.memberId = memberId; }

}