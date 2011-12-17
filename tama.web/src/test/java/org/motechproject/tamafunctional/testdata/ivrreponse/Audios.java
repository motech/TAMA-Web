package org.motechproject.tamafunctional.testdata.ivrreponse;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.common.util.StringUtil;

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
                if (audioResourceMatchesUrl(audioResource, audioUrl)) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    private boolean audioResourceMatchesUrl(String audioResource, String audioUrl) {
        String resourceFile = String.format("%s.wav", audioResource);
        return resourceFile.equals(StringUtil.lastMatch(audioUrl, "/"));
    }

    @Override
    public String toString() {
        return StringUtils.join(strings, ", ");
    }
}
