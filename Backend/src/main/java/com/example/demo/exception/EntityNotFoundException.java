package com.example.demo.exception;

/**
 * Entity bulunamadığında fırlatılan özel exception sınıfı
 * Veritabanında aradığımız entity'nin mevcut olmadığını belirtir
 * RESTful API'lerde 404 Not Found response'u döndürmek için kullanılır
 *
 * @author Nodora Team
 * @version 1.0
 */
public class EntityNotFoundException extends RuntimeException {

    private final String entityName;
    private final Object entityId;

    /**
     * Entity adı ve ID ile exception oluşturur
     *
     * @param entityName Bulunamayan entity'nin adı (örn: "User", "Card", "Checklist")
     * @param entityId Bulunamayan entity'nin kimlik numarası
     */
    public EntityNotFoundException(String entityName, Object entityId) {
        super(String.format("%s with id '%s' not found", entityName, entityId));
        this.entityName = entityName;
        this.entityId = entityId;
    }

    /**
     * Sadece mesaj ile exception oluşturur
     *
     * @param message Hata mesajı
     */
    public EntityNotFoundException(String message) {
        super(message);
        this.entityName = "Unknown";
        this.entityId = null;
    }

    /**
     * Mesaj ve cause ile exception oluşturur
     *
     * @param message Hata mesajı
     * @param cause Hatanın nedeni olan exception
     */
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.entityName = "Unknown";
        this.entityId = null;
    }

    /**
     * Bulunamayan entity'nin adını döndürür
     *
     * @return Entity adı
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * Bulunamayan entity'nin ID'sini döndürür
     *
     * @return Entity ID'si
     */
    public Object getEntityId() {
        return entityId;
}
}