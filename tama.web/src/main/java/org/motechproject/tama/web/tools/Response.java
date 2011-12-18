package org.motechproject.tama.web.tools;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@XStreamAlias("response")
public class Response {

    @XStreamAsAttribute
    private String sid;

    private String dial;

    @XStreamImplicit(itemFieldName = "playaudio")
    private List<String> playaudios = new ArrayList<String>();

    @XStreamImplicit(itemFieldName = "playtext")
    private List<String> playtexts = new ArrayList<String>();

    private CollectDtmf collectdtmf;

    @XStreamAlias("hangup")
    private HangUp hangUp;
    public static final int EXCLUDING_FACTOR = 1;
    public static final String LEFT_BOUNDING_SUBSEQUENCE = "/";
    public static final String RIGHT_BOUNDING_SUBSEQUENCE = ".wav";

    public String sid() {
        return sid;
    }

    public boolean collectDtmf() {
        return collectdtmf != null;
    }

    public List<String> responsePlayed() {
        if (collectDtmf()) {
            List<String> responsesPlayed = collectdtmf.responsePlayed();
            if (collectdtmf.isPlayAudio()) {
                return parsePlayedResponsesToFormat(responsesPlayed);
            } else {
                return responsesPlayed;
            }
        }
        return getResponsesPlayed();
    }

    private List<String> getResponsesPlayed() {
        if (playaudios != null && !playaudios.isEmpty()) {
            return parsePlayedResponsesToFormat(playaudios);
        }
        if (playtexts != null && !playtexts.isEmpty()) {
            return playtexts;
        }
        return Collections.emptyList();
    }

    private List<String> parsePlayedResponsesToFormat(List<String> playAudios) {
        List<String> parsedResponses = new ArrayList<String>();
        String responseToBeDisplayed;
        for (String responsePlayed : playAudios) {
            responseToBeDisplayed = responsePlayed.substring(responsePlayed.lastIndexOf(LEFT_BOUNDING_SUBSEQUENCE) + EXCLUDING_FACTOR, responsePlayed.indexOf(RIGHT_BOUNDING_SUBSEQUENCE));
            parsedResponses.add(responseToBeDisplayed);
        }
        return parsedResponses;
    }
}

