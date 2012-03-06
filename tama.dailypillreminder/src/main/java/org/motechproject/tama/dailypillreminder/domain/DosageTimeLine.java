package org.motechproject.tama.dailypillreminder.domain;

import org.joda.time.DateTime;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.util.DateUtil;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DosageTimeLine implements Iterator<Dose> {

    private DosageResponse dosageResponse;
    private DateTime from;
    private DateTime to;
    private Dose nextDose;

    public DosageTimeLine(DosageResponse dosageResponse, DateTime from, DateTime to) {
        this.dosageResponse = dosageResponse;
        this.from = from;
        this.to = to;
        this.nextDose = computeFirstDose();
    }

    private Dose computeFirstDose() {
        if (to.isBefore(from)) return null;
        if (from.toLocalDate().isBefore(dosageResponse.getStartDate())) {
            from = DateUtil.newDateTime(dosageResponse.getStartDate());
        }

        DateTime tentativeFirstDoseDateTime = DateUtil.newDateTime(from.toLocalDate(), dosageResponse.getDosageHour(), dosageResponse.getDosageMinute(), 0);
        if (tentativeFirstDoseDateTime.isBefore(from)) {
            tentativeFirstDoseDateTime = tentativeFirstDoseDateTime.plusDays(1);
        }
        if (tentativeFirstDoseDateTime.isAfter(to)) {
            return null;
        }
        return new Dose(dosageResponse, tentativeFirstDoseDateTime.toLocalDate());
    }

    private Dose computeNextDose() {
        DateTime tentativeNextDoseTime = nextDose.getDoseTime().plusDays(1);
        if (tentativeNextDoseTime.isAfter(to)) return null;
        return new Dose(dosageResponse, tentativeNextDoseTime.toLocalDate());
    }

    @Override
    public boolean hasNext() {
        return nextDose != null;
    }

    @Override
    public Dose next() {
        if (!hasNext()) throw new NoSuchElementException();
        Dose currentDose = nextDose;
        nextDose = computeNextDose();
        return currentDose;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}