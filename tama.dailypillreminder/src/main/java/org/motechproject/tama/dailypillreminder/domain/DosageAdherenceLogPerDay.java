package org.motechproject.tama.dailypillreminder.domain;

import org.joda.time.LocalDate;

import java.util.List;

public class DosageAdherenceLogPerDay {

        List<DosageAdherenceLogSummary> logs;
        LocalDate date;

        public void setLogs(List<DosageAdherenceLogSummary> dosageAdherenceLogSummaries){
            this.logs = dosageAdherenceLogSummaries;
        }

        public List<DosageAdherenceLogSummary> getLogs(){
            return logs;
        }

        public void setDate(LocalDate date){
            this.date = date;
        }

        public LocalDate getDate(){
            return date;
        }
    }
