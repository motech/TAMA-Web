package org.motechproject.tama.clinicvisits.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListOfWeeks {

    public static List<Integer> weeks(String weeks) {
        List<String> allWeeks = Arrays.asList(weeks.split(","));
        List<Integer> weekNumbers = new ArrayList<Integer>();

        for (String week : allWeeks) {
            weekNumbers.add(Integer.parseInt(week));
        }
        return weekNumbers;
    }
}
