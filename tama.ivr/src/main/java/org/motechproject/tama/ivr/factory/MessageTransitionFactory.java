package org.motechproject.tama.ivr.factory;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.command.CallStateCommand;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.domain.CallState;

public class MessageTransitionFactory {

    public static class SetCategoryCommand implements ITreeCommand {

        private TAMAIVRContextFactory contextFactory;
        private String categoryName;

        public SetCategoryCommand(String categoryName) {
            this.categoryName = categoryName;
            contextFactory = new TAMAIVRContextFactory();
        }

        public SetCategoryCommand(TAMAIVRContextFactory contextFactory, String categoryName) {
            this.contextFactory = contextFactory;
            this.categoryName = categoryName;
        }

        @Override
        public String[] execute(Object o) {
            TAMAIVRContext tamaivrContext = contextFactory.create((KooKooIVRContext) o);
            tamaivrContext.setMessagesCategory(categoryName);
            return new String[0];
        }
    }

    public static Transition createTransition(CallState callState, String messageCategory) {
        TAMAIVRContextFactory contextFactory = new TAMAIVRContextFactory();
        return new Transition().setDestinationNode(
                new Node()
                        .setTreeCommands(
                                new CallStateCommand(callState, contextFactory),
                                new SetCategoryCommand(messageCategory)
                        )
        );
    }
}
