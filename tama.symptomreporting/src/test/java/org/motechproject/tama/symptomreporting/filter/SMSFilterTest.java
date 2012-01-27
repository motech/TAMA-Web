package org.motechproject.tama.symptomreporting.filter;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.Node;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class SMSFilterTest {

    private FirstPriorityFilter firstPriorityFilter;
    private FifthPriorityFilter fifthPriorityFilter;
    private SMSFilter smsFilter;

    @Before
    public void setUp() {
        firstPriorityFilter = new FirstPriorityFilter();
        firstPriorityFilter.addCriteria("adv_crocin01");

        fifthPriorityFilter = new FifthPriorityFilter();
        fifthPriorityFilter.addCriteria("adv_crocin02");

        smsFilter = new SMSFilter(firstPriorityFilter, fifthPriorityFilter);
    }

    @Test
    public void shouldNotFilterNodesThatMatchFirstPriorityCriteria() {
        Node node = new Node().setPrompts(new AudioPrompt().setName(firstPriorityFilter.getCriteria()[0]));
        assertFalse(smsFilter.filter(node).isEmpty());
    }

    @Test
    public void shouldNotFilterNodesThatMatchFifthPriorityCriteria() {
        Node node = new Node().setPrompts(new AudioPrompt().setName(fifthPriorityFilter.getCriteria()[0]));
        assertFalse(smsFilter.filter(node).isEmpty());
    }

    @Test
    public void shouldFilterNodeThatDoesNotMatchAnyCriteria(){
        Node node = new Node().setPrompts(new AudioPrompt().setName("adv_continuemedicineseeclinicasap"));
        assertTrue(smsFilter.filter(node).isEmpty());
    }
}
