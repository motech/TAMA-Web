package org.motechproject.tama.web;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.tama.patient.builder.LabResultBuilder;
import org.motechproject.tama.patient.domain.LabResult;
import org.motechproject.tama.patient.service.LabResultsService;
import org.motechproject.tama.web.model.CD4Json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
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
        List<LabResult> defaultCD4Results = defaultCD4LabResults();

        when(labResultsService.listCD4Counts(PATIENT_ID, 3)).thenReturn(defaultCD4Results);

        String cd4list = controller.listCD4Count(PATIENT_ID, 3);

        assertThat(cd4list, is(ExpectedCD4Json.For(defaultCD4Results)));
    }

    private List<LabResult> defaultCD4LabResults() {
        LabResult labResult = LabResultBuilder.defaultCD4Result().build();
        return Arrays.asList(labResult, labResult, labResult);
    }
}

class ExpectedCD4Json extends ArgumentMatcher<String> {

    private List<LabResult> defaultCD4Results;

    private ExpectedCD4Json() {
        defaultCD4Results = new ArrayList<LabResult>();
    }

    public static ExpectedCD4Json For(List<LabResult> defaultCD4Results) {
        ExpectedCD4Json ExpectedCD4Json = new ExpectedCD4Json();
        ExpectedCD4Json.defaultCD4Results = defaultCD4Results;
        return ExpectedCD4Json;
    }

    @Override
    public boolean matches(Object argument) {
        CD4Json cd4Json = null;
        try {
            cd4Json = new CD4Json(defaultCD4Results);
        } catch (JSONException e) {
            return false;
        }
        return cd4Json.toString().equals((String) argument);
    }
}
