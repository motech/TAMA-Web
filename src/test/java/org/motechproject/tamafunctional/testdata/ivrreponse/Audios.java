package org.motechproject.tamafunctional.testdata.ivrreponse;

import org.apache.commons.lang.StringUtils;

import java.util.List;

public class Audios {
    private List<String> strings;

    public Audios(List<String> strings) {
        this.strings = strings;
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
