package org.motechproject.tamafunctional.testdata.ivrrequest;

import org.motechproject.deliverytools.kookoo.QueryParams;

public class NoCallInfo implements CallInfo {
    @Override
    public String asString() {
        return "";
    }

    @Override
    public QueryParams appendDataMapTo(QueryParams queryParams) {
        return queryParams;
    }
}
