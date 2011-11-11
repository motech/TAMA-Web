package org.motechproject.tama.ivr.decisiontree.filter.alerts;

import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.tama.ivr.decisiontree.filter.DecisionTreeNodesFilter;
import org.springframework.stereotype.Component;

@Component
public class SecondPrioritySymptomReportingFilter extends DecisionTreeNodesFilter {
    @Override
    public boolean select(Node node) {
        boolean appropriateNode = false;
        for (Prompt prompt : node.getPrompts()) {
            if ("adv_stopmedicineseeclinicasap".equals(prompt.getName()) || "adv_seeclinicasapdepression".equals(prompt.getName())) {
                appropriateNode = true;
                break;
            }
        }
        return appropriateNode;
    }
}
