package com.example.demo.dto.request;

import lombok.Data;

@Data
public class ListsRequest {
    private String title;
    private Integer boardId;
    private Integer position;
}
