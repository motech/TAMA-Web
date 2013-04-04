package org.motechproject.tama.refdata.seed;

import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.tama.common.domain.TAMAMessageTypes;
import org.motechproject.tama.refdata.domain.HealthTip;
import org.motechproject.tama.refdata.repository.AllHealthTips;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HealthTipSeed {

    @Autowired
    private AllHealthTips healthTips;

    @Seed(version = "2.0", priority = 0)
    public void load() {
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.ALL_MESSAGES.getDisplayName(), "HT016a", 6, 6, 159, 159));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.ALL_MESSAGES.getDisplayName(), "HT050", 7, 7, 141, 141));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.ALL_MESSAGES.getDisplayName(), "HT051", 2, 2, 103, 103));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.ALL_MESSAGES.getDisplayName(), "HT052", 3, 3, 112, 112));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.ALL_MESSAGES.getDisplayName(), "HT053", 5, 5, 121, 121));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.ALL_MESSAGES.getDisplayName(), "HT054", 4, 4, 117, 117));

        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.FAMILY_AND_CHILDREN.getDisplayName(), "HT006a", 1, 1, 132, 132));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.FAMILY_AND_CHILDREN.getDisplayName(), "HT038a", 4, 4, 185, 185));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.FAMILY_AND_CHILDREN.getDisplayName(), "HT039a", 2, 2, 104, 104));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.FAMILY_AND_CHILDREN.getDisplayName(), "HT055", 7, 7, 133, 133));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.FAMILY_AND_CHILDREN.getDisplayName(), "HT056", 8, 8, 125, 125));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.FAMILY_AND_CHILDREN.getDisplayName(), "HT057", 3, 3, 105, 105));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.FAMILY_AND_CHILDREN.getDisplayName(), "HT058", 6, 6, 151, 151));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.FAMILY_AND_CHILDREN.getDisplayName(), "HT059", 5, 5, 181, 181));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.FAMILY_AND_CHILDREN.getDisplayName(), "HT060", 10, 10, 129, 129));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.FAMILY_AND_CHILDREN.getDisplayName(), "HT061", 9, 9, 139, 139));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.FAMILY_AND_CHILDREN.getDisplayName(), "HT062", 12, 12, 134, 134));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.FAMILY_AND_CHILDREN.getDisplayName(), "HT063", 11, 11, 160, 160));

        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.NUTRITION_AND_LIFESTYLE.getDisplayName(), "HT034a", 1, 1, 171, 171));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.NUTRITION_AND_LIFESTYLE.getDisplayName(), "HT035a", 3, 3, 107, 107));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.NUTRITION_AND_LIFESTYLE.getDisplayName(), "HT036a", 4, 4, 108, 108));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.NUTRITION_AND_LIFESTYLE.getDisplayName(), "HT037a", 9, 9, 168, 168));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.NUTRITION_AND_LIFESTYLE.getDisplayName(), "HT064", 5, 5, 101, 101));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.NUTRITION_AND_LIFESTYLE.getDisplayName(), "HT065", 6, 6, 126, 126));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.NUTRITION_AND_LIFESTYLE.getDisplayName(), "HT066", 7, 7, 158, 158));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.NUTRITION_AND_LIFESTYLE.getDisplayName(), "HT067", 11, 11, 173, 173));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.NUTRITION_AND_LIFESTYLE.getDisplayName(), "HT068", 10, 10, 110, 110));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.NUTRITION_AND_LIFESTYLE.getDisplayName(), "HT069", 2, 2, 118, 118));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.NUTRITION_AND_LIFESTYLE.getDisplayName(), "HT070", 8, 8, 137, 137));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.NUTRITION_AND_LIFESTYLE.getDisplayName(), "HT071", 12, 12, 111, 111));

        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.SYMPTOMS.getDisplayName(), "HT072", 8, 8, 106, 106));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.SYMPTOMS.getDisplayName(), "HT073", 9, 9, 109, 109));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.SYMPTOMS.getDisplayName(), "HT074", 10, 10, 114, 114));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.SYMPTOMS.getDisplayName(), "HT075", 11, 11, 120, 120));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.SYMPTOMS.getDisplayName(), "HT076", 12, 12, 127, 127));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.SYMPTOMS.getDisplayName(), "HT077", 13, 13, 130, 130));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.SYMPTOMS.getDisplayName(), "HT078", 14, 14, 124, 124));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.SYMPTOMS.getDisplayName(), "HT079", 15, 15, 138, 138));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.SYMPTOMS.getDisplayName(), "HT080", 16, 16, 162, 162));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.SYMPTOMS.getDisplayName(), "HT081", 17, 17, 145, 145));

        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.SYMPTOMS.getDisplayName(), "HT040a", 4, 4, 147, 147));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.SYMPTOMS.getDisplayName(), "HT019a", 3, null, 154, null));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.SYMPTOMS.getDisplayName(), "HT020a", null, 3, null, 154));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.SYMPTOMS.getDisplayName(), "HT082", 1, 1, 113, 113));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.SYMPTOMS.getDisplayName(), "HT083", 2, 2, 102, 102));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.SYMPTOMS.getDisplayName(), "HT084", 7, 7, 140, 140));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.SYMPTOMS.getDisplayName(), "HT085", 5, 5, 143, 143));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.SYMPTOMS.getDisplayName(), "HT086", 6, 6, 136, 136));

        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.ADHERENCE.getDisplayName(), "HT011a", 6, 6, 157, 157));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.ADHERENCE.getDisplayName(), "HT012a", 2, 2, 161, 161));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.ADHERENCE.getDisplayName(), "HT013a", 4, 4, 135, 135));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.ADHERENCE.getDisplayName(), "HT014a", 5, null, 131, null));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.ADHERENCE.getDisplayName(), "HT015a", null, 5, null, 131));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.ADHERENCE.getDisplayName(), "HT017a", 8, 8, 167, 167));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.ADHERENCE.getDisplayName(), "HT018a", 9, 9, 153, 153));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.ADHERENCE.getDisplayName(), "HT021a", 7, null, 175, null));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.ADHERENCE.getDisplayName(), "HT022a", null, 7, null, 175));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.ADHERENCE.getDisplayName(), "HT087", 3, 3, 142, 142));

        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.ART_AND_CD4.getDisplayName(), "HT001a", 3, 3, 123, 123));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.ART_AND_CD4.getDisplayName(), "HT002a", 4, 4, 148, 148));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.ART_AND_CD4.getDisplayName(), "HT003a", 7, 7, 144, 144));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.ART_AND_CD4.getDisplayName(), "HT004a", 5, 5, 128, 128));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.ART_AND_CD4.getDisplayName(), "HT005a", 6, 6, 150, 150));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.ART_AND_CD4.getDisplayName(), "HT088", 2, 2, 146, 146));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.ART_AND_CD4.getDisplayName(), "HT089", 1, 1, 116, 116));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.ART_AND_CD4.getDisplayName(), "HT008a", 8, 8, 149, 149));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.ART_AND_CD4.getDisplayName(), "HT009a", 10, 10, 169, 169));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.ART_AND_CD4.getDisplayName(), "HT090", 11, 11, 155, 155));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.ART_AND_CD4.getDisplayName(), "HT091", 9, 9, 182, 182));

        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.LIVING_WITH_HIV.getDisplayName(), "HT033a", 3, 3, 174, 174));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.LIVING_WITH_HIV.getDisplayName(), "HT033b", 8, 8, 183, 183));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.LIVING_WITH_HIV.getDisplayName(), "HT092", 2, 2, 152, 152));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.LIVING_WITH_HIV.getDisplayName(), "HT093", 4, 4, 115, 115));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.LIVING_WITH_HIV.getDisplayName(), "HT094", 1, 1, 119, 119));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.LIVING_WITH_HIV.getDisplayName(), "HT095", 7, 7, 156, 156));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.LIVING_WITH_HIV.getDisplayName(), "HT096", 10, 10, 184, 184));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.LIVING_WITH_HIV.getDisplayName(), "HT097", 5, 5, 122, 122));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.LIVING_WITH_HIV.getDisplayName(), "HT098", 9, 9, 172, 172));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageTypes.LIVING_WITH_HIV.getDisplayName(), "HT099", 6, 6, 170, 170));


    }
}
