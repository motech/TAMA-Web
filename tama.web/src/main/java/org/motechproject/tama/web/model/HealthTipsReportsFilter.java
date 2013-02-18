package org.motechproject.tama.web.model;

import lombok.Data;

@Data
public class HealthTipsReportsFilter extends FilterWithPatientIDAndDateRange {

    private String clinicName;
}
