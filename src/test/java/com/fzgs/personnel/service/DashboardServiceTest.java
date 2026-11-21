package com.fzgs.personnel.service;

import com.fzgs.personnel.model.DashboardSummary;
import com.fzgs.personnel.repository.DashboardRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DashboardServiceTest {

    @Test
    void returnsAllSummaryCounters() {
        DashboardRepository repository = new DashboardRepository(null) {
            @Override
            public DashboardSummary summary() {
                return new DashboardSummary(6, 5, 3, 2);
            }
        };

        DashboardSummary result = new DashboardService(repository).summary();

        assertThat(result).isEqualTo(new DashboardSummary(6, 5, 3, 2));
    }
}
