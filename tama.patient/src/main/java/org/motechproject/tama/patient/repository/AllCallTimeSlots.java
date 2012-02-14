package org.motechproject.tama.patient.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.support.View;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.patient.domain.CallTimeSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllCallTimeSlots extends AbstractCouchRepository<CallTimeSlot> {

    @Autowired
    protected AllCallTimeSlots(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(CallTimeSlot.class, db);
    }

    @View(name = "find_by_slot_time_range", map = "function(doc) {if (doc.documentType =='CallTimeSlot' && doc.callTime) {emit(doc.callTime, doc.patientDocumentId);}}", reduce = "_count")
    public int countOfPatientsAllottedForSlot(LocalTime slotStartTime, LocalTime slotEndTime) {
        DateTime startKey = new DateTime(0).withTime(slotStartTime.getHourOfDay(), slotStartTime.getMinuteOfHour(), 0, 0);
        DateTime endKey = new DateTime(0).withTime(slotEndTime.getHourOfDay(), slotEndTime.getMinuteOfHour(), 0, 0);
        ViewQuery q = createQuery("find_by_slot_time_range").startKey(startKey).endKey(endKey).reduce(true);
        final ViewResult viewResult = db.queryView(q);
        return rowCount(viewResult);
    }

    @View(name = "find_by_slot_time_and_patient_id", map = "function(doc) {if (doc.documentType =='CallTimeSlot' && doc.callTime) {emit([doc.callTime, doc.patientDocumentId], doc._id);}}")
    public List<CallTimeSlot> findBySlotTimeAndPatientId(LocalTime callTime, String patientDocumentId) {
        DateTime callTimeKey = new DateTime(0).withTime(callTime.getHourOfDay(), callTime.getMinuteOfHour(), 0, 0);
        ViewQuery find_by_slot_time_range = createQuery("find_by_slot_time_and_patient_id").key(ComplexKey.of(callTimeKey, patientDocumentId)).includeDocs(true);
        return db.queryView(find_by_slot_time_range, CallTimeSlot.class);
    }

    @Override
    public void add(CallTimeSlot callSlot) {
        CallTimeSlot callTimeSlot = singleResult(findBySlotTimeAndPatientId(callSlot.getCallTime(), callSlot.getPatientDocumentId()));
        if (callTimeSlot == null) super.add(callSlot);
    }
}
