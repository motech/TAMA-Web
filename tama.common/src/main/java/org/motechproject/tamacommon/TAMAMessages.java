package org.motechproject.tamacommon;

public class TAMAMessages {

    public static final String MOBILE_NUMBER_REGEX_MESSAGE = "Mobile Phone Number should be numeric and 10 digits long.";
    public static final String PASSCODE_REGEX_MESSAGE = "Passcode should be numeric and 4-10 digits long.";
    public static final String DATE_OF_BIRTH_MUST_BE_IN_PAST = "Date Of Birth must be in the past.";
    public static final String OLD_PASSWORD_MISMATCH = "The current password is incorrect";

    public static final String TEST_DATE_MUST_BE_IN_PAST = "Test date must be in the past.";
    public static final String TEST_DATE_NOT_EMPTY = "Test date must not be empty";

    public static final String ADHERENCE_FALLING_FROM_TO = "Adherence fell by " + TAMAConstants.PERCENTAGE_FORMAT + ", from " + TAMAConstants.PERCENTAGE_FORMAT + " to " + TAMAConstants.PERCENTAGE_FORMAT;
    public static final String ADHERENCE_PERCENTAGE_IS = "Adherence percentage is " + TAMAConstants.PERCENTAGE_FORMAT;
}
