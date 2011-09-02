package org.motechproject.tama.ivr.builder;

import org.motechproject.decisiontree.model.*;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;

import java.util.List;

public class DecisionTreeBasedResponseBuilder {
    public IVRResponseBuilder ivrResponse(String sid, Node node, IVRContext ivrContext, boolean retryOnIncorrectUserAction) {
        IVRResponseBuilder ivrResponseBuilder = new IVRResponseBuilder(sid, ivrContext.ivrSession().getPrefferedLanguageCode());
        List<Prompt> prompts = node.getPrompts();
        boolean hasTransitions = node.hasTransitions();
        for (Prompt prompt : prompts) {
            if (retryOnIncorrectUserAction && !(prompt instanceof MenuAudioPrompt) && prompt instanceof AudioPrompt)
                continue;
            ITreeCommand command = prompt.getCommand();
            boolean isAudioPrompt = prompt instanceof AudioPrompt;
            boolean isDialPrompt = prompt instanceof DialPrompt;
            if (command == null) {
                if (isDialPrompt) {
                    buildPrompts(ivrResponseBuilder, null, nextDialPhoneNumber((DialPrompt) prompt, ivrContext), isAudioPrompt, isDialPrompt);
                } else {
                    buildPrompts(ivrResponseBuilder, prompt.getName(), null, isAudioPrompt, isDialPrompt);
                }
            } else if (!retryOnIncorrectUserAction) {
                String[] promptsFromCommand = command.execute(ivrContext);
                for (String promptFromCommand : promptsFromCommand) {
                    buildPrompts(ivrResponseBuilder, promptFromCommand, null, isAudioPrompt, isDialPrompt);
                }
            }
        }
        if (hasTransitions) {
            if (node.getTransitions().get("answered") == null) {
                ivrResponseBuilder.collectDtmf(maxLenOfTransitionOptions(node));
            }
        } else {
            ivrResponseBuilder.withPlayAudios(IVRMessage.SIGNATURE_MUSIC_URL).withHangUp();
        }
        return ivrResponseBuilder;
    }

    private String[] nextDialPhoneNumber(DialPrompt prompt, IVRContext ivrContext) {
        Object dialCount = ivrContext.ivrSession().get("dialCount");
        Integer.parseInt((String) dialCount);

        return ((DialPrompt) prompt).getPhoneNumbers();
    }

    private int maxLenOfTransitionOptions(Node node) {
        int maxLen = 0;
        for (String key : node.getTransitions().keySet()) {
            if (maxLen < key.length()) maxLen = key.length();
        }
        return maxLen;
    }

    private void buildPrompts(IVRResponseBuilder ivrResponseBuilder, String promptName, String phoneNumber, boolean isAudioPrompt, boolean isDialPrompt) {
        if (isAudioPrompt) ivrResponseBuilder.withPlayAudios(promptName);
        else if (isDialPrompt) {
            ivrResponseBuilder.withDialNumber(phoneNumber);
        }
        else ivrResponseBuilder.withPlayTexts(promptName);
    }
}
