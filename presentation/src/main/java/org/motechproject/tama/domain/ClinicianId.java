package org.motechproject.tama.domain;

import org.ektorp.support.TypeDiscriminator;

@TypeDiscriminator("doc.documentType == 'ClinicianId'")
public class ClinicianId extends CouchEntity {

    public ClinicianId() {
    }

    public ClinicianId(String _id) {
        this.setId(_id);
    }
}
