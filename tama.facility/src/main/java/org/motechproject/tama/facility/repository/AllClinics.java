package org.motechproject.tama.facility.repository;

import org.apache.commons.lang.StringUtils;
import org.ektorp.CouchDbConnector;
import org.motechproject.tama.common.repository.AllAuditRecords;
import org.motechproject.tama.common.repository.AuditableCouchRepository;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.reporting.ClinicRequestMapper;
import org.motechproject.tama.facility.reporting.ClinicianContactRequestMapper;
import org.motechproject.tama.refdata.objectcache.AllCitiesCache;
import org.motechproject.tama.reporting.ClinicReportingRequest;
import org.motechproject.tama.reporting.service.ClinicReportingService;
import org.motechproject.tama.reports.contract.ClinicRequest;
import org.motechproject.tama.reports.contract.ClinicianContactRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Arrays.asList;

@Component
public class AllClinics extends AuditableCouchRepository<Clinic> {

    private AllCitiesCache allCities;
    private ClinicReportingService clinicReportingService;

    @Autowired
    public AllClinics(@Qualifier("tamaDbConnector") CouchDbConnector db, AllCitiesCache allCities, AllAuditRecords allAuditRecords, ClinicReportingService clinicReportingService) {
        super(Clinic.class, db, allAuditRecords);
        this.allCities = allCities;
        this.clinicReportingService = clinicReportingService;
        initStandardDesignDocument();
    }

    @Override
    public void add(Clinic entity, String user) {
        super.add(entity, user);
        publishClinicReport(entity);
    }

    @Override
    public void update(Clinic entity, String user) {
        super.update(entity, user);
        ClinicRequest clinicRequest = new ClinicRequestMapper(allCities, entity).map();
        List<ClinicianContactRequest> contactRequests = new ClinicianContactRequestMapper(entity).map();
        clinicReportingService.update(new ClinicReportingRequest(clinicRequest, contactRequests));
    }

    @Override
    public List<Clinic> getAll() {
        List<Clinic> clinicList = super.getAll();
        loadDependencies(clinicList);
        return clinicList;
    }

    @Override
    public Clinic get(String id) {
        Clinic clinic = super.get(id);
        loadDependencies(asList(clinic));
        return clinic;
    }

    private void publishClinicReport(Clinic entity) {
        ClinicRequest clinicRequest = new ClinicRequestMapper(allCities, entity).map();
        List<ClinicianContactRequest> contactRequests = new ClinicianContactRequestMapper(entity).map();
        clinicReportingService.save(new ClinicReportingRequest(clinicRequest, contactRequests));
    }

    protected void loadDependencies(List<Clinic> clinicList) {
        for (Clinic clinic : clinicList) {
            if (!StringUtils.isEmpty(clinic.getCityId()))
                clinic.setCity(allCities.getBy(clinic.getCityId()));
        }
    }
}
