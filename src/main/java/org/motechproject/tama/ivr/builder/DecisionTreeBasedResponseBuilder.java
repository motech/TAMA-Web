package org.motechproject.tama.ivr.builder;

import com.ozonetel.kookoo.Response;
import org.motechproject.decisiontree.model.*;
import org.motechproject.tama.ivr.IVRMessage;

import java.util.List;

public class DecisionTreeBasedResponseBuilder {
    private IVRResponseBuilder ivrResponseBuilder;
    private IVRMessage ivrMessage;

    public DecisionTreeBasedResponseBuilder(IVRResponseBuilder ivrResponseBuilder, IVRMessage ivrMessage) {
        this.ivrResponseBuilder = ivrResponseBuilder;
        this.ivrMessage = ivrMessage;
    }

    public String nextResponse(Node node) {
        List<Prompt> prompts = node.getPrompts();
        boolean hasTransitions = node.hasTransitions();
        for (Prompt prompt : prompts) {
            String promptName = prompt.getName();
            ITreeCommand command = prompt.getCommand();
            if (command != null) promptName = command.execute(null);

            if (prompt instanceof AudioPrompt) ivrResponseBuilder.withPlayAudios(promptName);
            else if (prompt instanceof TextToSpeechPrompt) ivrResponseBuilder.withPlayTexts(promptName);
        }
        if (hasTransitions) {
            ivrResponseBuilder.collectDtmf();
        } else {
            ivrResponseBuilder.withHangUp();
        }
        Response response = ivrResponseBuilder.create(ivrMessage);
        return response.getXML();
    }
}
