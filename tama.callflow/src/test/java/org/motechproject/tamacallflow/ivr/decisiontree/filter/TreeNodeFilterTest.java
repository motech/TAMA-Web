package org.motechproject.tamacallflow.ivr.decisiontree.filter;

import org.junit.Test;
import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.tamacallflow.ivr.decisiontree.filter.TreeNodeFilter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TreeNodeFilterTest {
    @Test
    public void shouldFilterIfPromptWithSameNameIsPresent() {
        final TreeNodeFilter treeNodeFilter = new TreeNodeFilter("prompt1", "prompt2");
        Node node = new Node().setPrompts(
                new AudioPrompt().setName("prompt1"),
                new AudioPrompt().setName("prompt3")
        );
        final boolean isFiltered = treeNodeFilter.select(node);
        assertTrue(isFiltered);
    }

    @Test
    public void shouldNotFilterIfPromptWithSameNameIsPresent() {
        final TreeNodeFilter treeNodeFilter = new TreeNodeFilter("prompt1", "prompt2");
        Node node = new Node().setPrompts(
                new AudioPrompt().setName("prompt3"),
                new AudioPrompt().setName("prompt4")
        );
        final boolean isFiltered = treeNodeFilter.select(node);
        assertFalse(isFiltered);
    }

}
