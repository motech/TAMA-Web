package org.motechproject.tama.symptomreporting.filter;

import org.springframework.stereotype.Component;

@Component
public class FourthPriorityFilter extends TreeNodeFilter {

    public FourthPriorityFilter() {
        super("adv_callclinic");
    }
}
