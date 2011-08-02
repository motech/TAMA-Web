package org.motechproject.tama.ivr.builder;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.tama.ivr.IVRContext;

import java.util.List;

public class DecisionTreeBasedResponseBuilder {
    public IVRResponseBuilder ivrResponse(String sid, Node node, IVRContext ivrContext) {
        IVRResponseBuilder ivrResponseBuilder = new IVRResponseBuilder(sid);
        List<Prompt> prompts = node.getPrompts();
        boolean hasTransitions = node.hasTransitions();
        boolean playPromptTotheUser = !prompts.isEmpty();
        for (Prompt prompt : prompts) {
            String promptName = prompt.getName();
            ITreeCommand command = prompt.getCommand();
            boolean isAudioPrompt = prompt instanceof AudioPrompt;
            if (command == null) {
                buildPrompts(ivrResponseBuilder, promptName, isAudioPrompt);
            }
            else {
                String[] promptsFromCommand = command.execute(ivrContext);
                for (String promptFromCommand : promptsFromCommand) {
                    buildPrompts(ivrResponseBuilder, promptFromCommand, isAudioPrompt);
                }
            }
        }
        if (playPromptTotheUser && hasTransitions) {
            ivrResponseBuilder.collectDtmf();
        }
        else {
            ivrResponseBuilder.withHangUp();
        }
        return ivrResponseBuilder;
    }

    private void buildPrompts(IVRResponseBuilder ivrResponseBuilder, String promptName, boolean isAudioPrompt) {
        if (isAudioPrompt) ivrResponseBuilder.withPlayAudios(promptName);
        else ivrResponseBuilder.withPlayTexts(promptName);
    }
}
