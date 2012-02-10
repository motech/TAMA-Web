package org.motechproject.tama.patient.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.support.View;
import org.motechproject.tama.common.domain.TimeOfDay;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.patient.domain.CallTimeSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllCallTimeSlots extends AbstractCouchRepository<CallTimeSlot> {

    @Autowired
    protected AllCallTimeSlots(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(CallTimeSlot.class, db);
    }

    @View(name = "find_by_slot_time_range", map = "function(doc) {if (doc.documentType =='CallTimeSlot' && doc.callTime) {emit(doc.callTime, doc.patientDocumentId);}}", reduce = "_count")
    public int countOfPatientsAllottedForSlot(TimeOfDay slotStartTime, TimeOfDay slotEndTime) {
        ViewQuery q = createQuery("find_by_slot_time_range").startKey(slotStartTime).endKey(slotEndTime).reduce(true);
        final ViewResult viewResult = db.queryView(q);
        return rowCount(viewResult);
    }

}
