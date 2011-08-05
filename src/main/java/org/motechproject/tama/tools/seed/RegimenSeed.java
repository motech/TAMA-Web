package org.motechproject.tama.tools.seed;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.domain.Drug;
import org.motechproject.tama.domain.Regimen;
import org.motechproject.tama.domain.DrugComposition;
import org.motechproject.tama.repository.Regimens;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
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
        DrugComposition regimen2DrugComposition1 = regimenComposition(drugs, "TDF+3TC", "EFV");
        DrugComposition regimen2DrugComposition2 = regimenComposition(drugs, "TDF+FTC", "EFV");
        DrugComposition regimen2DrugComposition3 = regimenComposition(drugs, "TDF+3TC+EFV");
        DrugComposition regimen2DrugComposition4 = regimenComposition(drugs, "TDF+FTC+EFV");

        regimen2.addComposition(regimen2DrugComposition1, regimen2DrugComposition2, regimen2DrugComposition3, regimen2DrugComposition4);
        regimens.add(regimen2);

        Regimen regimen3 = new Regimen("Regimen III", "AZT + 3TC + NVP");
        DrugComposition regimen3DrugComposition1 = regimenComposition(drugs, "AZT+3TC+NVP");
        DrugComposition regimen3DrugComposition2 = regimenComposition(drugs, "AZT+3TC", "NVP");

        regimen3.addComposition(regimen3DrugComposition1, regimen3DrugComposition2);
        regimens.add(regimen3);

        Regimen regimen4 = new Regimen("Regimen IV", "AZT + 3TC + EFV");
        regimen4.addComposition(regimenComposition(drugs, "AZT+3TC", "EFV"));
        regimens.add(regimen4);

        Regimen regimen5 = new Regimen("Regimen V", "d4T + 3TC + NVP");
        DrugComposition regimen5DrugComposition2 = regimenComposition(drugs, "d4T+3TC", "NVP");
        DrugComposition regimen5DrugComposition1 = regimenComposition(drugs, "d4T+3TC+NVP");

        regimen5.addComposition(regimen5DrugComposition2, regimen5DrugComposition1);
        regimens.add(regimen5);

        Regimen regimen6 = new Regimen("Regimen VI", "d4T + 3TC + EFV");
        regimen6.addComposition(regimenComposition(drugs, "d4T+3TC", "EFV"));
        regimens.add(regimen6);

    }

    private DrugComposition regimenComposition(Map<String, Drug> drugs, String... drugNames) {
        DrugComposition drugComposition = new DrugComposition();
        for (String drugName : drugNames) {
            drugComposition.addDrug(drugs.get(drugName));
        }
        drugComposition.setDisplayName(StringUtils.join(drugNames, " + "));
        return drugComposition;
    }
}
