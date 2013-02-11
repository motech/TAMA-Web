package org.motechproject.tama.web.model;

import lombok.Data;
import org.motechproject.tama.patient.domain.PatientEvent;

import java.util.ArrayList;
import java.util.List;

@Data
public class PatientEventFilter extends DateFilter {

    private String eventName;

    public List<String> getAllPatientEvents() {
        List<String> result = new ArrayList<>();
        result.add("");
        for (PatientEvent event : PatientEvent.values()) {
            result.add(event.getDisplayName());
        }
        return result;
    }
}
