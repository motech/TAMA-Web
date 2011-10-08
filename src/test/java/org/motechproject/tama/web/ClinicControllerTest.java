package org.motechproject.tama.web;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.tama.domain.City;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.repository.AllCities;
import org.motechproject.tama.repository.AllClinics;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Collection;

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
        City city1 = new City("1");
        city1.setName("Pune");
        City city2 = new City("2");
        city2.setName("Chennai");
        City city3 = new City("3");
        city3.setName("Hyderabad");
        City city4 = new City("4");
        city4.setName("chirala");
        ArrayList<City> cityList = new ArrayList<City>();
        cityList.add(city1);
        cityList.add(city2);
        cityList.add(city3);
        cityList.add(city4);

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
}