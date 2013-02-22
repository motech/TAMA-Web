package org.motechproject.tama.migration;


import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.ivr.domain.SMSLog;
import org.motechproject.tama.ivr.reporting.SMSLogMapper;
import org.motechproject.tama.ivr.reporting.SMSType;
import org.motechproject.tama.migration.repository.PagedSMSLogRepository;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.reporting.service.SMSReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SMSLogMigration extends Migration<SMSLog> {

    private AllPatients allPatients;
    private AllClinics allClinics;
    private SMSReportingService reportingService;

    @Autowired
    public SMSLogMigration(PagedSMSLogRepository repository, AllPatients allPatients, AllClinics allClinics, SMSReportingService reportingService) {
        super(repository);
        this.allPatients = allPatients;
        this.allClinics = allClinics;
        this.reportingService = reportingService;
    }

    @Override
    @Seed(version = "2.0", priority = 0)
    public void migrate() {
        super.migrate();
    }

    @Override
    protected void save(SMSLog document) {
        boolean clinician = document.getMessage().contains("(");
        if (!clinician) {
            Patient entity = allPatients.findByMobileNumber(document.getRecipient());
            migrateOTCSMS(document, entity);
        } else {
            Clinic.ClinicianContact contact = allClinics.findByPhoneNumber(document.getRecipient());
            migrateClinicianSMS(document, contact);
        }
    }

    private void migrateClinicianSMS(SMSLog document, Clinic.ClinicianContact contact) {
        if (null != contact) {
            reportingService.save(new SMSLogMapper(contact.getId(), SMSType.Clinician, document).map());
        }
    }

    private void migrateOTCSMS(SMSLog document, Patient entity) {
        if (null != entity) {
            reportingService.save(new SMSLogMapper(entity.getId(), SMSType.OTC, document).map());
        }
    }
}
