package org.motechproject.tama.ivr.decisiontree;

import com.ozonetel.kookoo.CollectDtmf;

public class KookooCollectDtmfFactory {
    public static CollectDtmf create() {
        return new CollectDtmf();
    }

}
