package com.example.demo.model.workspace_members;

import com.example.demo.model.members.Member;
import com.example.demo.model.roles.Roles;
import com.example.demo.model.workspaces.Workspaces;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "workspace_members")
public class WorkspaceMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_member_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "workspace_id",nullable = true)
    private Workspaces workspace;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Roles role;


    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public WorkspaceMember() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Workspaces getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspaces workspace) {
        this.workspace = workspace;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "WorkspaceMember{" +
                "id=" + id +
                ", workspace=" + workspace +
                ", member=" + member +
                ", role=" + role +
                ", createdAt=" + createdAt +
                '}';
    }
}