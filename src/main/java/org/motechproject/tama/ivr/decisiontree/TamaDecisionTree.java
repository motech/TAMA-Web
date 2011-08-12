package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Tree;

public abstract class TamaDecisionTree {
    protected Tree tree;

    public Tree getTree() {
        if (tree != null) return tree;
        tree = Tree.newBuilder()
                .setName(this.getClass().getName())
                .setRootNode(createRootNode())
                .build();
        return tree;
    }

    protected abstract Node createRootNode();
}
