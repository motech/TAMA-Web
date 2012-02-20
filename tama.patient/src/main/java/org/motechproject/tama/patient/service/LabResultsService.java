package org.motechproject.tama.patient.service;

import org.motechproject.tama.patient.domain.LabResult;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabResultsService {

    AllLabResults allLabResults;

    @Autowired
    public LabResultsService(AllLabResults allLabResults) {
        this.allLabResults = allLabResults;
    }

    public List<LabResult> listCD4Counts(String patientId, int rangeInMonths) {
        return allLabResults.findCD4LabResultsFor(patientId, rangeInMonths);
    }
}
