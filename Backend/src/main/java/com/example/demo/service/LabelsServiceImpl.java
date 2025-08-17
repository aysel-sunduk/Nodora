package com.example.demo.service;

import com.example.demo.dto.request.LabelsRequest;
import com.example.demo.dto.response.LabelsResponse;
import com.example.demo.exception.LabelNotFoundException;
import com.example.demo.model.boards.Boards;
import com.example.demo.model.labels.Labels;
import com.example.demo.model.members.Member;
import com.example.demo.repository.LabelsRepository;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.MemberRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LabelsServiceImpl implements LabelsService {

    private final LabelsRepository labelsRepository;
    private final BoardRepository boardsRepository;
    private final MemberRepository memberRepository; // ❗ Yeni eklendi

    public LabelsServiceImpl(LabelsRepository labelsRepository,
                             BoardRepository boardsRepository,
                             MemberRepository memberRepository) {
        this.labelsRepository = labelsRepository;
        this.boardsRepository = boardsRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public LabelsResponse createLabel(LabelsRequest request) {
        Boards board = boardsRepository.findById(request.getBoardId())
                .orElseThrow(() -> new LabelNotFoundException("Board not found"));

        Labels label = new Labels();
        label.setBoardId(board.getBoardId());
        label.setLabelName(request.getLabelName());
        label.setColor(request.getColor());

        // ❗ MemberId'yi request'ten almak yerine SecurityContextHolder'dan alıyoruz.
        label.setMemberId(getCurrentMemberId());

        Labels saved = labelsRepository.save(label);

        return mapToResponse(saved);
    }

    @Override
    public List<LabelsResponse> getAllLabels() {
        return labelsRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LabelsResponse getLabelById(Integer id) {
        Labels label = labelsRepository.findById(id)
                .orElseThrow(() -> new LabelNotFoundException("Label not found"));
        return mapToResponse(label);
    }

    @Override
    public void deleteLabel(Integer id) {
        Labels label = labelsRepository.findById(id)
                .orElseThrow(() -> new LabelNotFoundException("Label not found"));
        labelsRepository.delete(label);
    }

    private LabelsResponse mapToResponse(Labels label) {
        LabelsResponse response = new LabelsResponse();
        response.setLabelId(label.getLabelId());
        response.setBoardId(label.getBoardId());
        response.setLabelName(label.getLabelName());
        response.setColor(label.getColor());
        return response;
    }

    /**
     * Güvenlik bağlamından (SecurityContextHolder) o anki kullanıcının ID'sini alır.
     */
    private Integer getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        return member.getMemberId();
    }
}