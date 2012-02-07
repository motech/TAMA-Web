package org.motechproject.tamafunctionalframework.testdata.ivrrequest;

import org.motechproject.deliverytools.kookoo.QueryParams;

public interface CallInfo {
    String asString();

    QueryParams appendDataMapTo(QueryParams url);
}
