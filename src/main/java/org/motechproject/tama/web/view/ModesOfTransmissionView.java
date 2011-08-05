package org.motechproject.tama.web.view;


import org.motechproject.tama.domain.ModeOfTransmission;
import org.motechproject.tama.repository.ModesOfTransmission;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ModesOfTransmissionView {

    private final ModesOfTransmission modesOfTransmission;

    public ModesOfTransmissionView(ModesOfTransmission modesOfTransmission){
        this.modesOfTransmission = modesOfTransmission;
    }

    public List<ModeOfTransmission> getAll() {
        List<ModeOfTransmission> allModesOfTransmission = modesOfTransmission.getAll();
        Collections.sort(allModesOfTransmission, new Comparator<ModeOfTransmission>() {

            @Override
            public int compare(ModeOfTransmission modeOfTransmission, ModeOfTransmission otherModeOfTransmission) {
                return modeOfTransmission.getType().toLowerCase().compareTo(otherModeOfTransmission.getType().toLowerCase());
            }
        });
        return allModesOfTransmission;
    }
}
