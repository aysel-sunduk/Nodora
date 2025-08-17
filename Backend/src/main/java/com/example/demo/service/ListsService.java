package com.example.demo.service;

import com.example.demo.dto.request.ListsRequest;
import com.example.demo.dto.response.ListsResponse;
import com.example.demo.model.lists.Lists;
import com.example.demo.model.members.Member;
import com.example.demo.repository.ListsRepository;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListsService {

    private final ListsRepository listsRepository;
    private final MemberRepository memberRepository;

    // BoardId'ye göre listeleri getirir
    public List<ListsResponse> getListsByBoardId(Integer boardId) {
        List<Lists> lists = listsRepository.findByBoardIdOrderByPosition(boardId);
        return lists.stream().map(this::toResponseDto).collect(Collectors.toList());
    }

    // ID'ye göre bir listeyi getirir (response DTO)
    public ListsResponse getListById(Integer id) { // Metot adı düzeltildi
        Optional<Lists> optional = listsRepository.findById(id);
        return optional.map(this::toResponseDto).orElse(null);
    }

    // Yeni bir liste oluşturur
    public ListsResponse createList(ListsRequest listsRequest) { // Metot adı düzeltildi
        Lists lists = new Lists();
        lists.setTitle(listsRequest.getTitle());
        lists.setPosition(listsRequest.getPosition());
        lists.setBoardId(listsRequest.getBoardId());
        lists.setMemberId(getCurrentMemberId());

        Lists saved = listsRepository.save(lists);
        return toResponseDto(saved);
    }

    // Var olan bir listeyi günceller
    public ListsResponse updateList(Integer id, ListsRequest listsRequest) { // Metot adı düzeltildi
        Optional<Lists> optional = listsRepository.findById(id);
        if (optional.isPresent()) {
            Lists existing = optional.get();
            existing.setTitle(listsRequest.getTitle());
            existing.setPosition(listsRequest.getPosition());
            // BoardId'nin güncellenmesine izin vermeyebiliriz, bu yüzden bu satır yorum satırı yapıldı
            // existing.setBoardId(listsRequest.getBoardId());
            existing.setMemberId(getCurrentMemberId());

            Lists updated = listsRepository.save(existing);
            return toResponseDto(updated);
        } else {
            return null;
        }
    }

    // Belirli bir listeyi siler
    public boolean deleteList(Integer id) {
        if (listsRepository.existsById(id)) {
            listsRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    // Entity'den Response DTO'ya dönüşüm
    private ListsResponse toResponseDto(Lists entity) {
        ListsResponse dto = new ListsResponse();
        dto.setListId(entity.getListId()); // Alan adı düzeltildi
        dto.setTitle(entity.getTitle());
        dto.setBoardId(entity.getBoardId());
        dto.setPosition(entity.getPosition());
        dto.setMemberId(entity.getMemberId()); // Alan adı düzeltildi
        return dto;
    }

    // Yardımcı metot: Güvenlik bağlamından üye ID'sini alır.
    private Integer getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        return member.getMemberId();
    }
}
