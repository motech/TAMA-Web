package org.motechproject.tama.refdata.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tama.common.domain.CouchEntity;

import javax.validation.constraints.NotNull;


@TypeDiscriminator("doc.documentType == 'HealthTip'")
public class HealthTip extends CouchEntity {

    @NotNull
    private String category;

    @NotNull
    private String audioFileName;

    private Integer pullDailySequence;
    private Integer pullWeeklySequence;
    private Integer pushDailySequence;
    private Integer pushWeeklySequence;

    @JsonIgnore
    private Integer playCount = 0;

    protected HealthTip(){

    }

    public HealthTip(String id){
        super();
        this.setId(id);
    }

    public static HealthTip newHealthTip(String category, String audioFileName, Integer pullDailySequence, Integer pullWeeklySequence, Integer pushDailySequence, Integer pushWeeklySequence){
        HealthTip healthTip = new HealthTip();
        healthTip.setCategory(category);
        healthTip.setAudioFileName(audioFileName);
        healthTip.setPullDailySequence(pullDailySequence);
        healthTip.setPullWeeklySequence(pullWeeklySequence);
        healthTip.setPushDailySequence(pushDailySequence);
        healthTip.setPushWeeklySequence(pushWeeklySequence);
        return healthTip;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAudioFileName() {
        return audioFileName;
    }

    public void setAudioFileName(String audioFileName) {
        this.audioFileName = audioFileName;
    }

    public Integer getPullDailySequence() {
        return pullDailySequence;
    }

    public void setPullDailySequence(Integer pullDailySequence) {
        this.pullDailySequence = pullDailySequence;
    }

    public Integer getPullWeeklySequence() {
        return pullWeeklySequence;
    }

    public void setPullWeeklySequence(Integer pullWeeklySequence) {
        this.pullWeeklySequence = pullWeeklySequence;
    }

    public Integer getPushDailySequence() {
        return pushDailySequence;
    }

    public void setPushDailySequence(Integer pushDailySequence) {
        this.pushDailySequence = pushDailySequence;
    }

    public Integer getPushWeeklySequence() {
        return pushWeeklySequence;
    }

    public void setPushWeeklySequence(Integer pushWeeklySequence) {
        this.pushWeeklySequence = pushWeeklySequence;
    }

    @JsonIgnore
    public Integer getSequence(HealthTipSequence sequence){
        switch(sequence){
            case PULL_DAILY:
                return getPullDailySequence();
            case PULL_WEEKLY:
                return getPullWeeklySequence();
            case PUSH_DAILY:
                return getPushDailySequence();
            case PUSH_WEEKLY:
                return getPushWeeklySequence();
        }
        return 0;
    }

    @JsonIgnore
    public Integer getPlayCount(){
        return playCount;
    }

    @JsonIgnore
    public void setPlayCount(Integer playcount){
        this.playCount = playcount;
    }
}
