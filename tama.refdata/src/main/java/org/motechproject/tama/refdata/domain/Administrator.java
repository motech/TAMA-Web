package org.motechproject.tama.refdata.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tama.common.domain.BasicTAMAUser;

@TypeDiscriminator("doc.documentType == 'Administrator'")
public class Administrator extends BasicTAMAUser {

    public Administrator() {
    }

    public Administrator(String name, String username, String password) {
        super(name, username, password);
    }
}
