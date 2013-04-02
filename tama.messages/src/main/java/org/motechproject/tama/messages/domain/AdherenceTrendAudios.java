package org.motechproject.tama.messages.domain;

import org.motechproject.tama.ivr.TamaIVRMessage;

import java.util.ArrayList;
import java.util.List;

public class AdherenceTrendAudios {

    private double adherencePercentage;
    private boolean falling;

    public AdherenceTrendAudios(double adherencePercentage, boolean falling) {
        this.adherencePercentage = adherencePercentage;
        this.falling = falling;
    }

    public String[] getFiles() {
        List<String> messages = new ArrayList<>();
        if (adherencePercentage > 90) {
            messages.add(TamaIVRMessage.M02_04_ADHERENCE_COMMENT_GT95_FALLING);
        } else if (adherencePercentage > 70) {
            if (falling) {
                messages.add(TamaIVRMessage.M02_05_ADHERENCE_COMMENT_70TO90_FALLING);
            } else {
                messages.add(TamaIVRMessage.M02_06_ADHERENCE_COMMENT_70TO90_RISING);
            }
        } else {
            if (falling) {
                messages.add(TamaIVRMessage.M02_07_ADHERENCE_COMMENT_LT70_FALLING);
            } else {
                messages.add(TamaIVRMessage.M02_08_ADHERENCE_COMMENT_LT70_RISING);
            }
        }
        return messages.toArray(new String[messages.size()]);
    }
}
