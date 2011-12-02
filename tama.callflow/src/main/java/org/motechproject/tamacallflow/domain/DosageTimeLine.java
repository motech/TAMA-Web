package org.motechproject.tamacallflow.domain;

import org.joda.time.DateTime;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.tamacallflow.ivr.Dose;
import org.motechproject.util.DateUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class DosageTimeLine implements Iterator<Dose> {

    private List<DosageResponse> dosageResponses;
    private DateTime from;
    private DateTime to;
    private int index;
    private DateTime iteratingDate;

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
        int i = 0;
        boolean found = false;
        for (DosageResponse dosageResponse : dosageResponses) {
            if (isDosageApplicableForDate(dosageResponse, from)) {
                index = i;
                found = true;
                break;
            }
            i++;
        }
        if (found) {
            iteratingDate = from;
        } else {
            DosageResponse nextDosage = dosageResponses.get(index);
            iteratingDate = from.plusDays(1).withHourOfDay(nextDosage.getDosageHour()).withMinuteOfHour(nextDosage.getDosageMinute());
        }
    }

    @Override
    public boolean hasNext() {
        return isDosageApplicableForDate(dosageResponses.get(index), iteratingDate);
    }

    @Override
    public Dose next() {
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
        return new Dose(dosageResponseToReturn, dateToReturn.toLocalDate());
    }

    private boolean isDosageApplicableForDate(DosageResponse dosageResponse, DateTime iteratingDateTime) {
        int dosageTimeInMinutes = (dosageResponse.getDosageHour() * 60) + dosageResponse.getDosageMinute();
        DateTime dosageStartTime = DateUtil.newDateTime(dosageResponse.getStartDate(), dosageResponse.getDosageHour(), dosageResponse.getDosageMinute(), 0);
        if (to.isBefore(dosageStartTime))
            return false;
        if ((iteratingDateTime.isEqual(from))) {
            int timeInMinutes = (from.getHourOfDay() * 60) + from.getMinuteOfHour();
            return timeInMinutes <= dosageTimeInMinutes;
        } else if (isLastDay(iteratingDateTime)) {
            int timeInMinutes = (to.getHourOfDay() * 60) + to.getMinuteOfHour();
            return timeInMinutes >= dosageTimeInMinutes;
        } else if (iteratingDateTime.isAfter(from) && iteratingDateTime.isBefore(to)) {
            return true;
        } else if (iteratingDateTime.isEqual(from) && isLastDay(iteratingDateTime)) {
            int startTime = (from.getHourOfDay() * 60) + from.getMinuteOfHour();
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
}