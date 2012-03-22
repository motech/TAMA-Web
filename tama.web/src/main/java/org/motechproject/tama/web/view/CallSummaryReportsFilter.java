package org.motechproject.tama.web.view;

import lombok.Getter;
import lombok.Setter;
import org.motechproject.tama.common.TAMAConstants;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

public class CallSummaryReportsFilter {

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    @Getter @Setter private Date startDate;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    @Getter @Setter private Date endDate;
}
