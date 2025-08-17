package com.example.demo.dto.response;

import lombok.Data;

@Data
public class ListsResponse {
    private Integer listId;
    private String title;
    private Integer boardId;
    private Integer position;
    private Integer memberId;
}
