package org.motechproject.tamafunctional.testdata.ivrreponse;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.commons.io.filefilter.FalseFileFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectDtmf {
    @XStreamImplicit(itemFieldName="playaudio")
    private List<String> playaudios = new ArrayList<String>();
    @XStreamImplicit(itemFieldName="playtext")
    private List<String> playtexts = new ArrayList<String>();

    public boolean hasAudio(String... audioResourceNames) {
        for (String audioResource : audioResourceNames) {
            boolean found = false;
            for (String audioUrl : playaudios) {
                if (audioUrl.endsWith(String.format("%s.wav", audioResource))) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    public String playAudio() {
        return playaudios.get(0);
    }

    public CollectDtmf playAudios(String... playAudios) {
        playaudios.addAll(Arrays.asList(playAudios));
        return this;
    }
}
