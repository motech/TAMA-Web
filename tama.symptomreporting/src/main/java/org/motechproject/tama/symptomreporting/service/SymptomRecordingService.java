package org.motechproject.tama.symptomreporting.service;

import org.joda.time.DateTime;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.symptomreporting.domain.SymptomReport;
import org.motechproject.tama.symptomreporting.repository.AllSymptomReports;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SymptomRecordingService {

    private AllSymptomReports allSymptomReports;

    @Autowired
    public SymptomRecordingService(AllSymptomReports allSymptomReports) {
        this.allSymptomReports = allSymptomReports;
    }

    public SymptomReport save(String symptomId, String patientDocId, String callId, DateTime reportedAt) {
        SymptomReport symptomReport = allSymptomReports.findByCallId(callId);
        if (symptomReport == null) {
            symptomReport = new SymptomReport(patientDocId, callId, reportedAt);
        }
        symptomReport.addSymptomId(symptomId);
        symptomReport.setReportedAt(reportedAt);
        return allSymptomReports.addOrReplace(symptomReport);
    }

    public void saveAdviceGiven(String patientDocId, String callId, String advice) {
        SymptomReport symptomReport = allSymptomReports.findByCallId(callId);
        if (symptomReport == null) {
            symptomReport = new SymptomReport(patientDocId, callId, DateUtil.now());
        }
        symptomReport.setAdviceGiven(advice);
        allSymptomReports.addOrReplace(symptomReport);
    }

    public void setAsNotConnectedToDoctor(String callId) {
        final SymptomReport symptomReport = allSymptomReports.findByCallId(callId);
        symptomReport.setDoctorContacted(TAMAConstants.ReportedType.No);
        allSymptomReports.update(symptomReport);
    }

    public void setAsConnectedToDoctor(String callId) {
        final SymptomReport symptomReport = allSymptomReports.findByCallId(callId);
        symptomReport.setDoctorContacted(TAMAConstants.ReportedType.Yes);
        allSymptomReports.update(symptomReport);
    }

}
