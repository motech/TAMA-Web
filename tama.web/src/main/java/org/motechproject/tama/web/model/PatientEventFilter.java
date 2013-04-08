package org.motechproject.tama.web.model;

import lombok.Data;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.domain.PatientEvent;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.List;

@Data
public class PatientEventFilter {

    private DateFilter dateFilter = new DateFilter();
    private String eventName;
    private String clinicId;

    public List<String> getAllPatientEvents() {
        List<String> result = new ArrayList<>();
        result.add("");
        for (PatientEvent event : PatientEvent.values()) {
            if(event.shouldDisplayInAnalystFilter())
                result.add(event.getDisplayName());
        }
        return result;
    }

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    public LocalDate getStartDate() {
        return dateFilter.getStartDate();
    }

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    public LocalDate getEndDate() {
        return dateFilter.getEndDate();
    }

    public void setStartDate(LocalDate startDate) {
        dateFilter.setStartDate(startDate);
    }

    public void setEndDate(LocalDate endDate) {
        dateFilter.setEndDate(endDate);
    }
}

