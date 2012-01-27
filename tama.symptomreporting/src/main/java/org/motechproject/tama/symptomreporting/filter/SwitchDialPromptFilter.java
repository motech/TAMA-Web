package org.motechproject.tama.symptomreporting.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SwitchDialPromptFilter extends TreeNodeFilter {

    @Autowired
    public SwitchDialPromptFilter(FirstPriorityFilter firstPriorityFilter, SecondPriorityFilter secondPriorityFilter) {
        super();
        addCriteria(firstPriorityFilter.getCriteria());
        addCriteria(secondPriorityFilter.getCriteria());
    }
}
