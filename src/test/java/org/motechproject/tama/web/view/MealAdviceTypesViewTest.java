package org.motechproject.tama.web.view;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.domain.MealAdviceType;
import org.motechproject.tama.repository.AllMealAdviceTypes;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;


public class MealAdviceTypesViewTest {


    private MealAdviceTypesView mealAdviceTypesView;

    @Mock
    private AllMealAdviceTypes mealAdviceTypes;

    @Before
    public void setUp() {
        initMocks(this);
        mealAdviceTypesView = new MealAdviceTypesView(mealAdviceTypes);
    }

    @Test
    public void shouldSortByTypeAscending() {

        when(mealAdviceTypes.getAll()).thenReturn(new ArrayList<MealAdviceType>() {
            {
                add(new MealAdviceType("type2"));
                add(new MealAdviceType("type1"));
            }
        });
        assertEquals("type1", mealAdviceTypesView.getAll().get(0).getType());
        assertEquals("type2", mealAdviceTypesView.getAll().get(1).getType());
    }

    @Test
    public void shouldIgnoreCaseWhenSorting() {

        when(mealAdviceTypes.getAll()).thenReturn(new ArrayList<MealAdviceType>() {
            {
                add(new MealAdviceType("Type2"));
                add(new MealAdviceType("type1"));
            }
        });
        assertEquals("type1", mealAdviceTypesView.getAll().get(0).getType());
        assertEquals("Type2", mealAdviceTypesView.getAll().get(1).getType());
    }

}
