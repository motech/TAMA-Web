package org.motechproject.tama.service.symptomreportingservice;

import org.drools.runtime.StatelessKnowledgeSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.TamaException;
import org.motechproject.tama.domain.MedicalCondition;
import org.motechproject.tama.service.SymptomReportingTreeService;

import java.util.ArrayList;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.MockitoAnnotations.initMocks;

public class SymptomReportingTreeServiceTest {
    @Mock
    private StatelessKnowledgeSession ksession;

    private SymptomReportingTreeService symptomReportingTreeService;
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

            symptomReportingTreeService = new SymptomReportingServiceStub(ksession, treeWithTwoMatches);
            symptomReportingTreeService.getSymptomReportingTree(medicalCondition);
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

        symptomReportingTreeService = new SymptomReportingServiceStub(ksession, treeWithTwoMatches);
        String symptomReportingTree = symptomReportingTreeService.getSymptomReportingTree(medicalCondition);
        assertEquals("match1", symptomReportingTree);
    }

    @Test
    public void shouldReturnNoMatchingCondition() {
        symptomReportingTreeService = new SymptomReportingServiceStub(ksession, new ArrayList<String>());
        String symptomReportingTree = symptomReportingTreeService.getSymptomReportingTree(medicalCondition);
        assertNull(symptomReportingTree);
    }

    private class SymptomReportingServiceStub extends SymptomReportingTreeService {
        private ArrayList<String> tree;

        private SymptomReportingServiceStub(StatelessKnowledgeSession ksession, ArrayList<String> tree) {
            super(ksession);
            this.tree = tree;
        }

        @Override
        protected ArrayList<String> getTree() {
            return tree;
        }
    }
}