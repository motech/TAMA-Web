package org.motechproject.tama.refdata.seed;

import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.refdata.domain.LabTest;
import org.motechproject.tama.refdata.repository.AllLabTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LabTestSeed {

    @Autowired
    private AllLabTests allLabTests;

    @Seed(version = "1.0", priority = 0)
    public void load() {
        allLabTests.add(LabTest.newLabTest(TAMAConstants.LabTestType.CD4, ">500"));
        allLabTests.add(LabTest.newLabTest(TAMAConstants.LabTestType.PVL, "0"));
    }
}
