package org.motechproject.tamadomain.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tamacommon.domain.CouchEntity;

@TypeDiscriminator("doc.documentType == 'ClinicianId'")
public class ClinicianId extends CouchEntity {

    public ClinicianId() {
    }

    public ClinicianId(String _id) {
        this.setId(_id);
    }
}
