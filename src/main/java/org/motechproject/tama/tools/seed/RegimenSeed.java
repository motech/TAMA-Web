package org.motechproject.tama.tools.seed;

import java.util.Map;

import org.motechproject.tama.domain.Drug;
import org.motechproject.tama.domain.Regimen;
import org.motechproject.tama.domain.RegimenComposition;
import org.motechproject.tama.repository.Regimens;
import org.springframework.beans.factory.annotation.Autowired;

public class RegimenSeed extends Seed {

	@Autowired
	private Regimens regimens;
	@Autowired
	private DrugSeed drugSeed;
	
	@Override
	public void load() {
		
		Map<String, Drug> drugs = drugSeed.loadAll();
		
		Regimen regimen1 = new Regimen("Regimen I", "TDF + 3TC / fTC + NVP");
		regimen1.addComposition(regimenComposition(drugs, "TDF+3TC", "NVP"));
		regimen1.addComposition(regimenComposition(drugs, "TDF+FTC", "NVP"));
		regimens.add(regimen1);

		Regimen regimen2 = new Regimen("Regimen II", "TDF + 3TC / fTC + EFV");
		regimen2.addComposition(regimenComposition(drugs, "TDF+3TC", "EFV"));
		regimen2.addComposition(regimenComposition(drugs, "TDF+FTC", "EFV"));
		regimen2.addComposition(regimenComposition(drugs, "TDF+3TC+EFV"));
		regimen2.addComposition(regimenComposition(drugs, "TDF+FTC+EFV"));
		regimens.add(regimen2);

		Regimen regimen3 = new Regimen("Regimen III", "AZT + 3TC + NVP");
		regimen3.addComposition(regimenComposition(drugs, "AZT+3TC+NVP"));
		regimen3.addComposition(regimenComposition(drugs, "AZT+3TC", "NVP"));
		regimens.add(regimen3);

		Regimen regimen4 = new Regimen("Regimen IV", "AZT + 3TC + EFV");
		regimen4.addComposition(regimenComposition(drugs, "AZT+3TC", "EFV"));
		regimens.add(regimen4);
		
		Regimen regimen5 = new Regimen("Regimen V", "d4T + 3TC + NVP");
		regimen5.addComposition(regimenComposition(drugs, "d4T+3TC+NVP"));
		regimen5.addComposition(regimenComposition(drugs, "d4T+3TC", "NVP"));
		regimens.add(regimen5);
		
		Regimen regimen6 = new Regimen("Regimen VI", "d4T + 3TC + EFV");
		regimen6.addComposition(regimenComposition(drugs, "d4T+3TC", "EFV"));
		regimens.add(regimen6);
		
		
	}

	private RegimenComposition regimenComposition(Map<String, Drug> drugs, String... drugNames) {
		RegimenComposition regimenComposition = new RegimenComposition();
		for (String drugName : drugNames) {
			regimenComposition.addDrug(drugs.get(drugName));	
		}
		return regimenComposition;
	}
}
