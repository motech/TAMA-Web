package org.motechproject.tama.web.model;

import lombok.Data;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Data
public class AnalystOutboxReportFilter {
    private String patientId;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    private LocalDate startDate;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    private LocalDate endDate;
}
