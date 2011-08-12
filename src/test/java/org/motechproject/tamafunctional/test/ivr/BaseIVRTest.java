package org.motechproject.tamafunctional.test.ivr;


import org.apache.commons.lang.StringUtils;
import org.motechproject.tamafunctional.framework.BaseTest;

import static org.apache.commons.lang.StringUtils.replace;

public abstract class BaseIVRTest extends BaseTest{
    protected String print(String response) {
       return StringUtils.replace(response, System.getProperty("line.separator"), "");
    }
}
