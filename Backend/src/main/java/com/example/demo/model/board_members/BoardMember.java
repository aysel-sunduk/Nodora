package com.example.demo.model.board_members;

import jakarta.persistence.*;
import com.example.demo.model.members.Member;
import com.example.demo.model.workspace_members.WorkspaceMember;
import com.example.demo.model.boards.Boards;

@Entity
@Table(name = "board_members")
public class BoardMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_member_id")
    private Integer boardMemberId;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "workspace_member_id", referencedColumnName = "workspace_member_id", nullable = false)
    private WorkspaceMember workspaceMember;

    @ManyToOne
    @JoinColumn(name = "board_id", referencedColumnName = "board_id", nullable = false)
    private Boards board;

    @Column(name = "role_id", nullable = false)
    private Integer roleId; // 3: Leader, 4: Member

    public Integer getBoardMemberId() {
        return boardMemberId;
    }

    public void setBoardMemberId(Integer boardMemberId) {
        this.boardMemberId = boardMemberId;
    }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public WorkspaceMember getWorkspaceMember() {
        return workspaceMember;
    }

    public void setWorkspaceMember(WorkspaceMember workspaceMember) {
        this.workspaceMember = workspaceMember;
    }

    public Boards getBoard() {
        return board;
    }

    public void setBoard(Boards board) {
        this.board = board;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
} 