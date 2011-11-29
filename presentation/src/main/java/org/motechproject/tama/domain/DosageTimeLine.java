package org.motechproject.tama.domain;

import org.joda.time.DateTime;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.tama.ivr.DosageResponseWithDate;
import org.motechproject.util.DateUtil;

import javax.transaction.NotSupportedException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class DosageTimeLine implements Iterator<DosageResponseWithDate> {

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
        this.to = to == null ? DateUtil.now(): to ;
        initializeDosageIndexAndIteratingDate();
    }

    private void initializeDosageIndexAndIteratingDate() {
        iteratingDate = from;
        int i = 0;
        boolean found = false;
        for(DosageResponse dosageResponse: dosageResponses) {
            if(isDosageApplicableForDate(dosageResponse, from)) {
                index = i;
                found = true;
                break;
            }
            i++;
        }
        if(!found) {
            iteratingDate = iteratingDate.plusDays(1);
            DosageResponse nextDosage = dosageResponses.get(index);
            iteratingDate = iteratingDate.withHourOfDay(nextDosage.getDosageHour()).withMinuteOfHour(nextDosage.getDosageMinute());
        }

    }

    @Override
    public boolean hasNext() {
        return isDosageApplicableForDate(dosageResponses.get(index), iteratingDate);
    }

    @Override
    public DosageResponseWithDate next() {
        DosageResponse dosageResponseToReturn = dosageResponses.get(index);
        DateTime dateToReturn = iteratingDate;
        if(isDosageApplicableForDate(dosageResponseToReturn, iteratingDate)) {
            if(index == dosageResponses.size()-1) {
                iteratingDate = iteratingDate.plusDays(1);
                index = 0;
            } else {
                index++;
            }
            DosageResponse nextDosage = dosageResponses.get(index);
            iteratingDate = iteratingDate.withHourOfDay(nextDosage.getDosageHour()).withMinuteOfHour(nextDosage.getDosageMinute());
            return new DosageResponseWithDate(dosageResponseToReturn, dateToReturn.toLocalDate());
        }
        throw new ArrayIndexOutOfBoundsException("There are no more dosages to return");
    }

    private boolean isDosageApplicableForDate(DosageResponse dosageResponse, DateTime day) {
        int dosageTimeInMinutes = (dosageResponse.getDosageHour() * 60) + dosageResponse.getDosageMinute();
        if(day.isEqual(from) && !isLastDay(day)) {
            int timeInMinutes = (from.getHourOfDay() * 60) + from.getMinuteOfHour();
            return timeInMinutes <= dosageTimeInMinutes;
        } else if(isLastDay(day) && !day.isEqual(from)) {
            int timeInMinutes = (to.getHourOfDay() * 60) + to.getMinuteOfHour();
            return timeInMinutes >= dosageTimeInMinutes;
        } else if(day.isAfter(from) && day.isBefore(to)) {
            return true;
        } else if(day.isEqual(from) && isLastDay(day)){
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