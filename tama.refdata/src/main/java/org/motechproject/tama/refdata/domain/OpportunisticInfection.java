package org.motechproject.tama.refdata.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tama.common.domain.CouchEntity;

@TypeDiscriminator("doc.documentType == 'OpportunisticInfection'")
public class OpportunisticInfection extends CouchEntity {

    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static OpportunisticInfection newOpportunisticInfection(String name) {
        OpportunisticInfection opportunisticInfection = new OpportunisticInfection();
        opportunisticInfection.setName(name);
        return opportunisticInfection;
    }
}
