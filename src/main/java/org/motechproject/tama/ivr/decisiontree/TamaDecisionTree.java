package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Tree;
import org.motechproject.server.service.ivr.IVRContext;

public abstract class TamaDecisionTree {
    protected Tree tree;

    public Tree getTree(IVRContext ivrContext) {
        if (tree != null) return tree;
        tree = new Tree()
                .setName(this.getClass().getName())
                .setRootNode(createRootNode(ivrContext));
        return tree;
    }

    protected abstract Node createRootNode(IVRContext ivrContext);
}
