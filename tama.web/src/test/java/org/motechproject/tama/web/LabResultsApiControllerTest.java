package org.motechproject.tama.web;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.tama.patient.builder.LabResultBuilder;
import org.motechproject.tama.patient.domain.LabResult;
import org.motechproject.tama.patient.service.LabResultsService;
import org.motechproject.tama.web.model.LabResultsJson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LabResultsApiControllerTest {

    public static final String PATIENT_ID = "patientId";

    @Mock
    LabResultsService labResultsService;

    private LabResultsApiController controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        controller = new LabResultsApiController(labResultsService);
    }

    @Test
    public void shouldReturnCD4CountListAsJson() throws Exception {
        LabResult labResult = LabResultBuilder.defaultCD4Result().build();
        List<LabResult> CD4Results = Arrays.asList(labResult, labResult, labResult);
        when(labResultsService.listCD4Counts(PATIENT_ID, 3)).thenReturn(CD4Results);

        String cd4list = controller.listCD4Count(PATIENT_ID, 3);

        assertThat(cd4list, is(ExpectedLabResultsJson.For(CD4Results)));
    }

    @Test
    public void shouldReturnPVLabResultListAsJson() throws Exception {
        LabResult pvlLabResult = LabResultBuilder.defaultPVLResult().build();
        List<LabResult> pvlLabResults = Arrays.asList(pvlLabResult, pvlLabResult);
        when(labResultsService.listPVLLabResults(PATIENT_ID, 3)).thenReturn(pvlLabResults);

        String pvlList = controller.listPVLLabResults(PATIENT_ID, 3);

        assertThat(pvlList, is(ExpectedLabResultsJson.For(pvlLabResults)));
    }
}

class ExpectedLabResultsJson extends ArgumentMatcher<String> {

    private List<LabResult> results;

    private ExpectedLabResultsJson() {
        results = new ArrayList<LabResult>();
    }

    public static ExpectedLabResultsJson For(List<LabResult> results) {
        ExpectedLabResultsJson expectedLabResultsJson = new ExpectedLabResultsJson();
        expectedLabResultsJson.results = results;
        return expectedLabResultsJson;
    }

    @Override
    public boolean matches(Object argument) {
        LabResultsJson labResultsJson = null;
        try {
            labResultsJson = new LabResultsJson(results);
        } catch (JSONException e) {
            return false;
        }
        return labResultsJson.toString().equals((String) argument);
    }
}
