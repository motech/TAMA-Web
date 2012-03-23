package org.motechproject.tama.refdata.objectcache;

import org.motechproject.tama.refdata.domain.ModeOfTransmission;
import org.motechproject.tama.refdata.repository.AllModesOfTransmission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AllModesOfTransmissionCache extends Cachable<ModeOfTransmission>{

    @Autowired
    public AllModesOfTransmissionCache(AllModesOfTransmission allModesOfTransmission) {
        super(allModesOfTransmission);
    }

    @Override
    protected String getKey(ModeOfTransmission modeOfTransmission) {
        return modeOfTransmission.getId();
    }
}
