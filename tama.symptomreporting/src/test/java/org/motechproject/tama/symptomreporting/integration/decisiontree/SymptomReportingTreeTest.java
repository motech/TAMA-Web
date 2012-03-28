package org.motechproject.tama.symptomreporting.integration.decisiontree;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.decisiontree.model.Tree;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.symptomreporting.decisiontree.SymptomReportingTree;
import org.motechproject.tama.symptomreporting.decisiontree.SymptomReportingTreeInterceptor;
import org.motechproject.tama.symptomsreporting.decisiontree.service.SymptomReportingTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationSymptomReportingContext.xml", inheritLocations = false)
public class SymptomReportingTreeTest {
    @Autowired
    private SymptomReportingTreeInterceptor symptomReportingTreeInterceptor;
    @Autowired
    private SymptomReportingTreeService symptomReportingTreeService;
    @Autowired
    private TAMATreeRegistry tamaTreeRegistry;

    @Test
    public void shouldReturn_TheRightRegimenTree_ForGivenSymptomReportingTreeName() {
        Tree regimen1_1Tree = new SymptomReportingTree(symptomReportingTreeService, symptomReportingTreeInterceptor, tamaTreeRegistry).getTree("Regimen1_1");

        assertEquals("Regimen1_1", regimen1_1Tree.getName());
        assertEquals(TamaIVRMessage.START_SYMPTOM_FLOW, regimen1_1Tree.getRootNode().getPrompts().get(0).getName());
        assertEquals("q_fever", regimen1_1Tree.getRootNode().getPrompts().get(1).getName());
    }
}
