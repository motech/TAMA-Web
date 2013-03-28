package org.motechproject.tama.refdata.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tama.common.domain.CouchEntity;

import javax.validation.constraints.NotNull;

@TypeDiscriminator("doc.documentType == 'IVRLanguage'")
public class IVRLanguage extends CouchEntity {

    @NotNull
    private String name;
    private String code;

    public static final String ENGLISH_CODE = "en";
    public static final String HINDI_CODE = "hi";
    public static final String MARATHI_CODE = "mr";
    public static final String TAMIL_CODE = "ta";
    public static final String TELUGU_CODE = "te";
    public static final String KANNADA_CODE = "kn";
    public static final String MANIPURI_CODE = "mni";
    public static final String GUJARATI_CODE = "gu";

    public IVRLanguage() {
    }

    public IVRLanguage(String id) {
        this.setId(id);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static IVRLanguage newIVRLanguage(String language, String code) {
        IVRLanguage ivrLanguage = new IVRLanguage();
        ivrLanguage.setName(language);
        ivrLanguage.setCode(code);
        return ivrLanguage;
    }
}
