package org.motechproject.tamafunctional.testdata.ivrreponse;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Audios {
    private List<String> strings = new ArrayList<String>();

    public Audios(List<String> strings) {
        this.strings = strings;
        if (this.strings == null)
            this.strings = new ArrayList<String>();
    }

    public boolean hasAudio(String... audioResourceNames) {
        for (String audioResource : audioResourceNames) {
            boolean found = false;
            for (String audioUrl : strings) {
                if (audioUrl.endsWith(String.format("%s.wav", audioResource))) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return StringUtils.join(strings, ", ");
    }
}
