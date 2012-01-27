package org.motechproject.tama.symptomreporting.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SMSFilter extends TreeNodeFilter {

    @Autowired
    public SMSFilter(FirstPriorityFilter firstPriorityFilter, FifthPriorityFilter fifthPriorityFilter) {
        super();
        addCriteria(firstPriorityFilter.getCriteria());
        addCriteria(fifthPriorityFilter.getCriteria());
    }
}
