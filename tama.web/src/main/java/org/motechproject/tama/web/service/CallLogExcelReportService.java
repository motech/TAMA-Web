package org.motechproject.tama.web.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.joda.time.LocalDate;
import org.motechproject.tama.ivr.repository.AllCallLogs;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.refdata.objectcache.AllIVRLanguagesCache;
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
    private AllPatients allPatients;
    private AllIVRLanguagesCache allIVRLanguages;

    @Autowired
    public CallLogExcelReportService(AllCallLogs allCallLogs, AllPatients allPatients, AllIVRLanguagesCache allIVRLanguages) {
        this.allCallLogs = allCallLogs;
        this.allPatients = allPatients;
        this.allIVRLanguages = allIVRLanguages;
    }

    public HSSFWorkbook buildReport(LocalDate startDate, LocalDate endDate, boolean isAnalystReport) {
        CallLogReportBuilder callLogReportBuilder = new CallLogReportBuilder(allCallLogs, allPatients, allIVRLanguages, startDate, endDate, isAnalystReport);
        return createExcelReport(callLogReportBuilder);
    }

    protected HSSFWorkbook createExcelReport(ReportBuilder reportBuilder) {
        try {
            return reportBuilder.getExcelWorkbook();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error while generating excel report: " + e);
        }
        return null;
    }
}
