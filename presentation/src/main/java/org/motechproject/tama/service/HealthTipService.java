package org.motechproject.tama.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.motechproject.tama.domain.HealthTipsHistory;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.AllHealthTipsHistory;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.lessThan;

public class HealthTipService {

    //TODO: make it configurable
    public static final int PRIORITY_1_EXPIRY = 7;
    public static final int PRIORITY_2_EXPIRY = 14;
    public static final int PRIORITY_3_EXPIRY = 21;

    static class PrioritizedHealthTip {

        private HealthTipsHistory healthTipsHistory;
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

    AllHealthTipsHistory allHealthTipsHistory;

    Integer playListSize = 2; //TODO: read from properties.

    public HealthTipService(AllHealthTipsHistory allHealthTipsHistory) {
        this.allHealthTipsHistory = allHealthTipsHistory;
    }

    //TODO: duh
    List<PrioritizedHealthTip> getApplicableHealthTips(Patient patient) {
        return null;
    }

    List<String> getPlayList(Patient patient) {
        List<PrioritizedHealthTip> healthTips = getApplicableHealthTips(patient);

        filterExpiredHealthTips(healthTips);

        return extract(healthTips, on(PrioritizedHealthTip.class).getHealthTipsHistory().getAudioFilename());
    }

    private void filterExpiredHealthTips(List<PrioritizedHealthTip> healthTips) {
        final long[] priorityExpiries = {DateUtil.now().minusDays(PRIORITY_1_EXPIRY).getMillis(), DateUtil.now().minusDays(PRIORITY_2_EXPIRY).getMillis(), DateUtil.now().minusDays(PRIORITY_3_EXPIRY).getMillis()};
        CollectionUtils.filter(healthTips, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                PrioritizedHealthTip healthTip = (PrioritizedHealthTip) o;
                return healthTip.getHealthTipsHistory().getLastPlayed().getMillis() < priorityExpiries[healthTip.getPriority() - 1];
            }
        });
    }
}
