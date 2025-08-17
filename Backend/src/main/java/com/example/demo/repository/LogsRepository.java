package com.example.demo.repository;

import com.example.demo.model.logs.Logs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogsRepository extends JpaRepository<Logs, Long> {

    /**
     * Kaynağa (source) göre log kayıtlarını listeler.
     * Spring Data JPA bu metot adından yola çıkarak sorguyu otomatik oluşturur.
     * Örnek: SELECT * FROM logs WHERE source = ?
     *
     * @param source Log kaynağı
     * @return Belirtilen kaynağa ait log kayıtlarının listesi
     */
    List<Logs> findBySource(String source);

    /**
     * Log seviyesine göre log kayıtlarını listeler.
     *
     * @param logLevel Log seviyesi (INFO, WARN, ERROR, vb.)
     * @return Belirtilen seviyeye ait log kayıtlarının listesi
     */
    List<Logs> findByLogLevel(String logLevel);

    /**
     * Belirli bir üye ID'sine (memberId) ait log kayıtlarını listeler.
     *
     * @param memberId Üye ID'si
     * @return Belirtilen üyeye ait log kayıtlarının listesi
     */
    List<Logs> findByMemberId(Integer memberId);

    /**
     * Belirli bir üye ID'sine ve log seviyesine ait log kayıtlarını listeler.
     *
     * @param memberId Üye ID'si
     * @param logLevel Log seviyesi
     * @return Üye ID ve log seviyesine göre filtrelenmiş log kayıtlarının listesi
     */
    List<Logs> findByMemberIdAndLogLevel(Integer memberId, String logLevel);

}