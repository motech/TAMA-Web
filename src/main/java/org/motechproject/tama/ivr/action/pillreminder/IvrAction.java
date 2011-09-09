package org.motechproject.tama.ivr.action.pillreminder;

import com.ozonetel.kookoo.Response;
import org.apache.commons.lang.StringUtils;
import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.decisiontree.model.NodeInfo;
import org.motechproject.decisiontree.model.Tree;
import org.motechproject.tama.ivr.*;
import org.motechproject.tama.ivr.action.BaseAction;
import org.motechproject.tama.ivr.builder.DecisionTreeBasedResponseBuilder;
import org.motechproject.tama.ivr.builder.IVRResponseBuilder;
import org.motechproject.tama.ivr.decisiontree.TreeChooser;
import org.springframework.aop.target.ThreadLocalTargetSource;

import java.util.List;

public class IvrAction {
    private TreeChooser treeChooser;
    private IVRMessage ivrMessage;
    private ThreadLocalTargetSource threadLocalTargetSource;

    public IvrAction(TreeChooser treeChooser, IVRMessage ivrMessage, ThreadLocalTargetSource threadLocalTargetSource) {
        this.treeChooser = treeChooser;
        this.ivrMessage = ivrMessage;
        this.threadLocalTargetSource = threadLocalTargetSource;
    }

    public String handle(IVRRequest ivrRequest, IVRSession ivrSession) {
        IVRContext ivrContext = new IVRContext(ivrRequest, ivrSession);
        ThreadLocalContext threadLocalContext = (ThreadLocalContext) threadLocalTargetSource.getTarget();
        threadLocalContext.setIvrContext(ivrContext);

        Tree tree = treeChooser.getTree(ivrContext);
        String userInput = StringUtils.remove(ivrRequest.getData(), BaseAction.POUND_SYMBOL);
        String currentPosition = ivrSession.currentDecisionTreePath();
        DecisionTreeBasedResponseBuilder responseBuilder = new DecisionTreeBasedResponseBuilder();

        NodeInfo nodeInfo = tree.nextNodeInfo(currentPosition, userInput);
        boolean retryOnIncorrectUserAction = StringUtils.isNotEmpty(currentPosition) && StringUtils.isEmpty(userInput);
        if (nodeInfo.node() == null) {
            nodeInfo = tree.currentNodeInfo(currentPosition);
            retryOnIncorrectUserAction = true;


        } else if (!retryOnIncorrectUserAction){
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
