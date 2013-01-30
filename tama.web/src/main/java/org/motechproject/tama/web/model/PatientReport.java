package org.motechproject.tama.web.model;

import lombok.Data;
import org.joda.time.DateTime;

@Data
public class PatientReport {

    public DateTime startDate;
    public DateTime endDate;
}
