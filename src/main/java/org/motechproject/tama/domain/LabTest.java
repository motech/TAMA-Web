package org.motechproject.tama.domain;

import org.ektorp.support.TypeDiscriminator;

@TypeDiscriminator("doc.documentType == 'LabTest'")
public class LabTest extends CouchEntity {

    String name;

    String normalRange;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNormalRange() {
        return normalRange;
    }

    public void setNormalRange(String normalRange) {
        this.normalRange = normalRange;
    }

    public static LabTest newLabTest(String name, String normalRange) {
        LabTest labTest = new LabTest();
        labTest.setName(name);
        labTest.setNormalRange(normalRange);
        return labTest;
    }
}
