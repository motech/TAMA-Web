package org.motechproject.tama.web;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.tamadomain.domain.City;
import org.motechproject.tamadomain.domain.Clinic;
import org.motechproject.tamadomain.repository.AllCities;
import org.motechproject.tamadomain.repository.AllClinics;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ClinicControllerTest {

    @Mock
    private AllCities allCities;

    @Mock
    private AllClinics allClinics;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Model uiModel;

    private ClinicController clinicController;

    @Before
    public void setUp() {
        initMocks(this);
        clinicController = new ClinicController(allClinics, allCities);
    }

    @Test
    public void shouldSortCitiesInAlphabeticalOrderCaseInsensitive() {
        List<City> cityList = Arrays.asList(City.newCity("Pune"), City.newCity("Chennai"), City.newCity("Hyderabad"), City.newCity("chirala"));

        Mockito.when(allCities.getAllCities()).thenReturn(cityList);

        Collection<City> sortedCities = clinicController.populateCitys();
        Assert.assertEquals(4, sortedCities.size());
        City[] sortedCityArray = sortedCities.toArray(new City[0]);

        Assert.assertEquals("Chennai", sortedCityArray[0].getName());
        Assert.assertEquals("chirala", sortedCityArray[1].getName());
        Assert.assertEquals("Hyderabad", sortedCityArray[2].getName());
        Assert.assertEquals("Pune", sortedCityArray[3].getName());
    }

    @Test
    public void updateShouldPassUpdateModeToView() {
        Model uiModel = mock(Model.class);
        String clinicId = "tempId";

        when(allClinics.get(clinicId)).thenReturn(null);
        clinicController.updateForm(clinicId, uiModel);

        verify(uiModel).addAttribute("mode", "update");
    }

    @Test
    public void shouldReturnAllCities() {
        final ArrayList<Clinic> clinics = new ArrayList<Clinic>() {{
            add(new Clinic("testId"));
        }};
        when(allClinics.getAll()).thenReturn(clinics);

        assertEquals(clinics, clinicController.populateClinics());
    }

    @Test
    public void shouldReturnToUpdateFormIfUpdateHasErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        Clinic clinic = new Clinic("testClinicId");

        final String controllerReturnValue = clinicController.update(clinic, bindingResult, uiModel, null);
        verify(uiModel).addAttribute("clinic", clinic);
        assertEquals("clinics/clinicForm", controllerReturnValue);

    }

    @Test
    public void shouldReturnToUpdateFormIfCreateHasErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        Clinic clinic = new Clinic("testClinicId");

        final String controllerReturnValue = clinicController.create(clinic, bindingResult, uiModel, null);
        verify(uiModel).addAttribute("clinic", clinic);
        assertEquals("clinics/clinicForm", controllerReturnValue);

    }

    @Test
    public void shouldShowDetailsOfAClinic() {
        final String testId = "testId";
        final Clinic clinic = new Clinic(testId);
        when(allClinics.get(testId)).thenReturn(clinic);

        final String returnPath = clinicController.show(testId, uiModel);

        verify(uiModel).addAttribute("clinic", clinic);
        verify(uiModel).addAttribute("itemId", testId);
        assertEquals("clinics/show", returnPath);
    }
}