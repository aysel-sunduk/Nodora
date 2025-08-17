package com.example.demo.service;

import com.example.demo.dto.request.ChecklistRequestDTO;
import com.example.demo.dto.response.ChecklistResponseDTO;
import com.example.demo.dto.response.ChecklistDetailResponseDTO;
import com.example.demo.dto.response.ChecklistItemResponseDTO;
import com.example.demo.dto.response.ChecklistProgressDTO;
import com.example.demo.exception.EntityNotFoundException;
import com.example.demo.model.checklist.Checklist;
import com.example.demo.model.checklist_items.ChecklistItems;
import com.example.demo.repository.ChecklistRepository;
import com.example.demo.repository.ChecklistItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChecklistService {

    private final ChecklistRepository checklistRepository;
    private final ChecklistItemRepository checklistItemRepository;

    @Autowired
    public ChecklistService(ChecklistRepository checklistRepository,
                            ChecklistItemRepository checklistItemRepository) {
        this.checklistRepository = checklistRepository;
        this.checklistItemRepository = checklistItemRepository;
    }

    /**
     * Yeni bir checklist oluşturur.
     * Eğer bir pozisyon belirtilmemişse, en yüksek pozisyonun bir fazlasını atar.
     * @param requestDTO Oluşturulacak checklist'in verilerini içeren DTO.
     * @return Oluşturulan checklist'in DTO'su.
     */
    public ChecklistResponseDTO createChecklist(ChecklistRequestDTO requestDTO) {
        Integer position = requestDTO.getPosition();
        if (position == null) {
            position = checklistRepository.findMaxPositionByCardId(requestDTO.getCardId())
                    .orElse(0) + 1;
        }

        Checklist checklist = new Checklist();
        checklist.setTitle(requestDTO.getTitle());
        checklist.setCardId(requestDTO.getCardId());
        checklist.setPosition(position);

        Checklist savedChecklist = checklistRepository.save(checklist);
        return convertToResponseDTO(savedChecklist);
    }

    /**
     * ID'ye göre bir checklist getirir.
     * @param checklistId Getirilecek checklist'in ID'si.
     * @return Bulunan checklist'in DTO'su.
     * @throws EntityNotFoundException Eğer belirtilen ID'de bir checklist bulunamazsa.
     */
    @Transactional(readOnly = true)
    public ChecklistResponseDTO getChecklistById(Integer checklistId) {
        Checklist checklist = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new EntityNotFoundException("Checklist", checklistId));
        return convertToResponseDTO(checklist);
    }

    /**
     * ID'ye göre bir checklist'i, item'ları ile birlikte detaylı olarak getirir.
     * @param checklistId Getirilecek checklist'in ID'si.
     * @return Bulunan checklist'in detaylı DTO'su.
     * @throws EntityNotFoundException Eğer belirtilen ID'de bir checklist bulunamazsa.
     */
    @Transactional(readOnly = true)
    public ChecklistDetailResponseDTO getChecklistWithDetails(Integer checklistId) {
        Checklist checklist = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new EntityNotFoundException("Checklist", checklistId));

        List<ChecklistItems> items = checklistItemRepository.findByChecklistIdOrderByPositionAsc(checklistId);

        ChecklistDetailResponseDTO detailDTO = new ChecklistDetailResponseDTO();
        detailDTO.setChecklistId(checklist.getChecklistId());
        detailDTO.setTitle(checklist.getTitle());
        detailDTO.setCardId(checklist.getCardId());
        detailDTO.setPosition(checklist.getPosition());

        List<ChecklistItemResponseDTO> itemDTOs = items.stream()
                .map(this::convertItemToResponseDTO)
                .collect(Collectors.toList());
        detailDTO.setItems(itemDTOs);

        return detailDTO;
    }

    /**
     * Bir karta ait tüm checklist'leri, içlerindeki item'larla birlikte döndürür.
     * @param cardId Checklist'leri çekilecek kartın ID'si.
     * @return Kart ID'sine ait checklist'lerin ve item'larının listesi.
     */
    @Transactional(readOnly = true)
    public List<ChecklistDetailResponseDTO> getChecklistsWithItemsByCardId(Integer cardId) {
        List<Checklist> checklists = checklistRepository.findByCardIdOrderByPositionAsc(cardId);
        return checklists.stream()
                .map(this::convertToDetailResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Bir karta ait tüm checklist'leri (item'sız) getirir.
     * @param cardId Checklist'leri çekilecek kartın ID'si.
     * @return Kart ID'sine ait checklist'lerin listesi.
     */
    @Transactional(readOnly = true)
    public List<ChecklistResponseDTO> getChecklistsByCardId(Integer cardId) {
        List<Checklist> checklists = checklistRepository.findByCardIdOrderByPositionAsc(cardId);
        return checklists.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Bir checklist'i günceller.
     * @param checklistId Güncellenecek checklist'in ID'si.
     * @param requestDTO Güncelleme verilerini içeren DTO.
     * @return Güncellenmiş checklist'in DTO'su.
     * @throws EntityNotFoundException Eğer belirtilen ID'de bir checklist bulunamazsa.
     */
    public ChecklistResponseDTO updateChecklist(Integer checklistId, ChecklistRequestDTO requestDTO) {
        Checklist checklist = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new EntityNotFoundException("Checklist", checklistId));

        checklist.setTitle(requestDTO.getTitle());
        checklist.setCardId(requestDTO.getCardId());

        if (requestDTO.getPosition() != null) {
            checklist.setPosition(requestDTO.getPosition());
        }

        Checklist updatedChecklist = checklistRepository.save(checklist);
        return convertToResponseDTO(updatedChecklist);
    }

    /**
     * Checklist'in pozisyonunu günceller.
     * @param checklistId Pozisyonu güncellenecek checklist'in ID'si.
     * @param newPosition Yeni pozisyon değeri.
     * @return Güncellenmiş checklist'in DTO'su.
     * @throws EntityNotFoundException Eğer belirtilen ID'de bir checklist bulunamazsa.
     */
    public ChecklistResponseDTO updatePosition(Integer checklistId, Integer newPosition) {
        Checklist checklist = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new EntityNotFoundException("Checklist", checklistId));
        checklist.setPosition(newPosition);
        Checklist updatedChecklist = checklistRepository.save(checklist);
        return convertToResponseDTO(updatedChecklist);
    }

    /**
     * Bir checklist'i ve tüm item'larını siler.
     * @param checklistId Silinecek checklist'in ID'si.
     * @throws EntityNotFoundException Eğer belirtilen ID'de bir checklist bulunamazsa.
     */
    public void deleteChecklist(Integer checklistId) {
        if (!checklistRepository.existsById(checklistId)) {
            throw new EntityNotFoundException("Checklist", checklistId);
        }
        checklistItemRepository.deleteByChecklistId(checklistId);
        checklistRepository.deleteById(checklistId);
    }

    /**
     * Bir checklist'in ilerleme durumunu (tamamlanma yüzdesi) hesaplar.
     * @param checklistId İlerlemesi hesaplanacak checklist'in ID'si.
     * @return İlerleme bilgilerini içeren DTO.
     * @throws EntityNotFoundException Eğer belirtilen ID'de bir checklist bulunamazsa.
     */
    @Transactional(readOnly = true)
    public ChecklistProgressDTO getChecklistProgress(Integer checklistId) {
        Checklist checklist = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new EntityNotFoundException("Checklist", checklistId));
        ChecklistProgressDTO progress = new ChecklistProgressDTO(checklistId, checklist.getTitle());
        long totalItems = checklistItemRepository.countByChecklistId(checklistId);
        long completedItems = checklistItemRepository.countByChecklistIdAndIsCompletedTrue(checklistId);
        progress.calculateProgress(totalItems, completedItems);
        return progress;
    }

    /**
     * Bir checklist'e ait tamamlanmış item'ları siler.
     * @param checklistId Tamamlanmış item'ları silinecek checklist'in ID'si.
     * @throws EntityNotFoundException Eğer belirtilen ID'de bir checklist bulunamazsa.
     */
    public void deleteCompletedItems(Integer checklistId) {
        if (!checklistRepository.existsById(checklistId)) {
            throw new EntityNotFoundException("Checklist", checklistId);
        }
        checklistItemRepository.deleteByChecklistIdAndIsCompletedTrue(checklistId);
    }

    /**
     * Checklist nesnesini item'ları ile birlikte DTO'ya dönüştürür.
     * Bu, CardService'de kullanılmak üzere detaylı bir dönüşüm sağlar.
     * @param checklist Dönüştürülecek checklist nesnesi.
     * @return Item'ları da içeren detaylı checklist DTO'su.
     */
    private ChecklistDetailResponseDTO convertToDetailResponseDTO(Checklist checklist) {
        List<ChecklistItems> items = checklistItemRepository.findByChecklistIdOrderByPositionAsc(checklist.getChecklistId());

        ChecklistDetailResponseDTO detailDTO = new ChecklistDetailResponseDTO();
        detailDTO.setChecklistId(checklist.getChecklistId());
        detailDTO.setTitle(checklist.getTitle());
        detailDTO.setCardId(checklist.getCardId());
        detailDTO.setPosition(checklist.getPosition());

        List<ChecklistItemResponseDTO> itemDTOs = items.stream()
                .map(this::convertItemToResponseDTO)
                .collect(Collectors.toList());
        detailDTO.setItems(itemDTOs);

        return detailDTO;
    }

    /**
     * Checklist nesnesini temel bir DTO'ya dönüştürür.
     * @param checklist Dönüştürülecek checklist nesnesi.
     * @return Temel checklist DTO'su.
     */
    private ChecklistResponseDTO convertToResponseDTO(Checklist checklist) {
        ChecklistResponseDTO dto = new ChecklistResponseDTO();
        dto.setChecklistId(checklist.getChecklistId());
        dto.setTitle(checklist.getTitle());
        dto.setCardId(checklist.getCardId());
        dto.setPosition(checklist.getPosition());
        return dto;
    }

    /**
     * ChecklistItem nesnesini DTO'ya dönüştürür.
     * @param item Dönüştürülecek item nesnesi.
     * @return Checklist item DTO'su.
     */
    private ChecklistItemResponseDTO convertItemToResponseDTO(ChecklistItems item) {
        ChecklistItemResponseDTO dto = new ChecklistItemResponseDTO();
        dto.setChecklistItemsId(item.getChecklistItemsId());
        dto.setChecklistId(item.getChecklistId());
        dto.setText(item.getText());
        dto.setIsCompleted(item.getIsCompleted());
        dto.setPosition(item.getPosition());
        return dto;
    }
}