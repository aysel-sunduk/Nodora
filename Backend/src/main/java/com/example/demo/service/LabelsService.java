package com.example.demo.service;

import com.example.demo.dto.request.LabelsRequest;
import com.example.demo.dto.response.LabelsResponse;

import java.util.List;

public interface LabelsService {

    LabelsResponse createLabel(LabelsRequest request);

    List<LabelsResponse> getAllLabels();

    LabelsResponse getLabelById(Integer id);

    void deleteLabel(Integer id);
}