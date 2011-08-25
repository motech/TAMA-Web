package org.motechproject.tama.domain;

import org.ektorp.support.TypeDiscriminator;

@TypeDiscriminator("doc.documentType == 'LabTest'")
public class LabTest extends CouchEntity {

    String name;

    String normalValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNormalValue() {
        return normalValue;
    }

    public void setNormalValue(String normalValue) {
        this.normalValue = normalValue;
    }
}
