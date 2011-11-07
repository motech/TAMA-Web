package org.motechproject.tama.tools.seed;

import org.motechproject.tama.domain.ModeOfTransmission;
import org.motechproject.tama.repository.AllModesOfTransmission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModeOfTransmissionSeed extends Seed {

	@Autowired
	private AllModesOfTransmission allModesOfTransmission;
	
	@Override
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
	}
}