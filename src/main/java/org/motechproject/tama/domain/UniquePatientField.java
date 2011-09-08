package org.motechproject.tama.domain;

import org.ektorp.support.TypeDiscriminator;

@TypeDiscriminator("doc.documentType == 'UniquePatientField'")
public class UniquePatientField extends CouchEntity {

    public UniquePatientField() {
    }

    public UniquePatientField(String _id) {
        this.setId(_id);
    }
}
