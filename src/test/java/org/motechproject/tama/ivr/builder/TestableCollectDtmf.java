package org.motechproject.tama.ivr.builder;

import com.ozonetel.kookoo.CollectDtmf;

public class TestableCollectDtmf extends CollectDtmf{
    private int maxDigits;

    @Override
    public void setMaxDigits(int maxDigits) {
        this.maxDigits = maxDigits;
        super.setMaxDigits(maxDigits);
    }

    public int getMaxDigits() {
        return maxDigits;
    }
}
