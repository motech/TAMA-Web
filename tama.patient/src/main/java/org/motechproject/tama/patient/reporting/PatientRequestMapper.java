package org.motechproject.tama.patient.reporting;

import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.refdata.domain.IVRLanguage;
import org.motechproject.tama.refdata.objectcache.AllIVRLanguagesCache;
import org.motechproject.tama.reports.contract.PatientRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientRequestMapper {

    private AllIVRLanguagesCache ivrLanguagesCache;

    @Autowired
    public PatientRequestMapper(AllIVRLanguagesCache ivrLanguagesCache) {
        this.ivrLanguagesCache = ivrLanguagesCache;
    }

    public PatientRequest map(Patient patient) {
        PatientRequest request = new PatientRequest();
        IVRLanguage language = ivrLanguagesCache.getBy(patient.getPatientPreferences().getIvrLanguageId());

        new BasicDetails(patient).copyTo(request);
        new IVRDetails(patient).copyTo(request, language.getCode());
        return request;
    }
}
