package org.motechproject.tama.tools.seed;

import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.domain.LabTest;
import org.motechproject.tama.repository.AllLabTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LabTestSeed extends Seed{

    @Autowired
    private AllLabTests allLabTests;

    @Override
    public void load() {
        allLabTests.add(LabTest.newLabTest(TAMAConstants.LabTestType.CD4, ">500"));
        allLabTests.add(LabTest.newLabTest(TAMAConstants.LabTestType.PVL, "0"));
    }
}
