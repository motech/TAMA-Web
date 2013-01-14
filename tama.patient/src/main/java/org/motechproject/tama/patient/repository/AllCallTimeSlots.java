package org.motechproject.tama.patient.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.patient.domain.AllottedSlot;
import org.motechproject.tama.patient.domain.AllottedSlots;
import org.motechproject.tama.patient.domain.CallTimeSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Component
public class AllCallTimeSlots extends AbstractCouchRepository<CallTimeSlot> {

    @Autowired
    protected AllCallTimeSlots(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(CallTimeSlot.class, db);
    }

    @View(name = "get_allotted_slots_v1",
          map = "function(doc) { if(doc.documentType == 'CallTimeSlot') {emit(doc.callTime, {\"callTime\":doc.callTime}) }}",
          reduce = "function (key, values, rereduce) { var slotTime = ''; var allottedCount = 0; if (!rereduce) { slotTime = values[0].callTime; allottedCount = values.length; } else { slotTime = values[0].slotTime; for(var i in values) { allottedCount = allottedCount + values[i].allottedCount; } } return {\"slotTime\":slotTime, \"allottedCount\":allottedCount}; }")
    public AllottedSlots getAllottedSlots() {
        ViewQuery q = createQuery("get_allotted_slots_v1").reduce(true).inclusiveEnd(true).group(true);
        return new AllottedSlots(db.queryView(q, AllottedSlot.class));
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
