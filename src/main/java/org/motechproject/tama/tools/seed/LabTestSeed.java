package org.motechproject.tama.tools.seed;

import org.motechproject.tama.domain.LabTest;
import org.motechproject.tama.repository.LabTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LabTestSeed extends Seed{

    @Autowired
    private LabTests labTests;

    @Override
    public void load() {
        labTests.add(LabTest.newLabTest("CD4", "xx-yy"));
        labTests.add(LabTest.newLabTest("PVL", "xx-yy"));
    }
}
