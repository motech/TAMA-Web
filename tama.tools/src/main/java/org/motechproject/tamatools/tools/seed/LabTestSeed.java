package org.motechproject.tamatools.tools.seed;

import org.motechproject.tamacommon.TAMAConstants;
import org.motechproject.tamadomain.domain.LabTest;
import org.motechproject.tamadomain.repository.AllLabTests;
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
