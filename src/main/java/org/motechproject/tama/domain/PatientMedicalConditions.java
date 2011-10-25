package org.motechproject.tama.domain;

public class PatientMedicalConditions {
    private String regimenName;
    private String gender;
    private int age;
    private int cd4Count;
    private boolean diabetic;
    private boolean hyperTensic;
    private boolean nephrotoxic;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getCd4Count() {
        return cd4Count;
    }

    public void setCd4Count(int cd4Count) {
        this.cd4Count = cd4Count;
    }

    public String getRegimenName() {
        return regimenName;
    }

    public void setRegimenName(String regimenId) {
        this.regimenName = regimenId;
    }

    public boolean isDiabetic() {
        return diabetic;
    }

    public void setDiabetic(boolean diabetic) {
        this.diabetic = diabetic;
    }

    public boolean isHyperTensic() {
        return hyperTensic;
    }

    public void setHyperTensic(boolean hyperTensic) {
        this.hyperTensic = hyperTensic;
    }

    public boolean isNephrotoxic() {
        return nephrotoxic;
    }

    public void setNephrotoxic(boolean nephrotoxic) {
        this.nephrotoxic = nephrotoxic;
    }
}