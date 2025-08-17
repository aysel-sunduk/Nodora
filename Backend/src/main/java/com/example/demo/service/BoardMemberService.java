package com.example.demo.service;

import com.example.demo.model.board_members.BoardMember;
import java.util.List;

public interface BoardMemberService {

    // <<< GÜNCELLENMİŞ METOT İMZASI >>>
    void addMemberToBoard(Integer boardId, Integer memberId);

    void removeMemberFromBoard(Integer boardId, Integer memberId);

    List<BoardMember> getBoardMembers(Integer boardId);
}