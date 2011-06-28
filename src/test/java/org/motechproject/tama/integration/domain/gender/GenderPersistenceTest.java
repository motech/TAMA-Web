package org.motechproject.tama.integration.domain.gender;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.Gender;
import org.motechproject.tama.repository.Genders;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Gender.class)
public class GenderPersistenceTest {
    @Test
    public void testFindAllGenders() {
        Genders mockGenders = mock(Genders.class);

        when(mockGenders.getAll()).thenReturn(new ArrayList<Gender>());
        PowerMockito.spy(Gender.class);
        when(Gender.genders()).thenReturn(mockGenders);
        List<Gender> gendersList = Gender.findAllGenders();

        Assert.assertNotNull("Total Gender list should not be null", gendersList);
        Assert.assertEquals(0, gendersList.size());

    }

    @Test
    public void testCountGenders() {
        Genders mockGenders = mock(Genders.class);

        when(mockGenders.getAll()).thenReturn(new ArrayList<Gender>());
        PowerMockito.spy(Gender.class);
        when(Gender.genders()).thenReturn(mockGenders);
        long totalGenders = Gender.countGenders();

        Assert.assertEquals(0, totalGenders);

    }

    @Test
    public void testFindGenderWithValidId() {
        Genders mockgenders = mock(Genders.class);
        String dummyGenderId = "Dummy";
        Gender testGender = new Gender();

        when(mockgenders.get(dummyGenderId)).thenReturn(testGender);
        PowerMockito.spy(Gender.class);
        when(Gender.genders()).thenReturn(mockgenders);
        Gender gender = Gender.findGender(dummyGenderId);
        Assert.assertNotNull("Gender should not be null", gender);

    }

    @Test
    public void testFindGenderWithNullId() {
        Gender gender = Gender.findGender(null);
        Assert.assertNull("Gender" +
                " should be null", gender);
    }

    @Test
    public void testFindGenderEntries() {
        Genders mockGenders = mock(Genders.class);

        when(mockGenders.getAll()).thenReturn(new ArrayList<Gender>());
        PowerMockito.spy(Gender.class);
        when(Gender.genders()).thenReturn(mockGenders);

        List<Gender> genderList = Gender.findGenderEntries(10, 10);
        Assert.assertNotNull("Total Gender list should not be null", genderList);
        Assert.assertEquals(0, genderList.size());
    }

    @Test
    public void testPersistGender() {
        Genders mockGenders = mock(Genders.class);
        Gender gender = new Gender();
        gender.setGenders(mockGenders);

        gender.persist();
        verify(mockGenders).add(gender);
    }

    @Test
    public void testDeleteGender() {
        Genders mockGenders = mock(Genders.class);
        Gender gender = new Gender();
        gender.setGenders(mockGenders);

        gender.persist();
        verify(mockGenders).add(gender);

        gender.remove();
        verify(mockGenders).remove(gender);
    }


    @Test
    public void testUpdateGender() {
        Genders mockGenders = mock(Genders.class);
        String id = "Dummy";
        Gender gender = new Gender();
        gender.setId(id);
        gender.setRevision("rev");
        gender.setGenders(mockGenders);
        when(mockGenders.get(id)).thenReturn(gender);

        gender.persist();
        verify(mockGenders).add(gender);

        gender.merge();
        verify(mockGenders).update(gender);

    }
}

