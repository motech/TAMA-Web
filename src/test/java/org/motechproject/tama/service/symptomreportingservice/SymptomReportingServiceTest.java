package org.motechproject.tama.service.symptomreportingservice;

import org.drools.runtime.StatelessKnowledgeSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.TamaException;
import org.motechproject.tama.domain.MedicalCondition;
import org.motechproject.tama.service.SymptomReportingService;

import java.util.ArrayList;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.MockitoAnnotations.initMocks;

public class SymptomReportingServiceTest {
    @Mock
    private StatelessKnowledgeSession ksession;

    private SymptomReportingService symptomReportingService;
    private MedicalCondition medicalCondition;

    @Before
    public void setUp() {
        initMocks(this);
        medicalCondition = new MedicalCondition();
    }

    @Test
    public void shouldNotReturnMoreThanOneMatchingCondition() {
        try {
            ArrayList<String> treeWithTwoMatches = new ArrayList<String>();
            treeWithTwoMatches.add("match1");
            treeWithTwoMatches.add("match2");

            symptomReportingService = new SymptomReportingService(ksession, treeWithTwoMatches);
            symptomReportingService.getSymptomReportingTree(medicalCondition);
            fail("test failed ...");
        }
        catch (TamaException e){
            assertEquals("Should not match more than one tree condition.", e.getMessage());
        }
    }

    @Test
    public void shouldReturnOneMatchingCondition() {
        ArrayList<String> treeWithTwoMatches = new ArrayList<String>();
        treeWithTwoMatches.add("match1");

        symptomReportingService = new SymptomReportingService(ksession, treeWithTwoMatches);
        String symptomReportingTree = symptomReportingService.getSymptomReportingTree(medicalCondition);
        assertEquals("match1", symptomReportingTree);
    }

    @Test
    public void shouldReturnNoMatchingCondition() {
        symptomReportingService = new SymptomReportingService(ksession, new ArrayList<String>());
        String symptomReportingTree = symptomReportingService.getSymptomReportingTree(medicalCondition);
        assertNull(symptomReportingTree);
    }
}
