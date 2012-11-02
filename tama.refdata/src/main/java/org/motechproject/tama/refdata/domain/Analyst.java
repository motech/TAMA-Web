package org.motechproject.tama.refdata.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tama.common.domain.BasicTAMAUser;

@TypeDiscriminator("doc.documentType == 'Analyst'")
public class Analyst extends BasicTAMAUser {

    public Analyst() {
    }

    public Analyst(String name, String username, String password) {
        super(name, username, password);
    }
}
