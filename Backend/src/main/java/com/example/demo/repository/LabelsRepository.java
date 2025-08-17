package com.example.demo.repository;

import com.example.demo.model.labels.Labels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabelsRepository extends JpaRepository<Labels,Integer>{
        }