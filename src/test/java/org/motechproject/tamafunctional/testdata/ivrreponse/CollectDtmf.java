package org.motechproject.tamafunctional.testdata.ivrreponse;

public class CollectDtmf {
    private String playaudio;
    private String playtext;

    public boolean playsAudio(String audioUrl) {
        return playaudio.endsWith(String.format("%s.wav", audioUrl));
    }

    public String playAudio() {
        return playaudio;
    }
}
