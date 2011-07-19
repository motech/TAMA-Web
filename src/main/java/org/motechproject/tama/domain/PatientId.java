package org.motechproject.tama.domain;

import org.ektorp.support.TypeDiscriminator;

@TypeDiscriminator("doc.documentType == 'PatientId'")
public class PatientId extends CouchEntity {
    public PatientId() {
    }

    public PatientId(String _id) {
        this.setId(_id);
    }

}
