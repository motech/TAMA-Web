package org.motechproject.tamacallflow.ivr.decisiontree.filter;

import ch.lambdaj.Lambda;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.tamacallflow.ivr.decisiontree.filter.DecisionTreeNodesFilter;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.equalTo;

public class DecisionTreeNodesFilterTest {

    private Node rootNode;

    @Before
    public void setUp() {
        Node node1 = new Node();
        Node node2 = new Node().setPrompts(new AudioPrompt().setName("test"));
        rootNode = new Node()
                .setTransitions(new Object[][]{
                        {"1", new Transition().setDestinationNode(node1)},
                        {"2", new Transition().setDestinationNode(node2)}
                });
    }

    @Test
    public void shouldGetAllNodesWhenAllNodesAreAppropriate() {
        DecisionTreeNodesFilter filter = new DecisionTreeNodesFilter() {
            @Override
            public boolean select(Node node) {
                return true;
            }
        };
        final List<Node> nodes = filter.filter(rootNode);
        assertEquals(3, nodes.size());
    }

    @Test
    public void shouldNotGetAnyNodeWhenNoNodeIsAppropriate() {
        DecisionTreeNodesFilter filter = new DecisionTreeNodesFilter() {
            @Override
            public boolean select(Node node) {
                return false;
            }
        };
        final List<Node> nodes = filter.filter(rootNode);
        assertEquals(0, nodes.size());
    }

    @Test
    public void shouldGetNodesAccordingToCustomCriteria() {
        DecisionTreeNodesFilter filter = new DecisionTreeNodesFilter() {
            @Override
            public boolean select(Node node) {
                return Lambda.select(node.getPrompts(), having(on(Prompt.class).getName(), equalTo("test"))).size() > 0;
            }
        };
        final List<Node> nodes = filter.filter(rootNode);
        assertEquals(1, nodes.size());

    }
}
