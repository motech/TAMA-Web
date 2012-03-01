package org.motechproject.tama.patient.domain;

import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.domain.CouchEntity;
import org.motechproject.tama.refdata.domain.OpportunisticInfection;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@TypeDiscriminator("doc.documentType == 'ReportedOpportunisticInfections'")
public class ReportedOpportunisticInfections extends CouchEntity {

    @NotNull
    private List<String> opportunisticInfectionIds = new ArrayList<String>();

    private String otherOpportunisticInfectionDetails;

    @NotNull
    private String patientId;

    private LocalDate captureDate;

    public ReportedOpportunisticInfections(String patientId) {
        this.patientId = patientId;
    }

    public List<String> getOpportunisticInfectionIds() {
        return opportunisticInfectionIds;
    }

    public String getPatientId() {
        return patientId;
    }

    public LocalDate getCaptureDate() {
        return captureDate;
    }

    public void setCaptureDate(LocalDate captureDate) {
        this.captureDate = captureDate;
    }

    public void setOtherOpportunisticInfectionDetails(String otherOpportunisticInfectionDetails) {
        this.otherOpportunisticInfectionDetails = otherOpportunisticInfectionDetails;
    }

    public String getOtherOpportunisticInfectionDetails() {
        return otherOpportunisticInfectionDetails;
    }

    public void addOpportunisticInfection(OpportunisticInfection opportunisticInfection) {
        opportunisticInfectionIds.add(opportunisticInfection.getId());
    }

}

