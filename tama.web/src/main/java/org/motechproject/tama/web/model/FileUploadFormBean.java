package org.motechproject.tama.web.model;

import org.motechproject.tama.refdata.domain.IVRLanguage;
import java.util.List;

public class FileUploadFormBean {
    String language;
    String filename;
    List<IVRLanguage> ivrLanguages;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<IVRLanguage> getIvrLanguages() {
        return ivrLanguages;
    }

    public void setIvrLanguages(List<IVRLanguage> ivrLanguages) {
        this.ivrLanguages = ivrLanguages;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
