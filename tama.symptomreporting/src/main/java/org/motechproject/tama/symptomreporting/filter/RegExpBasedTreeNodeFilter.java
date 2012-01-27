package org.motechproject.tama.symptomreporting.filter;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;

import java.util.regex.Pattern;

public class RegExpBasedTreeNodeFilter extends DecisionTreeNodesFilter {

    private Pattern pattern;

    public RegExpBasedTreeNodeFilter(String includeFilter) {
        pattern = Pattern.compile(includeFilter);
    }

    @Override
    public boolean select(Node node) {
        return selectPrompt(node) != null;
    }

    public Prompt selectPrompt(Node node) {
        for (Prompt prompt : node.getPrompts()) {
            if (prompt instanceof AudioPrompt && pattern.matcher(prompt.getName()).find()) return prompt;
        }
        return null;
    }
}
