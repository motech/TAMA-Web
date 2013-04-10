package org.motechproject.tama.healthtips.service;

import net.sf.cglib.core.CollectionUtils;
import net.sf.cglib.core.Predicate;
import org.motechproject.tama.common.domain.TAMAMessageType;
import org.motechproject.tama.healthtips.domain.HealthTipsHistory;
import org.motechproject.tama.healthtips.repository.AllHealthTipsHistory;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.refdata.domain.HealthTip;
import org.motechproject.tama.refdata.domain.HealthTipSequence;
import org.motechproject.tama.refdata.repository.AllHealthTips;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

@Service
public class HealthTipService {

    private AllHealthTipsHistory allHealthTipsHistory;
    private AllPatients allPatients;
    private AllHealthTips allHealthTips;

    @Autowired
    public HealthTipService(AllHealthTipsHistory allHealthTipsHistory,
                            AllPatients allPatients, AllHealthTips allHealthTips) {
        this.allHealthTipsHistory = allHealthTipsHistory;
        this.allPatients = allPatients;
        this.allHealthTips = allHealthTips;
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

    public String nextHealthTip(String patientId, TAMAMessageType type) {
        List<String> playList = getPlayList(patientId, type);
        if (playList.size() > 0) {
            return playList.get(0);
        }
        return "";
    }

    public List<String> getPlayList(String patientId, TAMAMessageType type) {

        // Get All Health Tips
        // Get Health Tip History
        // Update Health Tip Playcount
        // Filter nonapplicable health tips where sequence is null
        // Sort by Playcount and then by sequence
        // return the audio filename list.

        String category = (TAMAMessageType.PUSHED_MESSAGE.equals(type)) ? null : type.getDisplayName();
        List<HealthTip> allHealthTipsByCategory = allHealthTips.findByCategory(category);
        List<HealthTipsHistory> healthTipHistoriesForPatient = allHealthTipsHistory.findByPatientId(patientId);

        for (HealthTipsHistory history : healthTipHistoriesForPatient) {
            for (HealthTip healthTip : allHealthTipsByCategory) {
                if (history.getAudioFilename().equals(healthTip.getAudioFileName())) {
                    healthTip.setPlayCount(history.getPlayCount());
                }
            }
        }

        Patient patient = allPatients.get(patientId);

        HealthTipSequence sequence = getHealthTipSequence(patient, type);

        filterHealthTipsBySequence(allHealthTipsByCategory, sequence);
        sortBasedOnPlayCountThenBySequence(allHealthTipsByCategory, sequence);
        return extract(allHealthTipsByCategory, on(HealthTip.class).getAudioFileName());
    }

    private void filterHealthTipsBySequence(List<HealthTip> healthTips, final HealthTipSequence sequence) {
        CollectionUtils.filter(healthTips, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                HealthTip healthTip = (HealthTip) o;
                Integer sequenceNumber = healthTip.getSequence(sequence);
                return (sequenceNumber != null);
            }
        });
    }

    private HealthTipSequence getHealthTipSequence(Patient patient, TAMAMessageType type) {
        boolean isDailyPillReminder = patient.isOnDailyPillReminder();
        boolean isPushMessage = (TAMAMessageType.PUSHED_MESSAGE.equals(type));
        return isPushMessage ? (isDailyPillReminder ? HealthTipSequence.PUSH_DAILY : HealthTipSequence.PUSH_WEEKLY) : (isDailyPillReminder ? HealthTipSequence.PULL_DAILY : HealthTipSequence.PULL_WEEKLY);
    }

    private void sortBasedOnPlayCountThenBySequence(List<HealthTip> healthTips, final HealthTipSequence sequence) {
        Collections.sort(healthTips, new Comparator<HealthTip>() {
            @Override
            public int compare(HealthTip tip1, HealthTip tip2) {

                if (!tip1.getPlayCount().equals(tip2.getPlayCount()))
                    return Integer.compare(tip1.getPlayCount(), tip2.getPlayCount());

                return Integer.compare(tip1.getSequence(sequence), tip2.getSequence(sequence));
            }
        });
    }
}
