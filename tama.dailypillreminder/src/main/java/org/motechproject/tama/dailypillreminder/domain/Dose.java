package org.motechproject.tama.dailypillreminder.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

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

    public boolean isLate(DateTime doseTakenTime, int dosageInterval) {
        DateTime scheduledDoseInterval = getDoseTime().plusMinutes(dosageInterval);
        return doseTakenTime.isAfter(scheduledDoseInterval);
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

    public List<String> medicineNames() {
        List<String> medicineNames = new ArrayList<String>();

        for (MedicineResponse medicine : getMedicines()) {
            if (!date.isBefore(medicine.getStartDate()) &&
                    (medicine.getEndDate() == null || !date.isAfter(medicine.getEndDate())))
                medicineNames.add(String.format("pill%s", medicine.getName()));
        }
        return medicineNames;

    }
}
