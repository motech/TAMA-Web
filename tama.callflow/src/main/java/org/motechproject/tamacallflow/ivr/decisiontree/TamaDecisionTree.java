package org.motechproject.tamacallflow.ivr.decisiontree;

import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Tree;

public abstract class TamaDecisionTree {
    protected Tree tree;

    public Tree getTree() {
        if (tree != null) return tree;
        tree = new Tree()
                .setName(this.getClass().getName())
                .setRootNode(createRootNode());
        return tree;
    }

    protected abstract Node createRootNode();
}
