package org.motechproject.tama.symptomreporting.filter;

import org.springframework.stereotype.Component;

@Component
public class ThirdPriorityFilter extends TreeNodeFilter {

    public ThirdPriorityFilter() {
        super("adv_continuemedicineseeclinicasap");
    }
}
