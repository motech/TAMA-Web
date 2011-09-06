package org.motechproject.tama.web;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.tama.domain.City;
import org.motechproject.tama.repository.AllCities;

import java.util.ArrayList;
import java.util.Collection;

public class ClinicControllerTest {
    @Mock
    private AllCities allCities;

    @Test
    public void shouldSortCitiesInAlphabeticalOrderCaseInsensitive() {
        MockitoAnnotations.initMocks(this);

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
        ClinicController clinicController = new ClinicController(null, allCities);

        Collection<City> sortedCities = clinicController.populateCitys();
        Assert.assertEquals(4, sortedCities.size());
        City[] sortedCityArray = sortedCities.toArray(new City[0]);
        Assert.assertEquals("Chennai", sortedCityArray[0].getName());
        Assert.assertEquals("chirala", sortedCityArray[1].getName());
        Assert.assertEquals("Hyderabad", sortedCityArray[2].getName());
        Assert.assertEquals("Pune", sortedCityArray[3].getName());
    }
}