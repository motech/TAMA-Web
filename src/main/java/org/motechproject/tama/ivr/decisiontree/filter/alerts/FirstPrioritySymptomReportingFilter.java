package org.motechproject.tama.ivr.decisiontree.filter.alerts;

import ch.lambdaj.Lambda;
import org.bouncycastle.util.Arrays;
import org.hamcrest.Matcher;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.tama.ivr.decisiontree.filter.DecisionTreeNodesFilter;
import org.springframework.stereotype.Component;

import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;

@Component
public class FirstPrioritySymptomReportingFilter extends DecisionTreeNodesFilter {

    @Override
    public boolean select(Node node) {
        return Lambda.select(node.getPrompts(), having(on(Prompt.class).getName(),
                anyOf(equalTo("adv_noteatanythg"), equalTo("adv_crocin01")))).size() > 0;
    }
}
