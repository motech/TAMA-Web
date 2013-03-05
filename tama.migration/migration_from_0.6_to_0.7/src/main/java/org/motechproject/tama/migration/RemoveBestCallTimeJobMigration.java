package org.motechproject.tama.migration;

import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.tama.migration.repository.PagedPatientsRepository;
import org.motechproject.tama.outbox.service.OutboxService;
import org.motechproject.tama.patient.domain.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RemoveBestCallTimeJobMigration extends Migration<Patient> {

    private OutboxService outboxService;

    @Autowired
    public RemoveBestCallTimeJobMigration(PagedPatientsRepository patientsRepository, OutboxService outboxService) {
        super(patientsRepository);
        this.outboxService = outboxService;
    }

    @Override
    @Seed(version = "2.0", priority = 0)
    public void migrate() {
        super.migrate();
    }

    @Override
    protected void save(Patient patient) {
        if (patient.isOnDailyPillReminder() && patient.hasAgreedToBeCalledAtBestCallTime()) {
            outboxService.disEnroll(patient);
        }
    }
}
