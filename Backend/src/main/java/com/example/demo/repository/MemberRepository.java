package com.example.demo.repository;

import com.example.demo.model.members.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {

    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);
    
    /**
     * Aktif kullanıcı sayısını getir
     */
    @Query("SELECT COUNT(m) FROM Member m WHERE m.membersActive = true")
    Long countActiveUsers();
}
