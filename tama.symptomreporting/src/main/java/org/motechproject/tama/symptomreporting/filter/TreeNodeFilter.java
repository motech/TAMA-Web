package org.motechproject.tama.symptomreporting.filter;

import ch.lambdaj.Lambda;
import org.apache.commons.lang.ArrayUtils;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.isOneOf;

@Component
public class TreeNodeFilter extends DecisionTreeNodesFilter {

    protected String[] criteria;

    private TreeNodeFilter() {
    }

    public TreeNodeFilter(String... criteria) {
        this.criteria = criteria;
    }

    @Override
    public boolean select(Node node) {
        return Lambda.select(node.getPrompts(), having(on(Prompt.class).getName(),
                isOneOf(criteria))).size() > 0;
    }

    protected void addCriteria(String... criteria){
        this.criteria = (String[])ArrayUtils.addAll(this.criteria, criteria);
    }

    protected String[] getCriteria() {
        return Arrays.copyOf(criteria, criteria.length);
    }
}
