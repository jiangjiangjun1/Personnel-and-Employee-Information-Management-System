package com.fzgs.personnel.service;

import com.fzgs.personnel.model.DashboardSummary;
import com.fzgs.personnel.repository.DashboardRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final DashboardRepository repository;

    public DashboardService(DashboardRepository repository) {
        this.repository = repository;
    }

    public DashboardSummary summary() {
        return repository.summary();
    }
}
