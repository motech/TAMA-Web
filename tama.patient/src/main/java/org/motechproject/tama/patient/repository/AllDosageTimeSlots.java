package org.motechproject.tama.patient.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.support.View;
import org.motechproject.tama.common.domain.TimeOfDay;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.patient.domain.DosageTimeSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllDosageTimeSlots extends AbstractCouchRepository<DosageTimeSlot> {

    @Autowired
    protected AllDosageTimeSlots(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(DosageTimeSlot.class, db);
    }

    @View(name = "find_by_slot_time_range", map = "function(doc) {if (doc.documentType =='DosageTimeSlot' && doc.dosageTime) {emit(doc.dosageTime, doc.patientDocumentId);}}", reduce = "_count")
    public int countOfPatientsAllottedForSlot(TimeOfDay slotStartTime, TimeOfDay slotEndTime) {
        ViewQuery q = createQuery("find_by_slot_time_range").startKey(slotStartTime).endKey(slotEndTime).reduce(true);
        final ViewResult viewResult = db.queryView(q);
        return rowCount(viewResult);
    }

}
