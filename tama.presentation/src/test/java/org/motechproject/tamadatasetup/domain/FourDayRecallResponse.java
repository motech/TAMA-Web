package org.motechproject.tamadatasetup.domain;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.util.DateUtil;

public class FourDayRecallResponse {
    private FourDayRecallPatientEvents[] fourDayRecallPatientEventses;



    public FourDayRecallPatientEvents[] calls() {
        return fourDayRecallPatientEventses;
    }
}
