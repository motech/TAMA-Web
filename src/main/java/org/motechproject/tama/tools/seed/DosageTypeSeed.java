package org.motechproject.tama.tools.seed;

import org.motechproject.tama.domain.DosageType;
import org.motechproject.tama.repository.DosageTypes;
import org.springframework.beans.factory.annotation.Autowired;

public class DosageTypeSeed implements Seed {

	@Autowired
	private DosageTypes dosageTypes;
	
	@Override
	public void load() {
		dosageTypes.add(new DosageType("Once Daily"));
		dosageTypes.add(new DosageType("Twice Daily"));
	}

}
