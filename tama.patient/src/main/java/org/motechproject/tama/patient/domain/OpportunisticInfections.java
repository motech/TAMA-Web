package org.motechproject.tama.patient.domain;

import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.domain.CouchEntity;

import javax.validation.constraints.NotNull;

@TypeDiscriminator("doc.documentType == 'OpportunisticInfections'")
public class OpportunisticInfections extends CouchEntity {

    private boolean addisonsDisease;
    private boolean anemia;
    private boolean bacterialInfection;
    private boolean convulsions;
    private boolean dementia;
    private boolean encephalitis;
    private boolean gastroenteropathy;
    private boolean hypertension;
    private boolean liverAbscess;
    private boolean malaria;
    private boolean nonHealingUlcer;
    private boolean oralCandidiasis;
    private boolean pancreatitis;
    private boolean scabies;
    private boolean TBMeningitis;

    private boolean otherOpportunisticInfection;
    private String otherOpportunisticInfectionDetails;

    @NotNull
    private String patientId;

   private LocalDate captureDate;

    public OpportunisticInfections() {
    }

    public OpportunisticInfections(String patientId) {
        this.patientId = patientId;
    }

    public boolean getAddisonsDisease() {
        return addisonsDisease;
    }

    public void setAddisonsDisease(boolean addisonsDisease) {
        this.addisonsDisease = addisonsDisease;
    }

    public boolean getAnemia() {
        return anemia;
    }

    public void setAnemia(boolean anemia) {
        this.anemia = anemia;
    }

    public boolean getBacterialInfection() {
        return bacterialInfection;
    }

    public void setBacterialInfection(boolean bacterialInfection) {
        this.bacterialInfection = bacterialInfection;
    }

    public boolean getConvulsions() {
        return convulsions;
    }

    public void setConvulsions(boolean convulsions) {
        this.convulsions = convulsions;
    }

    public boolean getDementia() {
        return dementia;
    }

    public void setDementia(boolean dementia) {
        this.dementia = dementia;
    }

    public boolean getEncephalitis() {
        return encephalitis;
    }

    public void setEncephalitis(boolean encephalitis) {
        this.encephalitis = encephalitis;
    }

    public boolean getGastroenteropathy() {
        return gastroenteropathy;
    }

    public void setGastroenteropathy(boolean gastroenteropathy) {
        this.gastroenteropathy = gastroenteropathy;
    }

    public boolean getHypertension() {
        return hypertension;
    }

    public void setHypertension(boolean hypertension) {
        this.hypertension = hypertension;
    }

    public boolean getLiverAbscess() {
        return liverAbscess;
    }

    public void setLiverAbscess(boolean liverAbscess) {
        this.liverAbscess = liverAbscess;
    }

    public boolean getMalaria() {
        return malaria;
    }

    public void setMalaria(boolean malaria) {
        this.malaria = malaria;
    }

    public boolean getNonHealingUlcer() {
        return nonHealingUlcer;
    }

    public void setNonHealingUlcer(boolean nonHealingUlcer) {
        this.nonHealingUlcer = nonHealingUlcer;
    }

    public boolean getOralCandidiasis() {
        return oralCandidiasis;
    }

    public void setOralCandidiasis(boolean oralCandidiasis) {
        this.oralCandidiasis = oralCandidiasis;
    }

    public boolean getPancreatitis() {
        return pancreatitis;
    }

    public void setPancreatitis(boolean pancreatitis) {
        this.pancreatitis = pancreatitis;
    }

    public boolean getScabies() {
        return scabies;
    }

    public void setScabies(boolean scabies) {
        this.scabies = scabies;
    }

    public boolean getTBMeningitis() {
        return TBMeningitis;
    }

    public void setTBMeningitis(boolean TBMeningitis) {
        this.TBMeningitis = TBMeningitis;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public LocalDate getCaptureDate() {
        return captureDate;
    }

    public void setCaptureDate(LocalDate captureDate) {
        this.captureDate = captureDate;
    }

    public boolean getOtherOpportunisticInfection() {
        return otherOpportunisticInfection;
    }

    public void setOtherOpportunisticInfection(boolean otherOpportunisticInfection) {
        this.otherOpportunisticInfection = otherOpportunisticInfection;
    }

    public String getOtherOpportunisticInfectionDetails() {
        return otherOpportunisticInfectionDetails;
    }

    public void setOtherOpportunisticInfectionDetails(String otherOpportunisticInfectionDetails) {
        this.otherOpportunisticInfectionDetails = otherOpportunisticInfectionDetails;
    }
}
