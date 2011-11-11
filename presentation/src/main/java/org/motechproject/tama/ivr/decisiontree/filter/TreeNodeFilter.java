package org.motechproject.tama.ivr.decisiontree.filter;

import ch.lambdaj.Lambda;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.springframework.stereotype.Component;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.isOneOf;

@Component
public class TreeNodeFilter extends DecisionTreeNodesFilter {

    String[] prompts;

    private TreeNodeFilter() {
    }

    public TreeNodeFilter(String... prompts) {
        this.prompts = prompts;
    }

    @Override
    public boolean select(Node node) {
        return Lambda.select(node.getPrompts(), having(on(Prompt.class).getName(),
                isOneOf(prompts))).size() > 0;
    }
}
