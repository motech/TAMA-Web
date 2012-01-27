package org.motechproject.tama.symptomreporting.filter;

import org.springframework.stereotype.Component;

@Component
public class SecondPriorityFilter extends TreeNodeFilter {

    public SecondPriorityFilter() {
        super("adv_stopmedicineseeclinicasap", "adv_seeclinicasapdepression");
    }
}
