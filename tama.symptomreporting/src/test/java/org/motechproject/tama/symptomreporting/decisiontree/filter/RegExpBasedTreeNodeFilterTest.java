package org.motechproject.tama.symptomreporting.decisiontree.filter;

import org.junit.Test;
import org.motechproject.decisiontree.model.*;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class RegExpBasedTreeNodeFilterTest {

    @Test
    public void shouldReturnMatchingNodes() {
        final RegExpBasedTreeNodeFilter regExpBasedTreeNodeFilter = new RegExpBasedTreeNodeFilter("^cy_.*");

        final Node node = new Node().setPrompts(
                new AudioPrompt().setName("cn_lowurineorgenweakness")
                , new MenuAudioPrompt().setName("q_swellfacelegs"))
                .setTransitions(new Object[][]{
                        {"1", new Transition().setDestinationNode(
                                new Node().setPrompts(
                                        new AudioPrompt().setName("ppc_fevswellfacelegs")
                                        , new AudioPrompt().setName("adv_continuemedicineseeclinicasap")
                                )
                        )}
                        , {"3", new Transition().setDestinationNode(
                        new Node().setPrompts(
                        new AudioPrompt().setName("cn_swellfacelegs")
                                , new AudioPrompt().setName("cy_fever")
                                , new AudioPrompt().setName("adv_crocin02")
                                , new DialPrompt()
                        )
                )}
                });
        final List<Node> nodes = regExpBasedTreeNodeFilter.filter(node);
        assertEquals(1, nodes.size());
    }

    @Test
    public void shouldReturnNothingIfNoMatchFound() {
        final RegExpBasedTreeNodeFilter regExpBasedTreeNodeFilter = new RegExpBasedTreeNodeFilter("^cy_.*");
        final Node node = new Node().setPrompts(
                new AudioPrompt().setName("fever"),
                new AudioPrompt().setName("prompt3")
        );
        assertEquals(0, regExpBasedTreeNodeFilter.filter(node).size());
    }
}
