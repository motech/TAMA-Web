package org.motechproject.tama.patient.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllVitalStatistics extends AbstractCouchRepository<VitalStatistics> {

    @Autowired
    public AllVitalStatistics(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(VitalStatistics.class, db);
        initStandardDesignDocument();
    }

    @View(name = "find_by_patientId", map = "function(doc) {if (doc.documentType =='VitalStatistics' && doc.patientId) {emit(doc.patientId, doc._id);}}")
    public VitalStatistics findByPatientId(String patientId) {
        List<VitalStatistics> vitalStatisticsOfPatient = db.queryView(createQuery("find_by_patientId").key(patientId).includeDocs(true), VitalStatistics.class);
        return singleResult(vitalStatisticsOfPatient);
    }

    @Override
    public void update(VitalStatistics vitalStatistics) {
        VitalStatistics existingVitalStatistics = get(vitalStatistics.getId());
        vitalStatistics.setRevision(existingVitalStatistics.getRevision());
        super.update(vitalStatistics);
    }
}
