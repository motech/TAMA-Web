package org.motechproject.tamadomain.domain;

public class MedicalHistoryQuestion {

    private String question;

    private boolean historyPresent;

    private boolean requiresComment;

    private String comments;

    public MedicalHistoryQuestion(String question, boolean requiresComment) {
        this.question = question;
        this.requiresComment = requiresComment;
    }

    public MedicalHistoryQuestion() {
    }

    public boolean isRequiresComment() {
        return requiresComment;
    }

    public void setRequiresComment(boolean requiresComment) {
        this.requiresComment = requiresComment;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public boolean isHistoryPresent() {
        return historyPresent;
    }

    public void setHistoryPresent(boolean historyPresent) {
        this.historyPresent = historyPresent;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
