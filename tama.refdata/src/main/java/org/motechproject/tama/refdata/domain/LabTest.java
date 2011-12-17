package org.motechproject.tama.refdata.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tamacommon.TAMAConstants;
import org.motechproject.tamacommon.domain.CouchEntity;

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

    public static LabTest newLabTest(TAMAConstants.LabTestType labTestType, String normalRange) {
        LabTest labTest = new LabTest();
        labTest.setName(labTestType.getName());
        labTest.setNormalRange(normalRange);
        return labTest;
    }
}
