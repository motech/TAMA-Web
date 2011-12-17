package org.motechproject.tama.refdata.seed;

import org.motechproject.tama.refdata.domain.LabTest;
import org.motechproject.tama.refdata.repository.AllLabTests;
import org.motechproject.tamacommon.TAMAConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LabTestSeed extends Seed {

    @Autowired
    private AllLabTests allLabTests;

    @Override
    public void load() {
        allLabTests.add(LabTest.newLabTest(TAMAConstants.LabTestType.CD4, ">500"));
        allLabTests.add(LabTest.newLabTest(TAMAConstants.LabTestType.PVL, "0"));
    }
}
