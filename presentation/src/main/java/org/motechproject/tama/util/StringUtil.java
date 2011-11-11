package org.motechproject.tama.util;


import org.apache.commons.lang.StringUtils;

public class StringUtil {
    public static String lastMatch(String actualString, String splitOn) {
        if (StringUtils.isEmpty(actualString)) return "";
        return actualString.split(splitOn)[actualString.split(splitOn).length - 1];
    }

    public static String ivrMobilePhoneNumber(String mobilePhoneNumber) {
        return StringUtils.isEmpty(mobilePhoneNumber) ? mobilePhoneNumber : String.format("0%s", mobilePhoneNumber);
    }
}