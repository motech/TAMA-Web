package org.motechproject.tama.ivr.decisiontree.filter.alerts;

import ch.lambdaj.Lambda;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.tama.ivr.decisiontree.filter.DecisionTreeNodesFilter;
import org.springframework.stereotype.Component;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;

@Component
public class FifthPrioritySymptomReportingFilter extends DecisionTreeNodesFilter {
    @Override
    public boolean select(Node node) {
        return Lambda.select(node.getPrompts(), having(on(Prompt.class).getName(),
                anyOf(equalTo("adv_tingpainfeetcropanto"),
                        equalTo("adv_tingpainfeetcro"),
                        equalTo("adv_crocin02"),
                        equalTo("adv_crocin03"),
                        equalTo("adv`_crocinpanto01"),
                        equalTo("adv_crocinpanto02"),
                        equalTo("adv_halfhourcontmed01"),
                        equalTo("adv_halfhourcro01"),
                        equalTo("adv_halfhourcrocinpanto01"),
                        equalTo("adv_halfhourpanto01"),
                        equalTo("adv_levo01"),
                        equalTo("adv_levopanto01"),
                        equalTo("adv_panto01"),
                        equalTo("adv_panto02"),
                        equalTo("adv_tingpainfeet"),
                        equalTo("adv_tingpainfeetpanto")))).size() > 0;
    }
}
