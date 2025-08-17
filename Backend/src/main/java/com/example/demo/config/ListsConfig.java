// src/main/java/com/example/demo/config/ListsConfig.java

package com.example.demo.config;

import com.example.demo.model.lists.Lists;

// Lists entity'si ile DTO arasında manuel dönüşüm sağlayan yardımcı config sınıfı
public class ListsConfig {
    // Entity'den DTO'ya dönüştürme
    public static ListsDTO toDTO(Lists entity) {
        if (entity == null) return null;
        return new ListsDTO(
                entity.getListId(),
                entity.getTitle(),
                entity.getPosition(), // <<< Integer olarak kullanıldı
                entity.getBoardId()
        );
    }

    // DTO'dan Entity'ye dönüştürme
    public static Lists toEntity(ListsDTO dto) {
        if (dto == null) return null;
        Lists lists = new Lists();
        lists.setListId(dto.getListId());
        lists.setTitle(dto.getTitle());
        lists.setPosition(dto.getPosition()); // <<< Integer olarak kullanıldı
        lists.setBoardId(dto.getBoardId());
        return lists;
    }

    // Lists tablosu için sade DTO (Data Transfer Object) sınıfı
    public static class ListsDTO {
        private Integer listId;
        private String title;
        private Integer position; // <<< String'den Integer'a dönüştürüldü
        private Integer boardId;

        public ListsDTO() {}

        // Tüm alanları içeren constructor
        public ListsDTO(Integer listId, String title, Integer position, Integer boardId) { // <<< position tipi güncellendi
            this.listId = listId;
            this.title = title;
            this.position = position;
            this.boardId = boardId;
        }

        // Getter ve setter metotları
        public Integer getListId() { return listId; }
        public void setListId(Integer listId) { this.listId = listId; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public Integer getPosition() { return position; } // <<< Getter tipi güncellendi
        public void setPosition(Integer position) { this.position = position; } // <<< Setter tipi güncellendi

        public Integer getBoardId() { return boardId; }
        public void setBoardId(Integer boardId) { this.boardId = boardId;}
    }
}