package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.refdata.domain.Analyst;
import org.motechproject.tama.refdata.repository.AllAnalysts;
import org.springframework.ui.Model;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AnalystControllerTest {

    @Mock
    private Model uiModel;
    @Mock
    private AllAnalysts allAnalysts;

    private AnalystController analystController;

    @Before
    public void setup() {
        initMocks(this);
        analystController = new AnalystController(allAnalysts);
    }

    @Test
    public void shouldListAllAnalysts() {
        List<Analyst> analysts = asList(new Analyst());

        when(allAnalysts.getAll()).thenReturn(analysts);
        String view = analystController.index(uiModel);

        assertEquals("analysts/list", view);
        verify(uiModel).addAttribute("analysts", analysts);
    }
}
