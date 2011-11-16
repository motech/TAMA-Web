package org.motechproject.tama.ivr.decisiontree.filter;

import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

public abstract class DecisionTreeNodesFilter {

    public abstract boolean select(Node node);

    public List<Node> filter(Node rootNode) {
        return dfs(rootNode);
    }

    private List<Node> dfs(Node node) {
        List<Node> resultNodes = new ArrayList<Node>();
        if (select(node))
            resultNodes.add(node);
        for (Node next : getChildNodes(node))
            resultNodes.addAll(dfs(next));
        return resultNodes;
    }

    private List<Node> getChildNodes(Node node) {
        return extract(node.getTransitions().values(), on(Transition.class).getDestinationNode());
    }
}

