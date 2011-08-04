package org.motechproject.tama.tools.seed;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.domain.Drug;
import org.motechproject.tama.domain.Regimen;
import org.motechproject.tama.domain.RegimenComposition;
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
        RegimenComposition regimen2Drug1 = regimenComposition(drugs, "TDF+3TC", "EFV");
        RegimenComposition regimen2Drug2 = regimenComposition(drugs, "TDF+FTC", "EFV");
        RegimenComposition regimen2Drug3 = regimenComposition(drugs, "TDF+3TC+EFV");
        RegimenComposition regimen2Drug4 = regimenComposition(drugs, "TDF+FTC+EFV");
        regimen2Drug3.addDerivedComposition(regimen2Drug1.getRegimenCompositionId());
        regimen2Drug4.addDerivedComposition(regimen2Drug2.getRegimenCompositionId());

        regimen2.addComposition(regimen2Drug1, regimen2Drug2, regimen2Drug3, regimen2Drug4);
        regimens.add(regimen2);

        Regimen regimen3 = new Regimen("Regimen III", "AZT + 3TC + NVP");
        RegimenComposition regimen3Drug1 = regimenComposition(drugs, "AZT+3TC+NVP");
        RegimenComposition regimen3Drug2 = regimenComposition(drugs, "AZT+3TC", "NVP");
        regimen3Drug1.addDerivedComposition(regimen3Drug2.getRegimenCompositionId());
        regimen3.addComposition(regimen3Drug1, regimen3Drug2);
        regimens.add(regimen3);

        Regimen regimen4 = new Regimen("Regimen IV", "AZT + 3TC + EFV");
        regimen4.addComposition(regimenComposition(drugs, "AZT+3TC", "EFV"));
        regimens.add(regimen4);

        Regimen regimen5 = new Regimen("Regimen V", "d4T + 3TC + NVP");
        RegimenComposition regimen5Drug2 = regimenComposition(drugs, "d4T+3TC", "NVP");
        RegimenComposition regimen5Drug1 = regimenComposition(drugs, "d4T+3TC+NVP");
        regimen5Drug1.addDerivedComposition(regimen5Drug2.getRegimenCompositionId());
        regimen5.addComposition(regimen5Drug2, regimen5Drug1);
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
        regimenComposition.setDisplayName(StringUtils.join(drugNames, " / "));
        return regimenComposition;
    }
}
