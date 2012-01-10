package org.motechproject.tama.symptomreporting.decisiontree.filter;

import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class RegExpBasedTreeNodeFilter extends DecisionTreeNodesFilter {

    private Pattern pattern;

    public RegExpBasedTreeNodeFilter(String includeFilter) {
        pattern = Pattern.compile(includeFilter);
    }

    @Override
    public boolean select(Node node) {
        for (Prompt prompt : node.getPrompts()) {
            if (pattern.matcher(prompt.getName()).find()) return true;
        }
        return false;
    }
}
