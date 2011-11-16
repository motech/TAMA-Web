package org.motechproject.tama.web.model;

import org.motechproject.tama.domain.LabResults;

import javax.validation.Valid;

public class LabResultsUIModel {

    private String id;

    private String Version;

    private String z;

    @Valid
    private LabResults labResults = new LabResults();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }

    public String getZ() {
        return z;
    }

    public void setZ(String z) {
        this.z = z;
    }

    public LabResults getLabResults() {
        return labResults;
    }

    public void setLabResults(LabResults labResults) {
        this.labResults = labResults;
    }

    public String getPatientId() {
        if (!labResults.isEmpty())
            return labResults.get(0).getPatientId();
        else
            return "";
    }

    public static LabResultsUIModel newDefault() {
        return new LabResultsUIModel();
    }
}
