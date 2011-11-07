package org.motechproject.tama.ivr;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;

public class DosageResponseWithDate extends DosageResponse {

    private final DosageResponse dosageResponse;

    private final LocalDate dosageDate;

    public DosageResponseWithDate(DosageResponse dosageResponse, LocalDate dosageDate) {
        super(dosageResponse.getDosageId(), new Time(dosageResponse.getDosageHour(), dosageResponse.getDosageMinute()),
                dosageResponse.getStartDate(), dosageResponse.getEndDate(), dosageResponse.getResponseLastCapturedDate(),
                dosageResponse.getMedicines());
        this.dosageResponse = dosageResponse;
        this.dosageDate = dosageDate;
    }

    public LocalDate getDosageDate() {
        return dosageDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DosageResponseWithDate that = (DosageResponseWithDate) o;

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
