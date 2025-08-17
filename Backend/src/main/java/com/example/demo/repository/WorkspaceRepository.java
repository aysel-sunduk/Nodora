package com.example.demo.repository;

import com.example.demo.model.workspaces.Workspaces;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspaces, Integer> {
    List<Workspaces> findByMemberId(Integer memberId);
    
    /**
     * Toplam workspace sayısını getir
     */
    @Query("SELECT COUNT(w) FROM Workspaces w")
    Long countAllWorkspaces();
}
