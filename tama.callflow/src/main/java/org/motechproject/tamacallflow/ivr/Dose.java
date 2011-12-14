package org.motechproject.tamacallflow.ivr;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.util.DateUtil;

public class Dose extends DosageResponse {

    private final DosageResponse dosageResponse;

    private final LocalDate date;

    public Dose(DosageResponse dosageResponse, LocalDate date) {
        super(dosageResponse.getDosageId(), new Time(dosageResponse.getDosageHour(), dosageResponse.getDosageMinute()),
                dosageResponse.getStartDate(), dosageResponse.getEndDate(), dosageResponse.getResponseLastCapturedDate(),
                dosageResponse.getMedicines());
        this.dosageResponse = dosageResponse;
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public DateTime getDoseTime() {
        return DateUtil.newDateTime(date, dosageResponse.getDosageHour(), dosageResponse.getDosageMinute(), 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dose that = (Dose) o;

        if (dosageResponse != null ? !dosageResponse.equals(that.dosageResponse) : that.dosageResponse != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return dosageResponse != null ? dosageResponse.hashCode() : 0;
    }

    public DosageResponse getDosage() {
        return dosageResponse;
    }
}
