package org.motechproject.tama.refdata.seed;

import org.motechproject.tama.refdata.domain.Drug;
import org.motechproject.tama.refdata.domain.DrugComposition;
import org.motechproject.tama.refdata.domain.DrugCompositionGroup;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.repository.AllRegimens;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RegimenSeed extends Seed {

    @Autowired
    private AllRegimens allRegimens;
    @Autowired
    private DrugSeed drugSeed;

    @Override
    public void load() {
        Map<String, Drug> drugs = drugSeed.loadAll();

        Regimen regimen1 = new Regimen("Regimen I", "TDF + 3TC / fTC + NVP");
        regimen1.addCompositionGroup(
                drugCompositionGroup("TDF+3TC+NVP", drugComposition(drugs, "TDF+3TC", "NVP")),
                drugCompositionGroup("TDF+FTC+NVP", drugComposition(drugs, "TDF+FTC", "NVP"))
        );
        allRegimens.add(regimen1);

        Regimen regimen2 = new Regimen("Regimen II", "TDF + 3TC / fTC + EFV");
        regimen2.addCompositionGroup(
                drugCompositionGroup("TDF+3TC+EFV", drugComposition(drugs, "TDF+3TC", "EFV"), drugComposition(drugs, "TDF+3TC+EFV")),
                drugCompositionGroup("TDF+FTC+EFV", drugComposition(drugs, "TDF+FTC", "EFV"), drugComposition(drugs, "TDF+FTC+EFV"))
        );
        allRegimens.add(regimen2);

        Regimen regimen3 = new Regimen("Regimen III", "AZT + 3TC + NVP");
        regimen3.addCompositionGroup(
                drugCompositionGroup("AZT+3TC+NVP", drugComposition(drugs, "AZT+3TC+NVP"), drugComposition(drugs, "AZT+3TC", "NVP"))
        );
        allRegimens.add(regimen3);

        Regimen regimen4 = new Regimen("Regimen IV", "AZT + 3TC + EFV");
        regimen4.addCompositionGroup(
                drugCompositionGroup("AZT+3TC+EFV", drugComposition(drugs, "AZT+3TC", "EFV"))
        );
        allRegimens.add(regimen4);

        Regimen regimen5 = new Regimen("Regimen V", "d4T + 3TC + NVP");
        regimen5.addCompositionGroup(
                drugCompositionGroup("d4T+3TC+NVP", drugComposition(drugs, "d4T+3TC", "NVP"), drugComposition(drugs, "d4T+3TC+NVP"))
        );
        allRegimens.add(regimen5);

        Regimen regimen6 = new Regimen("Regimen VI", "d4T + 3TC + EFV");
        regimen6.addCompositionGroup(
                drugCompositionGroup("d4T+3TC+EFV", drugComposition(drugs, "d4T+3TC", "EFV"))
        );
        allRegimens.add(regimen6);
    }

    private DrugComposition drugComposition(Map<String, Drug> drugs, String... drugNames) {
        DrugComposition drugComposition = new DrugComposition();
        for (String drugName : drugNames) {
            final Drug drug = drugs.get(drugName);
            drugComposition.addDrugId(drug);
        }
        drugComposition.setDisplayName(drugNames[0]);
        return drugComposition;
    }

    private DrugCompositionGroup drugCompositionGroup(String name, DrugComposition... drugCompositions) {
        DrugCompositionGroup drugCompositionGroup = new DrugCompositionGroup();
        drugCompositionGroup.setName(name);
        for (DrugComposition drugComposition : drugCompositions) {
            drugCompositionGroup.addComposition(drugComposition);
        }
        return drugCompositionGroup;
    }
}
