package org.motechproject.tamacommon.util;

import junit.framework.Assert;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class ValidationUtil {

    public static <T> void assertConstraintViolation(Set<ConstraintViolation<T>> constraintViolations, String property, String message) {

        for (ConstraintViolation violation : constraintViolations) {
            if (violation.getPropertyPath().toString().equals(property) && violation.getMessage().equals(message)) {
                return;
            }
        }
        Assert.fail("could not find expected violation for property " + property + " and message " + message);
    }
}
