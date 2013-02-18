package org.motechproject.tama.reporting.service;

import org.motechproject.tama.reports.contract.HealthTipsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallLogReportingService {

    private HealthTipsReportingService healthTipsReportingService;

    @Autowired
    public CallLogReportingService(HealthTipsReportingService healthTipsReportingService) {
        this.healthTipsReportingService = healthTipsReportingService;
    }

    public void reportHealthTips(HealthTipsRequest request) {
        healthTipsReportingService.save(request);
    }
}
