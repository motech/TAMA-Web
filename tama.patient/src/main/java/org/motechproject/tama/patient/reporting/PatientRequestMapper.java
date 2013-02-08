package org.motechproject.tama.patient.reporting;

import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.refdata.domain.Gender;
import org.motechproject.tama.refdata.domain.IVRLanguage;
import org.motechproject.tama.refdata.objectcache.AllGendersCache;
import org.motechproject.tama.refdata.objectcache.AllIVRLanguagesCache;
import org.motechproject.tama.reports.contract.PatientRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientRequestMapper {

    private AllIVRLanguagesCache ivrLanguagesCache;
    private AllGendersCache gendersCache;

    @Autowired
    public PatientRequestMapper(AllIVRLanguagesCache ivrLanguagesCache, AllGendersCache gendersCache) {
        this.ivrLanguagesCache = ivrLanguagesCache;
        this.gendersCache = gendersCache;
    }

    public PatientRequest map(Patient patient) {
        PatientRequest request = new PatientRequest();
        IVRLanguage language = ivrLanguagesCache.getBy(patient.getPatientPreferences().getIvrLanguageId());
        Gender gender = gendersCache.getBy(patient.getGenderId());

        new BasicDetails(patient).copyTo(request);
        new IVRDetails(patient, getIVRLanguageCode(language), getGenderString(gender)).copyTo(request);
        return request;
    }

    private String getIVRLanguageCode(IVRLanguage language) {
        return null == language ? "" : language.getName();
    }

    private String getGenderString(Gender gender) {
        return (null == gender) ? "" : gender.getType();
    }
}
