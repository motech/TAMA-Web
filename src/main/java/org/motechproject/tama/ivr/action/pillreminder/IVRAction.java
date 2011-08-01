package org.motechproject.tama.ivr.action.pillreminder;

import com.ozonetel.kookoo.Response;
import org.apache.commons.lang.StringUtils;
import org.motechproject.decisiontree.model.NodeInfo;
import org.motechproject.decisiontree.model.Tree;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.action.BaseIncomingAction;
import org.motechproject.tama.ivr.builder.DecisionTreeBasedResponseBuilder;
import org.motechproject.tama.ivr.builder.IVRResponseBuilder;
import org.motechproject.tama.ivr.decisiontree.TAMADecisionTree;

public class IVRAction {
    private TAMADecisionTree tamaDecisionTree;
    private IVRMessage ivrMessage;

    public IVRAction(TAMADecisionTree tamaDecisionTree, IVRMessage ivrMessage) {
        this.tamaDecisionTree = tamaDecisionTree;
        this.ivrMessage = ivrMessage;
    }

    public String handle(IVRRequest ivrRequest, IVRSession ivrSession) {
        Tree tree = tamaDecisionTree.getTree();
        String userInput = StringUtils.remove(ivrRequest.getData(), BaseIncomingAction.POUND_SYMBOL);
        String currentPosition = ivrSession.currentDecisionTreePath();
        DecisionTreeBasedResponseBuilder responseBuilder = new DecisionTreeBasedResponseBuilder();
        IVRContext ivrContext = new IVRContext(ivrRequest, ivrSession);

        NodeInfo nodeInfo = tree.nextNodeInfo(currentPosition, userInput);
        if (nodeInfo.node() == null) {
            nodeInfo = tree.currentNodeInfo(currentPosition);
        } else {
            nodeInfo.node().getTreeCommand().execute(ivrContext);
        }

        ivrSession.currentDecisionTreePath(nodeInfo.path());
        IVRResponseBuilder ivrResponseBuilder = responseBuilder.ivrResponse(ivrRequest.getSid(), nodeInfo.node(), ivrContext);
        Response ivrResponse = ivrResponseBuilder.create(ivrMessage);
        return ivrResponse.getXML();
    }
}
