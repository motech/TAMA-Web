package org.motechproject.tamafunctional.testdata.ivrreponse;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectDtmf {
    @XStreamImplicit(itemFieldName="playaudio")
    private List<String> playaudios = new ArrayList<String>();
    @XStreamImplicit(itemFieldName="playtext")
    private List<String> playtexts = new ArrayList<String>();

    public boolean hasAudio(String... audioResourceNames) {
        return new Audios(playaudios).hasAudio(audioResourceNames);
    }

    public String audiosPlayed() {
        return new Audios(playaudios).toString();
    }

    public CollectDtmf playAudios(String... playAudios) {
        playaudios.addAll(Arrays.asList(playAudios));
        return this;
    }
}
