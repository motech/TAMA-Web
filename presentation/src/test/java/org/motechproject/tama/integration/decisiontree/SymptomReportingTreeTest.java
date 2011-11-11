package org.motechproject.tama.integration.decisiontree;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.decisiontree.model.Tree;
import org.motechproject.tama.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.ivr.decisiontree.SymptomReportingTreeInterceptor;
import org.motechproject.tama.ivr.decisiontree.SymptomReportingTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:**/applicationContext.xml")
public class SymptomReportingTreeTest extends SpringIntegrationTest {

    @Autowired
    private SymptomReportingTreeInterceptor symptomReportingService;

    @Test
    public void shouldReturn_TheRightRegimenTree_ForGivenSymptomReportingTreeName(){
        Tree regimen1_1Tree = new SymptomReportingTree(symptomReportingService).getTree("Regimen1_1");

        assertEquals("Regimen1_1", regimen1_1Tree.getName());
        assertEquals("q_fever", regimen1_1Tree.getRootNode().getPrompts().get(0).getName());
    }
}
