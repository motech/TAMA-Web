package org.motechproject.tama.web.view;

import lombok.Getter;
import lombok.Setter;
import org.motechproject.tama.common.TAMAConstants;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

public class CallLogPreferencesFilter {

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    @Getter @Setter private Date callLogStartDate;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    @Getter @Setter private Date callLogEndDate;

    @Getter @Setter private String callType;

    @Getter @Setter private String pageNumber;
}
