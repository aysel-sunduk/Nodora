package com.example.demo.model.Membership; // Paket tanımı eklendi


import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "membership")
public class Membership implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    @Column(name = "board_id")
    private Integer boardId;

    @Column(name = "workspace_id", nullable = false)
    private Integer workspaceId;

    @Column(name = "role_id", nullable = false)
    private Integer roleId;

    // Constructors
    public Membership() {
    }

    public Membership(Integer memberId, Integer workspaceId, Integer roleId) {
        this.memberId = memberId;
        this.workspaceId = workspaceId;
        this.roleId = roleId;
    }

    public Membership(Integer memberId, Integer boardId, Integer workspaceId, Integer roleId) {
        this.memberId = memberId;
        this.boardId = boardId;
        this.workspaceId = workspaceId;
        this.roleId = roleId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getBoardId() {
        return boardId;
    }

    public void setBoardId(Integer boardId) {
        this.boardId = boardId;
    }

    public Integer getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Integer workspaceId) {
        this.workspaceId = workspaceId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    // toString method
    @Override
    public String toString() {
        return "Membership{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", boardId=" + boardId +
                ", workspaceId=" + workspaceId +
                ", roleId=" + roleId + '}';
        }
}