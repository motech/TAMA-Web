package org.motechproject.tama.web.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.joda.time.LocalDate;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.ivr.repository.AllCallLogs;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.refdata.repository.AllIVRLanguages;
import org.motechproject.tama.web.resportbuilder.CallLogReportBuilder;
import org.motechproject.tama.web.resportbuilder.abstractbuilder.ReportBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallLogExcelReportService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private AllCallLogs allCallLogs;
    private AllClinics allClinics;
    private AllPatients allPatients;
    private AllIVRLanguages allIVRLanguages;

    @Autowired
    public CallLogExcelReportService(AllCallLogs allCallLogs, AllClinics allClinics, AllPatients allPatients, AllIVRLanguages allIVRLanguages) {
        this.allCallLogs = allCallLogs;
        this.allClinics = allClinics;
        this.allPatients = allPatients;
        this.allIVRLanguages = allIVRLanguages;
    }

    public HSSFWorkbook buildReport(LocalDate startDate, LocalDate endDate) {
        AllCallLogSummaries allCallLogSummaries = new AllCallLogSummaries(allCallLogs, allPatients, allClinics, allIVRLanguages);
        CallLogReportBuilder callLogReportBuilder = new CallLogReportBuilder(allCallLogSummaries, startDate, endDate);
        return createExcelReport(callLogReportBuilder);
    }

    protected HSSFWorkbook createExcelReport(ReportBuilder reportBuilder) {
        try {
            return reportBuilder.getExcelWorkbook();
        } catch (Exception e) {
            logger.error("Error while generating excel report: " + e.getMessage());
        }
        return null;
    }
}
