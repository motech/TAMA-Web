package org.motechproject.tama.ivr.decisiontree;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Tree;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SymptomReportingTreeTest  {

    @Mock
    private SymptomReportingAlertService symptomReportingAlertService;

    private SymptomReportingTree symptomReportingTree;

    @Before
    public void setUp(){
        initMocks(this);
        when(symptomReportingAlertService.addAlerts(any(Node.class))).thenReturn(null);
        symptomReportingTree = new SymptomReportingTree(symptomReportingAlertService);
    }

    @Test
    public void shouldReturn_TheRightRegimenTree_ForGivenSymptomReportingTreeName(){
        Tree regimen1_1Tree = symptomReportingTree.getTree("Regimen1_1");

        assertEquals("Regimen1_1", regimen1_1Tree.getName());
        assertEquals("q_fever", regimen1_1Tree.getRootNode().getPrompts().get(0).getName());
    }

    @Test
    public void shouldReturnNull_WhenCannotFindTheRightRegimenTree(){
        Tree regimen1_2Tree = symptomReportingTree.getTree("Regimen2_1");

        assertNull(regimen1_2Tree);
    }
}
