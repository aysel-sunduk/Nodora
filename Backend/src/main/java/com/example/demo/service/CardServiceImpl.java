package com.example.demo.service;

import com.example.demo.dto.request.CardRequest;
import com.example.demo.dto.response.CardResponse;
import com.example.demo.dto.response.ChecklistDetailResponseDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.cards.Card;
import com.example.demo.model.members.Member;
import com.example.demo.repository.CardRepository;
import com.example.demo.repository.ListsRepository;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final ListsRepository listsRepository;
    private final MemberRepository memberRepository;
    private final ChecklistService checklistService;

    @Override
    @Transactional
    public CardResponse createCard(CardRequest request) {
        listsRepository.findById(request.getListId())
                .orElseThrow(() -> new ResourceNotFoundException("Liste bulunamadı"));

        Card card = new Card();
        card.setTitle(request.getTitle());
        card.setDescription(request.getDescription());
        card.setEndingDate(request.getEndingDate());
        card.setListId(request.getListId());
        card.setPosition(request.getPosition());
        card.setMemberId(getCurrentMemberId());

        Card savedCard = cardRepository.save(card);
        return toResponse(savedCard);
    }

    @Override
    @Transactional(readOnly = true)
    public CardResponse getCardResponseById(Integer id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kart bulunamadı"));
        return toResponse(card);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardResponse> getCardsByListId(Integer listId) {
        listsRepository.findById(listId)
                .orElseThrow(() -> new ResourceNotFoundException("Liste bulunamadı"));

        List<Card> cards = cardRepository.findByListIdOrderByPosition(listId);

        return cards.stream().map(card -> {
            CardResponse response = toResponse(card);

            // Checklist'leri item'larıyla birlikte çekme
            List<ChecklistDetailResponseDTO> checklistsWithItems = checklistService.getChecklistsWithItemsByCardId(card.getCardId());
            response.setChecklists(checklistsWithItems);

            return response;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CardResponse updateCard(Integer id, CardRequest request) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kart bulunamadı"));

        listsRepository.findById(request.getListId())
                .orElseThrow(() -> new ResourceNotFoundException("Liste bulunamadı"));

        card.setTitle(request.getTitle());
        card.setDescription(request.getDescription());
        card.setEndingDate(request.getEndingDate());
        card.setListId(request.getListId());
        card.setPosition(request.getPosition());

        Card updatedCard = cardRepository.save(card);
        return toResponse(updatedCard);
    }

    @Override
    @Transactional
    public void deleteCard(Integer id) {
        if (!cardRepository.existsById(id)) {
            throw new ResourceNotFoundException("Kart bulunamadı");
        }
        cardRepository.deleteById(id);
    }

    private CardResponse toResponse(Card card) {
        CardResponse response = new CardResponse();
        response.setCardId(card.getCardId());
        response.setTitle(card.getTitle());
        response.setDescription(card.getDescription());
        response.setEndingDate(card.getEndingDate()); // Burası artık doğru tipte olacak
        response.setListId(card.getListId());
        response.setPosition(card.getPosition());
        response.setMemberId(card.getMemberId());
        return response;
    }

    private Integer getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        return member.getMemberId();
    }
}