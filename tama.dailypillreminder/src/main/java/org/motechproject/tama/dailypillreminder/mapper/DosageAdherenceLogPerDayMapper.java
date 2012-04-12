package org.motechproject.tama.dailypillreminder.mapper;

import org.joda.time.LocalDate;
import org.motechproject.tama.dailypillreminder.domain.DosageAdherenceLogPerDay;
import org.motechproject.tama.dailypillreminder.domain.DosageAdherenceLogSummary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DosageAdherenceLogPerDayMapper {

    public List<DosageAdherenceLogPerDay> map(List<DosageAdherenceLogSummary> dosageAdherenceLogSummaries) {
        HashMap<LocalDate, List<DosageAdherenceLogSummary>> map = constructLogsPerDayMap(dosageAdherenceLogSummaries);
        List<DosageAdherenceLogPerDay> list = new ArrayList<DosageAdherenceLogPerDay>();
        for (LocalDate date : map.keySet()) {
            list.add(new DosageAdherenceLogPerDay().setDate(date).setLogs(map.get(date)));
        }
        Collections.sort(list);
        return list;
    }

    private HashMap<LocalDate, List<DosageAdherenceLogSummary>> constructLogsPerDayMap(List<DosageAdherenceLogSummary> dosageAdherenceLogSummaries) {
        HashMap<LocalDate, List<DosageAdherenceLogSummary>> map = new HashMap<LocalDate, List<DosageAdherenceLogSummary>>();
        for (DosageAdherenceLogSummary summary : dosageAdherenceLogSummaries) {
            if (map.get(summary.getDosageDate()) == null) map.put(summary.getDosageDate(), new ArrayList<DosageAdherenceLogSummary>());
            map.get(summary.getDosageDate()).add(summary);
        }
        return map;
    }
}
