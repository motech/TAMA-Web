package org.motechproject.tama.domain;

import org.ektorp.support.TypeDiscriminator;

import javax.validation.constraints.NotNull;

@TypeDiscriminator("doc.documentType == 'UniquePatientField'")
public class UniquePatientField extends CouchEntity {

    @NotNull
    String primaryDocId;

    public UniquePatientField(String id, String primaryDocId) {
        this.setId(id);
        this.setPrimaryDocId(primaryDocId);
    }

    private UniquePatientField() {
    }

    public String getPrimaryDocId() {
        return primaryDocId;
    }

    public void setPrimaryDocId(String primaryDocId) {
        this.primaryDocId = primaryDocId;
    }
}
