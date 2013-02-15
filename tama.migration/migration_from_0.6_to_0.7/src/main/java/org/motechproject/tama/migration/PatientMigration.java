package org.motechproject.tama.migration;

import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.tama.migration.repository.PagedPatientsRepository;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.reporting.MedicalHistoryRequestMapper;
import org.motechproject.tama.patient.reporting.PatientRequestMapper;
import org.motechproject.tama.refdata.objectcache.AllGendersCache;
import org.motechproject.tama.refdata.objectcache.AllHIVTestReasonsCache;
import org.motechproject.tama.refdata.objectcache.AllIVRLanguagesCache;
import org.motechproject.tama.refdata.objectcache.AllModesOfTransmissionCache;
import org.motechproject.tama.reporting.service.PatientReportingService;
import org.motechproject.tama.reports.contract.MedicalHistoryRequest;
import org.motechproject.tama.reports.contract.PatientRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientMigration extends Migration<Patient> {

    private AllIVRLanguagesCache allIVRLanguagesCache;
    private AllGendersCache allGendersCache;

    private AllModesOfTransmissionCache allModesOfTransmissionCache;
    private AllHIVTestReasonsCache allHIVTestReasonsCache;

    private PatientReportingService reportingService;
    private PatientRequestMapper patientRequestContractMapper;
    private MedicalHistoryRequestMapper medicalHistoryRequestMapper;

    @Autowired
    public PatientMigration(PagedPatientsRepository patientsRepository,
                            AllIVRLanguagesCache allIVRLanguagesCache,
                            AllGendersCache allGendersCache,
                            AllModesOfTransmissionCache allModesOfTransmissionCache,
                            AllHIVTestReasonsCache allHIVTestReasonsCache,
                            PatientReportingService reportingService) {

        super(patientsRepository);

        this.allIVRLanguagesCache = allIVRLanguagesCache;
        this.allGendersCache = allGendersCache;
        this.allModesOfTransmissionCache = allModesOfTransmissionCache;
        this.allHIVTestReasonsCache = allHIVTestReasonsCache;
        this.reportingService = reportingService;

        patientRequestContractMapper = new PatientRequestMapper(allIVRLanguagesCache, allGendersCache);
        medicalHistoryRequestMapper = new MedicalHistoryRequestMapper(allModesOfTransmissionCache, allHIVTestReasonsCache);
    }

    @Override
    @Seed(version = "2.0", priority = 0)
    public void migrate() {
        super.migrate();
    }

    @Override
    protected void save(Patient patient) {
        PatientRequest request = patientRequestContractMapper.map(patient);
        MedicalHistoryRequest medicalHistoryRequest = medicalHistoryRequestMapper.map(patient);
        reportingService.save(request, medicalHistoryRequest);
    }
}
