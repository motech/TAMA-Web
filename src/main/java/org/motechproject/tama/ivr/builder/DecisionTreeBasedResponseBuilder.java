package org.motechproject.tama.ivr.builder;

import org.motechproject.decisiontree.model.*;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;

import java.util.List;

public class DecisionTreeBasedResponseBuilder {
    public IVRResponseBuilder ivrResponse(String sid, Node node, IVRContext ivrContext, boolean retryOnIncorrectUserAction) {
        IVRResponseBuilder ivrResponseBuilder = new IVRResponseBuilder(sid);
        List<Prompt> prompts = node.getPrompts();
        boolean hasTransitions = node.hasTransitions();
        for (Prompt prompt : prompts) {
            if (retryOnIncorrectUserAction && !(prompt instanceof MenuAudioPrompt)) continue;
            ITreeCommand command = prompt.getCommand();
            boolean isAudioPrompt = prompt instanceof AudioPrompt;
            if (command == null) {
                buildPrompts(ivrResponseBuilder, prompt.getName(), isAudioPrompt);
            } else {
                String[] promptsFromCommand = command.execute(ivrContext);
                for (String promptFromCommand : promptsFromCommand) {
                    buildPrompts(ivrResponseBuilder, promptFromCommand, isAudioPrompt);
                }
            }
        }
        if (hasTransitions) {
            ivrResponseBuilder.collectDtmf();
        } else {
            ivrResponseBuilder.withPlayAudios(IVRMessage.SIGNATURE_MUSIC_URL).withHangUp();
        }
        return ivrResponseBuilder;
    }

    private void buildPrompts(IVRResponseBuilder ivrResponseBuilder, String promptName, boolean isAudioPrompt) {
        if (isAudioPrompt) ivrResponseBuilder.withPlayAudios(promptName);
        else ivrResponseBuilder.withPlayTexts(promptName);
    }
}
