package org.motechproject.tama.ivr.decisiontree.filter.alerts;

import ch.lambdaj.Lambda;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.tama.ivr.decisiontree.filter.DecisionTreeNodesFilter;
import org.springframework.stereotype.Component;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.isOneOf;

@Component
public class FirstPrioritySymptomReportingFilter extends DecisionTreeNodesFilter {

    @Override
    public boolean select(Node node) {
        return Lambda.select(node.getPrompts(), having(on(Prompt.class).getName(),
                isOneOf("adv_noteatanythg", "adv_crocin01"))).size() > 0;
    }
}