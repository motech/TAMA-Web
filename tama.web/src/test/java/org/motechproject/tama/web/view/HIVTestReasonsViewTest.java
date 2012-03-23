package org.motechproject.tama.web.view;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.refdata.domain.HIVTestReason;
import org.motechproject.tama.refdata.objectcache.AllHIVTestReasonsCache;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;


public class HIVTestReasonsViewTest {

    private HIVTestReasonsView HIVTestReasonsView;

    @Mock
    private AllHIVTestReasonsCache HIVTestReasons;

    @Before
    public void setUp() {
        initMocks(this);
        HIVTestReasonsView = new HIVTestReasonsView(HIVTestReasons);
    }

    @Test
    public void shouldSortByNameAscending() {

        when(HIVTestReasons.getAll()).thenReturn(new ArrayList<HIVTestReason>() {
            {
                add(new HIVTestReason() {{
                    setName("reason2");
                }});
                add(new HIVTestReason() {{
                    setName("reason1");
                }});
            }
        });
        assertEquals("reason1", HIVTestReasonsView.getAll().get(0).getName());
        assertEquals("reason2", HIVTestReasonsView.getAll().get(1).getName());
    }

    @Test
    public void shouldIgnoreCaseWhenSorting() {
        when(HIVTestReasons.getAll()).thenReturn(new ArrayList<HIVTestReason>() {
            {
                add(new HIVTestReason() {{
                    setName("Reason2");
                }});
                add(new HIVTestReason() {{
                    setName("reason1");
                }});
            }
        });
        assertEquals("reason1", HIVTestReasonsView.getAll().get(0).getName());
        assertEquals("Reason2", HIVTestReasonsView.getAll().get(1).getName());
    }


}
