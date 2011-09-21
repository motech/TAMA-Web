package org.motechproject.tama.ivr.decisiontree.filter.alerts;

import ch.lambdaj.Lambda;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.tama.ivr.decisiontree.filter.DecisionTreeNodesFilter;
import org.springframework.stereotype.Component;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;

@Component
public class FifthPrioritySymptomReportingFilter extends DecisionTreeNodesFilter {
    @Override
    public boolean select(Node node) {
        return Lambda.select(node.getPrompts(), having(on(Prompt.class).getName(),
                anyOf(equalTo("adv_stopmedicineseeclinicasap"),
                        equalTo("adv_seeclinicasapdepression")))).size() > 0;
    }
}
