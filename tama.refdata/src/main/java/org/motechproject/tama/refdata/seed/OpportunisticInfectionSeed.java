package org.motechproject.tama.refdata.seed;

import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.tama.refdata.domain.OpportunisticInfection;
import org.motechproject.tama.refdata.repository.AllOpportunisticInfections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OpportunisticInfectionSeed {

    @Autowired
    private AllOpportunisticInfections allOpportunisticInfections;

    @Seed(version = "1.0", priority = 0)
    public void load() {
        allOpportunisticInfections.add(OpportunisticInfection.newOpportunisticInfection("Addison’s Disease"));
        allOpportunisticInfections.add(OpportunisticInfection.newOpportunisticInfection("Anemia"));
        allOpportunisticInfections.add(OpportunisticInfection.newOpportunisticInfection("Bacterial Infection of Skin"));
        allOpportunisticInfections.add(OpportunisticInfection.newOpportunisticInfection("Convulsions"));
        allOpportunisticInfections.add(OpportunisticInfection.newOpportunisticInfection("Dementia"));
        allOpportunisticInfections.add(OpportunisticInfection.newOpportunisticInfection("Encephalitis"));
        allOpportunisticInfections.add(OpportunisticInfection.newOpportunisticInfection("Gastroenteropathy"));
        allOpportunisticInfections.add(OpportunisticInfection.newOpportunisticInfection("Hypertension"));
        allOpportunisticInfections.add(OpportunisticInfection.newOpportunisticInfection("Liver Abscess"));
        allOpportunisticInfections.add(OpportunisticInfection.newOpportunisticInfection("Malaria"));
        allOpportunisticInfections.add(OpportunisticInfection.newOpportunisticInfection("Non Healing Ulcer"));
        allOpportunisticInfections.add(OpportunisticInfection.newOpportunisticInfection("Oral Candidiasis"));
        allOpportunisticInfections.add(OpportunisticInfection.newOpportunisticInfection("Pancreatitis"));
        allOpportunisticInfections.add(OpportunisticInfection.newOpportunisticInfection("Scabies"));
        allOpportunisticInfections.add(OpportunisticInfection.newOpportunisticInfection("TB Meningitis"));
        allOpportunisticInfections.add(OpportunisticInfection.newOpportunisticInfection("Other"));
    }
}
