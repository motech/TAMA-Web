package org.motechproject.tama.tools.seed;

import org.motechproject.tama.domain.DosageType;
import org.motechproject.tama.repository.DosageTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DosageTypeSeed extends Seed {

	@Autowired
	private DosageTypes dosageTypes;
	
	@Override
	public void load() {
		dosageTypes.add(new DosageType("Morning Daily"));
		dosageTypes.add(new DosageType("Evening Daily"));
		dosageTypes.add(new DosageType("Twice Daily"));
	}
}
