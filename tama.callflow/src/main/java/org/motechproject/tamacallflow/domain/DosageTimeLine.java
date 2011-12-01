package org.motechproject.tamacallflow.domain;

import org.joda.time.DateTime;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.tamacallflow.ivr.Dosage;
import org.motechproject.util.DateUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class DosageTimeLine implements Iterator<Dosage> {

    private List<DosageResponse> dosageResponses;
    private DateTime from;
    private DateTime to;
    private int index;
    private DateTime iteratingDate;
    private boolean dosageExists;

    public DosageTimeLine(List<DosageResponse> dosageResponses, DateTime from) {
        this(dosageResponses, from, null);
    }

    public DosageTimeLine(List<DosageResponse> dosages, DateTime from, DateTime to) {
        this.dosageResponses = sort(dosages);
        this.from = from;
        this.to = to == null ? DateUtil.now() : to;
        initializeDosageIndexAndIteratingDate();
    }

    private void initializeDosageIndexAndIteratingDate() {
        DosageResponse earliestDosage = earliestDosage();
        DateTime earliestDosageTime = DateUtil.newDateTime(earliestDosage.getStartDate(), earliestDosage.getDosageHour(), earliestDosage.getDosageMinute(), 0);
        iteratingDate = earliestDosageTime.isAfter(from)? earliestDosageTime : from;
        int i = 0;
        dosageExists = false;
        for (DosageResponse dosageResponse : dosageResponses) {
            if (isDosageApplicableForDate(dosageResponse, iteratingDate)) {
                index = i;
                dosageExists = true;
                break;
            }
            i++;
        }
    }

    @Override
    public boolean hasNext() {
        return isDosageApplicableForDate(dosageResponses.get(index), iteratingDate);
    }

    @Override
    public Dosage next() {
        if (!isDosageApplicableForDate(dosageResponses.get(index), iteratingDate))
            throw new ArrayIndexOutOfBoundsException("There are no more dosages to return");

        DosageResponse dosageResponseToReturn = dosageResponses.get(index);
        DateTime dateToReturn = iteratingDate;
        if (index == dosageResponses.size() - 1) {
            iteratingDate = iteratingDate.plusDays(1);
            index = 0;
        } else {
            index++;
        }
        DosageResponse nextDosage = dosageResponses.get(index);
        iteratingDate = iteratingDate.withHourOfDay(nextDosage.getDosageHour()).withMinuteOfHour(nextDosage.getDosageMinute());
        return new Dosage(dosageResponseToReturn, dateToReturn.toLocalDate());
    }

    private boolean isDosageApplicableForDate(DosageResponse dosageResponse, DateTime day) {
        int dosageTimeInMinutes = (dosageResponse.getDosageHour() * 60) + dosageResponse.getDosageMinute();
        if ((day.isEqual(from)) && !isLastDay(day)) {
            int timeInMinutes = (day.getHourOfDay() * 60) + day.getMinuteOfHour();
            return timeInMinutes <= dosageTimeInMinutes;
        } else if (isLastDay(day) && !day.isEqual(from)) {
            int timeInMinutes = (to.getHourOfDay() * 60) + to.getMinuteOfHour();
            return timeInMinutes >= dosageTimeInMinutes;
        } else if (day.isAfter(from) && day.isBefore(to)) {
            return true;
        } else if (day.isEqual(from) && isLastDay(day)) {
            int startTime = (day.getHourOfDay() * 60) + day.getMinuteOfHour();
            int endTime = (to.getHourOfDay() * 60) + to.getMinuteOfHour();
            return dosageTimeInMinutes >= startTime ? dosageTimeInMinutes <= endTime : false;
        }
        return false;
    }

    private boolean isLastDay(DateTime day) {
        return (day.toLocalDate().compareTo(to.toLocalDate())) == 0;
    }

    @Override
    public void remove() {
        throw new RuntimeException("Cannot remove dosages from iteration");
    }

    private List<DosageResponse> sort(List<DosageResponse> dosageResponses) {
        Collections.sort(dosageResponses, new Comparator<DosageResponse>() {
            @Override
            public int compare(DosageResponse o1, DosageResponse o2) {
                Integer time1InMinutes = (o1.getDosageHour() * 60) + o1.getDosageMinute();
                Integer time2InMinutes = (o2.getDosageHour() * 60) + o2.getDosageMinute();
                return time1InMinutes.compareTo(time2InMinutes);
            }
        });
        return dosageResponses;
    }

    private DosageResponse earliestDosage() {
        DosageResponse earliestDosage = dosageResponses.get(0);
        for (DosageResponse dosageResponse : dosageResponses) {
            if (dosageResponse.getStartDate().isBefore(earliestDosage.getStartDate())) {
                earliestDosage = dosageResponse;
            }
        }
        return earliestDosage;
    }
}