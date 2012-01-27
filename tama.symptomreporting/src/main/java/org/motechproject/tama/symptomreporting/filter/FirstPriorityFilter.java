package org.motechproject.tama.symptomreporting.filter;

import org.springframework.stereotype.Component;

@Component
public class FirstPriorityFilter extends TreeNodeFilter {

    public FirstPriorityFilter() {
        super("adv_crocin01", "adv_noteatanythg");
    }
}
