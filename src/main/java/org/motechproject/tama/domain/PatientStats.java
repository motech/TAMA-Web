package org.motechproject.tama.domain;

public class PatientStats {

    private int regimen = -1;

    private int baseLineCD4Count = -1;

    private boolean pychiatricIllnessHistoryPresent;

    private int baseLineHB4 =  -1;

    public int getRegimen() {
        return regimen;
    }

    public void setRegimen(int regimen) {
        this.regimen = regimen;
    }

    public int getBaseLineCD4Count() {
        return baseLineCD4Count;
    }

    public void setBaseLineCD4Count(int baseLineCD4Count) {
        this.baseLineCD4Count = baseLineCD4Count;
    }

    public boolean isPychiatricIllnessHistoryPresent() {
        return pychiatricIllnessHistoryPresent;
    }

    public void setPychiatricIllnessHistoryPresent(boolean pychiatricIllnessHistoryPresent) {
        this.pychiatricIllnessHistoryPresent = pychiatricIllnessHistoryPresent;
    }

    public int getBaseLineHB4() {
        return baseLineHB4;
    }

    public void setBaseLineHB4(int baseLineHB4) {
        this.baseLineHB4 = baseLineHB4;
    }
}
