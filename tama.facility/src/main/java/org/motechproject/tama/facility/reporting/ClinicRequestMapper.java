package org.motechproject.tama.facility.reporting;

import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.refdata.objectcache.AllCitiesCache;
import org.motechproject.tama.reports.contract.ClinicRequest;


public class ClinicRequestMapper {

    private Clinic clinic;

    private AllCitiesCache allCitiesCache;

    public ClinicRequestMapper(AllCitiesCache allCitiesCache, Clinic clinic) {
        this.allCitiesCache = allCitiesCache;
        this.clinic = clinic;
    }

    public ClinicRequest map() {
        ClinicRequest request = new ClinicRequest();
        request.setClinicId(clinic.getId());
        request.setClinicName(clinic.getName());
        request.setCityName(getCityName());
        request.setMonitoringAgentId(clinic.getMonitoringAgentId());
        request.setGreetingName(clinic.getGreetingName());
        request.setAddress(clinic.getAddress());
        request.setContactNumber(clinic.getPhone());
        return request;
    }

    private String getCityName() {
        return allCitiesCache.getBy(clinic.getCityId()).getName();
    }
}
