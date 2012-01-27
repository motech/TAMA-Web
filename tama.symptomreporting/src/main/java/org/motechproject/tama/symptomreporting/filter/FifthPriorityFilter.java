package org.motechproject.tama.symptomreporting.filter;

import org.springframework.stereotype.Component;

@Component
public class FifthPriorityFilter extends TreeNodeFilter {

    public FifthPriorityFilter() {
        super("adv_tingpainfeetcropanto", "adv_tingpainfeetcro",
                "adv_crocin02", "adv_crocin03", "adv_crocinpanto01",
                "adv_crocinpanto02", "adv_halfhourcontmed01",
                "adv_halfhourcro01", "adv_halfhourcrocinpanto01",
                "adv_halfhourpanto01", "adv_levo01",
                "adv_levopanto01", "adv_panto01",
                "adv_panto02", "adv_tingpainfeet",
                "adv_tingpainfeetpanto");
    }
}
