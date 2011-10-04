package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.web.command.NextCallDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CurrentDosageTakenTree extends TamaDecisionTree {

    @Autowired
    private NextCallDetails nextCallDetails;
    @Autowired
    private SymptomReportingTransitionsUtility symptomReportingTransitionsUtility;
    @Autowired
    private OutboxTransitionsUtility outboxTransitionsUtility;

    @Override
    protected Node createRootNode(IVRContext ivrContext) {
        return new Node()
                .setPrompts(new AudioPrompt().setCommand(nextCallDetails),
                			new AudioPrompt().setName(TamaIVRMessage.MENU_010_05_01_MAINMENU4))
                .setTransitions(new Object[][]{
                		{"2", symptomReportingTransitionsUtility.newInstance() },
                        {"3", outboxTransitionsUtility.newInstance() }
            		}
                );
    }
}
