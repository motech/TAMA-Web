package org.motechproject.tama.domain;

import org.motechproject.tama.TAMAConstants;

import java.util.ArrayList;
import java.util.List;

public class NonHIVMedicalHistory extends BaseEntity {

    private List<AllergyHistory> allergiesHistory = new ArrayList<AllergyHistory>();

    private List<TAMAConstants.NNRTIRash> rashes = new ArrayList<TAMAConstants.NNRTIRash>();

    public List<AllergyHistory> getAllergiesHistory() {
        return allergiesHistory;
    }

    public void setAllergiesHistory(List<AllergyHistory> allergiesHistory) {
        this.allergiesHistory = allergiesHistory;
    }

    public List<TAMAConstants.NNRTIRash> getRashes() {
        return rashes;
    }

    public void setRashes(List<TAMAConstants.NNRTIRash> rashes) {
        this.rashes = rashes;
    }

    public List<AllergyHistory> getSpecifiedAllergies() {
        ArrayList<AllergyHistory> specifiedAllergies = new ArrayList<AllergyHistory>();
        for (AllergyHistory allergyHistory : allergiesHistory) {
            if (allergyHistory.isSpecified())
                specifiedAllergies.add(allergyHistory);
        }
        return specifiedAllergies;
    }
}
