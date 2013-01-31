package org.motechproject.tama.web.model;

import lombok.Data;
import org.joda.time.DateTime;

@Data
public class DateFilter {
    public DateTime startDate;
    public DateTime endDate;
}
