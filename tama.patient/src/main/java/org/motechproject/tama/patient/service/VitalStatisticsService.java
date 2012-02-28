package org.motechproject.tama.patient.service;

import org.joda.time.LocalDate;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.patient.repository.AllVitalStatistics;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VitalStatisticsService {

    private AllVitalStatistics allVitalStatistics;

    @Autowired
    public VitalStatisticsService(AllVitalStatistics allVitalStatistics) {
        this.allVitalStatistics = allVitalStatistics;
    }

    public List<VitalStatistics> getAllFor(String patientId, int rangeInMonths) {
        LocalDate endDate = DateUtil.today();
        LocalDate startDate= endDate.minusMonths(rangeInMonths);

        return allVitalStatistics.findAllByPatientId(patientId, startDate, endDate);
    }
}
