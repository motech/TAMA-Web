package org.motechproject.tama.migration.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.motechproject.tama.patient.domain.PatientEventLog;
import org.motechproject.tama.patient.repository.AllPatientEventLogs;
import org.motechproject.tama.reporting.service.PatientEventReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PagedPatientEventsRepository extends AllPatientEventLogs implements Paged<PatientEventLog> {

    @Autowired
    public PagedPatientEventsRepository(@Qualifier("tamaDbConnector") CouchDbConnector db, PatientEventReportingService patientEventReportingService) {
        super(db, patientEventReportingService);
    }

    @Override
    public List<PatientEventLog> get(int skip, int limit) {
        ViewQuery query = createQuery("all").includeDocs(true).skip(skip).limit(limit);
        return db.queryView(query, PatientEventLog.class);
    }

    @Override
    protected void add(PatientEventLog entity, String user, boolean report) {
        super.add(entity, user, report);
    }
}

