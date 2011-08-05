package org.motechproject.tama.ivr.action.pillreminder;

import com.ozonetel.kookoo.Response;
import org.apache.commons.lang.StringUtils;
import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.decisiontree.model.NodeInfo;
import org.motechproject.decisiontree.model.Tree;
import org.motechproject.tama.ivr.*;
import org.motechproject.tama.ivr.action.BaseIncomingAction;
import org.motechproject.tama.ivr.builder.DecisionTreeBasedResponseBuilder;
import org.motechproject.tama.ivr.builder.IVRResponseBuilder;
import org.motechproject.tama.ivr.decisiontree.TAMADecisionTree;
import org.springframework.aop.target.ThreadLocalTargetSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class IVRAction {

    @Autowired
    ThreadLocalTargetSource threadLocalTargetSource;

    private TAMADecisionTree tamaDecisionTree;
    private IVRMessage ivrMessage;

    public IVRAction(TAMADecisionTree tamaDecisionTree, IVRMessage ivrMessage) {
        this.tamaDecisionTree = tamaDecisionTree;
        this.ivrMessage = ivrMessage;
    }

    public String handle(IVRRequest ivrRequest, IVRSession ivrSession) {
        IVRContext ivrContext = new IVRContext(ivrRequest, ivrSession);
        MyContext myContext = (MyContext) threadLocalTargetSource.getTarget();
        myContext.setIvrContext(ivrContext);

        Tree tree = tamaDecisionTree.getTree();
        String userInput = StringUtils.remove(ivrRequest.getData(), BaseIncomingAction.POUND_SYMBOL);
        String currentPosition = ivrSession.currentDecisionTreePath();
        DecisionTreeBasedResponseBuilder responseBuilder = new DecisionTreeBasedResponseBuilder();

        NodeInfo nodeInfo = tree.nextNodeInfo(currentPosition, userInput);
        boolean retryOnIncorrectUserAction = StringUtils.isNotEmpty(currentPosition) && StringUtils.isEmpty(userInput);
        if (nodeInfo.node() == null) {
            nodeInfo = tree.currentNodeInfo(currentPosition);
            retryOnIncorrectUserAction = true;
        } else {
            List<ITreeCommand> treeCommands = nodeInfo.node().getTreeCommands();
            for(ITreeCommand command : treeCommands) {
                command.execute(ivrContext);
            }
        }

        ivrSession.currentDecisionTreePath(nodeInfo.path());
        IVRResponseBuilder ivrResponseBuilder = responseBuilder.ivrResponse(ivrRequest.getSid(), nodeInfo.node(), ivrContext, retryOnIncorrectUserAction);
        Response ivrResponse = ivrResponseBuilder.create(ivrMessage);
        return ivrResponse.getXML();
    }
}
