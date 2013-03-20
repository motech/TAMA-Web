package org.motechproject.tama.symptomreporting.filter;

import org.springframework.stereotype.Component;

@Component
public class SuspendAdherenceCallsFilter extends TreeNodeFilter {

    public SuspendAdherenceCallsFilter() {
        super("adv_noteatanythg", "adv_stopmedicineseeclinicasap");
    }
}
