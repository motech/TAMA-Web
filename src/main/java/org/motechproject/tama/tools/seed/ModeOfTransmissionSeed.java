package org.motechproject.tama.tools.seed;

import org.motechproject.tama.domain.ModeOfTransmission;
import org.motechproject.tama.repository.ModesOfTransmission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModeOfTransmissionSeed extends Seed {

	@Autowired
	private ModesOfTransmission modesOfTransmission;
	
	@Override
	public void load() {
		modesOfTransmission.add(ModeOfTransmission.newModeOfTransmission("Heterosexual"));
		modesOfTransmission.add(ModeOfTransmission.newModeOfTransmission("Homosexual"));
		modesOfTransmission.add(ModeOfTransmission.newModeOfTransmission("Blood Transmission"));
		modesOfTransmission.add(ModeOfTransmission.newModeOfTransmission("Intravenous"));
		modesOfTransmission.add(ModeOfTransmission.newModeOfTransmission("Intramuscular"));
		modesOfTransmission.add(ModeOfTransmission.newModeOfTransmission("Dialysis"));
		modesOfTransmission.add(ModeOfTransmission.newModeOfTransmission("Vertical"));
		modesOfTransmission.add(ModeOfTransmission.newModeOfTransmission("Bisexual"));
		modesOfTransmission.add(ModeOfTransmission.newModeOfTransmission("Others"));
	}
}