package org.motechproject.tama.ivr.action.event;

import org.motechproject.decisiontree.model.NodeInfo;
import org.motechproject.decisiontree.model.Tree;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.action.AuthenticateAction;
import org.motechproject.tama.ivr.action.BaseIncomingAction;
import org.motechproject.tama.ivr.builder.DecisionTreeBasedResponseBuilder;
import org.motechproject.tama.ivr.builder.IVRResponseBuilder;
import org.motechproject.tama.ivr.decisiontree.PillReminderDecisionTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class DtmfEventAction extends BaseIncomingAction {
    private AuthenticateAction authenticateAction;
    private PillReminderDecisionTree pillReminderDecisionTree;

    @Autowired
    public DtmfEventAction(AuthenticateAction authenticateAction,
                           PillReminderDecisionTree pillReminderDecisionTree) {
        this.authenticateAction = authenticateAction;
        this.pillReminderDecisionTree = pillReminderDecisionTree;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRSession ivrSession = getIVRSession(request);
        if (ivrSession.isAuthentication()) {
            return authenticateAction.handle(ivrRequest, request, response);
        } else {
            Tree tree = pillReminderDecisionTree.getTree();
            NodeInfo nodeInfo = tree.nextNodeInfo(ivrSession.currentDecisionTreePath(), getInput(ivrRequest));
            nodeInfo.node().getTreeCommand().execute(ivrRequest);
            DecisionTreeBasedResponseBuilder responseBuilder = new DecisionTreeBasedResponseBuilder(new IVRResponseBuilder(ivrRequest.getSid()), messages);
            ivrSession.currentDecisionTreePath(nodeInfo.path());
            return responseBuilder.nextResponse(nodeInfo.node());
        }
    }
}
