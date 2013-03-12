package org.motechproject.tama.refdata.seed;

import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.tama.refdata.domain.ModeOfTransmission;
import org.motechproject.tama.refdata.objectcache.AllModesOfTransmissionCache;
import org.motechproject.tama.refdata.repository.AllModesOfTransmission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModeOfTransmissionSeed {

    @Autowired
    private AllModesOfTransmission allModesOfTransmission;

    @Autowired
    private AllModesOfTransmissionCache allModesOfTransmissionCache;

    @Seed(version = "1.0", priority = 0)
    public void load() {
        allModesOfTransmission.add(ModeOfTransmission.newModeOfTransmission("Heterosexual"));
        allModesOfTransmission.add(ModeOfTransmission.newModeOfTransmission("Homosexual"));
        allModesOfTransmission.add(ModeOfTransmission.newModeOfTransmission("Blood Transmission"));
        allModesOfTransmission.add(ModeOfTransmission.newModeOfTransmission("Intravenous"));
        allModesOfTransmission.add(ModeOfTransmission.newModeOfTransmission("Intramuscular"));
        allModesOfTransmission.add(ModeOfTransmission.newModeOfTransmission("Dialysis"));
        allModesOfTransmission.add(ModeOfTransmission.newModeOfTransmission("Vertical"));
        allModesOfTransmission.add(ModeOfTransmission.newModeOfTransmission("Bisexual"));
        allModesOfTransmission.add(ModeOfTransmission.newModeOfTransmission("Others"));
        allModesOfTransmissionCache.refresh();
    }
}