package org.motechproject.tama.symptomreporting.decisiontree.filter;

import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.springframework.stereotype.Component;

@Component
public class RegExpBasedTreeNodeFilter extends DecisionTreeNodesFilter {

    private String includeFilter;

    private RegExpBasedTreeNodeFilter() {
    }

    public RegExpBasedTreeNodeFilter(String includeFilter) {
        this.includeFilter = includeFilter;
    }

    @Override
    public boolean select(Node node) {
        for (Prompt prompt : node.getPrompts()) {
            return prompt.getName().matches("cy_.*");
        }
        return false;
    }
}
