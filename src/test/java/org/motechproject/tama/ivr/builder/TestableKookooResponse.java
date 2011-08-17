package org.motechproject.tama.ivr.builder;

import com.ozonetel.kookoo.CollectDtmf;
import com.ozonetel.kookoo.Dial;
import com.ozonetel.kookoo.Record;
import com.ozonetel.kookoo.Response;
import org.w3c.dom.Element;

public class TestableKookooResponse extends Response {
    private String sid;
    private CollectDtmf cd;

    @Override
    public void setSid(String sid) {
        this.sid = sid;
        super.setSid(sid);
    }

    @Override
    public void addCollectDtmf(CollectDtmf cd) {
        this.cd = cd;
        super.addCollectDtmf(cd);
    }

    public TestableCollectDtmf getDtmf() {
        return (TestableCollectDtmf)cd;
    }

}
