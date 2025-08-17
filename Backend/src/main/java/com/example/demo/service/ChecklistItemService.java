package com.example.demo.service;

import com.example.demo.dto.request.ChecklistItemRequestDTO;
import com.example.demo.dto.response.ChecklistItemResponseDTO;
import com.example.demo.exception.EntityNotFoundException;
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
public class ChecklistItemService {

    private final ChecklistItemRepository checklistItemRepository;
    private final ChecklistRepository checklistRepository;

    @Autowired
    public ChecklistItemService(ChecklistItemRepository checklistItemRepository,
                                ChecklistRepository checklistRepository) {
        this.checklistItemRepository = checklistItemRepository;
        this.checklistRepository = checklistRepository;
    }

    public ChecklistItemResponseDTO createItem(ChecklistItemRequestDTO requestDTO) {
        if (!checklistRepository.existsById(requestDTO.getChecklistId())) {
            throw new EntityNotFoundException("Checklist", requestDTO.getChecklistId());
        }

        Integer position = requestDTO.getPosition();
        if (position == null) {
            position = checklistItemRepository.findMaxPositionByChecklistId(requestDTO.getChecklistId())
                    .orElse(0) + 1;
        }

        ChecklistItems item = new ChecklistItems();
        item.setChecklistId(requestDTO.getChecklistId());
        item.setText(requestDTO.getText());
        item.setIsCompleted(requestDTO.getIsCompleted() != null ? requestDTO.getIsCompleted() : false);
        item.setPosition(position);

        ChecklistItems savedItem = checklistItemRepository.save(item);
        return convertToResponseDTO(savedItem);
    }

    @Transactional(readOnly = true)
    public ChecklistItemResponseDTO getItemById(Integer itemId) {
        ChecklistItems item = checklistItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("ChecklistItem", itemId));
        return convertToResponseDTO(item);
    }

    @Transactional(readOnly = true)
    public List<ChecklistItemResponseDTO> getItemsByChecklistId(Integer checklistId) {
        if (!checklistRepository.existsById(checklistId)) {
            throw new EntityNotFoundException("Checklist", checklistId);
        }
        List<ChecklistItems> items = checklistItemRepository.findByChecklistIdOrderByPositionAsc(checklistId);
        return items.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public ChecklistItemResponseDTO updateItem(Integer itemId, ChecklistItemRequestDTO requestDTO) {
        ChecklistItems item = checklistItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("ChecklistItem", itemId));
        if (!checklistRepository.existsById(requestDTO.getChecklistId())) {
            throw new EntityNotFoundException("Checklist", requestDTO.getChecklistId());
        }

        item.setChecklistId(requestDTO.getChecklistId());
        item.setText(requestDTO.getText());
        item.setIsCompleted(requestDTO.getIsCompleted());

        if (requestDTO.getPosition() != null) {
            item.setPosition(requestDTO.getPosition());
        }
        ChecklistItems updatedItem = checklistItemRepository.save(item);
        return convertToResponseDTO(updatedItem);
    }

    public ChecklistItemResponseDTO toggleCompletion(Integer itemId) {
        if (!checklistItemRepository.existsById(itemId)) {
            throw new EntityNotFoundException("ChecklistItem", itemId);
        }

        int updatedRows = checklistItemRepository.toggleCompletion(itemId);
        if (updatedRows == 0) {
            throw new EntityNotFoundException("ChecklistItem", itemId);
        }

        ChecklistItems updatedItem = checklistItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("ChecklistItem", itemId));
        return convertToResponseDTO(updatedItem);
    }

    public ChecklistItemResponseDTO updateText(Integer itemId, String newText) {
        if (newText == null || newText.trim().isEmpty()) {
            throw new IllegalArgumentException("Text cannot be null or empty");
        }
        if (newText.length() > 25) {
            throw new IllegalArgumentException("Text cannot be longer than 25 characters");
        }
        ChecklistItems item = checklistItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("ChecklistItem", itemId));
        item.setText(newText.trim());
        ChecklistItems updatedItem = checklistItemRepository.save(item);
        return convertToResponseDTO(updatedItem);
    }

    public ChecklistItemResponseDTO updatePosition(Integer itemId, Integer newPosition) {
        ChecklistItems item = checklistItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("ChecklistItem", itemId));
        item.setPosition(newPosition);
        ChecklistItems updatedItem = checklistItemRepository.save(item);
        return convertToResponseDTO(updatedItem);
    }

    public void deleteItem(Integer itemId) {
        if (!checklistItemRepository.existsById(itemId)) {
            throw new EntityNotFoundException("ChecklistItem", itemId);
        }
        checklistItemRepository.deleteById(itemId);
    }

    private ChecklistItemResponseDTO convertToResponseDTO(ChecklistItems item) {
        ChecklistItemResponseDTO dto = new ChecklistItemResponseDTO();
        dto.setChecklistItemsId(item.getChecklistItemsId());
        dto.setChecklistId(item.getChecklistId());
        dto.setText(item.getText());
        dto.setIsCompleted(item.getIsCompleted());
        dto.setPosition(item.getPosition());
        return dto;
    }
}