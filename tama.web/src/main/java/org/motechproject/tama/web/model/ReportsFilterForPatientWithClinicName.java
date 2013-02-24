package org.motechproject.tama.web.model;

import lombok.Data;

@Data
public class ReportsFilterForPatientWithClinicName extends FilterWithPatientIDAndDateRange {

    private String clinicName;
}
