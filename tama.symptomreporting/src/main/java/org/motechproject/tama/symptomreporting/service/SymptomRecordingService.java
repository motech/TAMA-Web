package org.motechproject.tama.symptomreporting.service;

import org.joda.time.DateTime;
import org.motechproject.tama.symptomreporting.domain.SymptomReport;
import org.motechproject.tama.symptomreporting.repository.AllSymptomReports;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Properties;

@Component
public class SymptomRecordingService {

    private AllSymptomReports allSymptomReports;

    @Autowired
    public SymptomRecordingService(AllSymptomReports allSymptomReports) {
        this.allSymptomReports = allSymptomReports;
    }

    public SymptomReport save(String symptomId, String patientDocId, String callId, DateTime reportedAt) {
        SymptomReport symptomReport = new SymptomReport(patientDocId, callId);
        symptomReport.setSymptomIds(Arrays.asList(symptomId));
        symptomReport.setReportedAt(reportedAt);
        return allSymptomReports.insertOrMerge(symptomReport);
    }

}
