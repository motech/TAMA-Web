package org.motechproject.tamahealthtip.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.motechproject.tamadomain.domain.Patient;
import org.motechproject.tamadomain.domain.TreatmentAdvice;
import org.motechproject.tamadomain.repository.AllPatients;
import org.motechproject.tamadomain.repository.AllTreatmentAdvices;
import org.motechproject.tamahealthtip.domain.HealthTipsHistory;
import org.motechproject.tamahealthtip.repository.AllHealthTipsHistory;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;

@Service
public class HealthTipService {

    //TODO: make it configurable
    public static final int PRIORITY_1_EXPIRY = 7;
    public static final int PRIORITY_2_EXPIRY = 14;
    public static final int PRIORITY_3_EXPIRY = 21;
    public static final int START_OF_TIME = 0;

    private AllHealthTipsHistory allHealthTipsHistory;
    private HealthTipRuleService healthTipRuleService;
    private AllTreatmentAdvices allTreatmentAdvices;
    private AllPatients allPatients;

    Integer playListSize = 2; //TODO: read from properties.

    public static class PrioritizedHealthTip {

        private HealthTipsHistory healthTipsHistory;          //todo: merge prioritizedHT and HThistory into single class.
        private Integer priority;

        public PrioritizedHealthTip(HealthTipsHistory healthTipsHistory, Integer priority) {
            this.healthTipsHistory = healthTipsHistory;
            this.priority = priority;
        }

        public HealthTipsHistory getHealthTipsHistory() {
            return healthTipsHistory;
        }

        public void setHealthTipsHistory(HealthTipsHistory healthTipsHistory) {
            this.healthTipsHistory = healthTipsHistory;
        }

        public Integer getPriority() {
            return priority;
        }

        public void setPriority(Integer priority) {
            this.priority = priority;
        }
    }

    @Autowired
    public HealthTipService(AllHealthTipsHistory allHealthTipsHistory, HealthTipRuleService healthTipRuleService, AllTreatmentAdvices allTreatmentAdvices, AllPatients allPatients) {
        this.allHealthTipsHistory = allHealthTipsHistory;
        this.healthTipRuleService = healthTipRuleService;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.allPatients = allPatients;
    }

    public void markAsPlayed(String patientDocumentId, String audioFilename) {
        HealthTipsHistory healthTipsHistory = allHealthTipsHistory.findByPatientIdAndAudioFilename(patientDocumentId, audioFilename);
        if (healthTipsHistory == null) {
            healthTipsHistory = new HealthTipsHistory(patientDocumentId, audioFilename, DateUtil.now());
            allHealthTipsHistory.add(healthTipsHistory);
        } else {
            healthTipsHistory.setLastPlayed(DateUtil.now());
            allHealthTipsHistory.update(healthTipsHistory);
        }
    }

    List<PrioritizedHealthTip> getApplicableHealthTips(String patientDocId) {
        final Patient patient = allPatients.get(patientDocId);
        final TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientDocId);
        Map<String, String> healthTipFiles = healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.newDate(treatmentAdvice.getStartDate()), patient);

        List<HealthTipsHistory> healthTipsHistories = allHealthTipsHistory.findByPatientId(patientDocId);

        List<PrioritizedHealthTip> prioritizedHealthTips = new ArrayList<PrioritizedHealthTip>();
        for (String audioFilename : healthTipFiles.keySet()) {
            HealthTipsHistory healthTipsHistory = selectFirst(healthTipsHistories, having(on(HealthTipsHistory.class).getAudioFilename(), equalTo(audioFilename)));
            if (healthTipsHistory == null) {
                healthTipsHistory = new HealthTipsHistory(patientDocId, audioFilename, new DateTime(START_OF_TIME));
            }
            prioritizedHealthTips.add(new PrioritizedHealthTip(healthTipsHistory, Integer.parseInt(healthTipFiles.get(audioFilename))));
        }
        return prioritizedHealthTips;
    }

    public List<String> getPlayList(String patientId) {
        List<PrioritizedHealthTip> healthTips = getApplicableHealthTips(patientId);
        filterExpiredHealthTips(healthTips);
        sortBasedOnPriorityThenByLastPlayedDate(healthTips);
        return extract(healthTips, on(PrioritizedHealthTip.class).getHealthTipsHistory().getAudioFilename());
    }

    private void filterExpiredHealthTips(List<PrioritizedHealthTip> healthTips) {
        final int[] priorityExpiries = {PRIORITY_1_EXPIRY, PRIORITY_2_EXPIRY, PRIORITY_3_EXPIRY};
        final DateTime now = DateUtil.now();
        CollectionUtils.filter(healthTips, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                PrioritizedHealthTip healthTip = (PrioritizedHealthTip) o;
                DateTime lastPlayed = healthTip.getHealthTipsHistory().getLastPlayed();
                if (lastPlayed == null) return true;
                return lastPlayed.isBefore(now.minusDays(priorityExpiries[healthTip.getPriority() - 1]));
            }
        });
    }

    private void sortBasedOnPriorityThenByLastPlayedDate(List<PrioritizedHealthTip> healthTips) {
        Collections.sort(healthTips, new Comparator<PrioritizedHealthTip>() {
            @Override
            public int compare(PrioritizedHealthTip tip1, PrioritizedHealthTip tip2) {
                if (tip1.getPriority() != tip2.getPriority())
                    return (tip1.getPriority() - tip2.getPriority());
                return DateTimeComparator.getInstance().compare(tip1.getHealthTipsHistory().getLastPlayed(), tip2.getHealthTipsHistory().getLastPlayed());
            }
        });
    }
}
