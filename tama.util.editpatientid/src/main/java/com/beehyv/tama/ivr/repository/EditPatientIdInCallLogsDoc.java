package com.beehyv.tama.ivr.repository;

import java.util.List;

import org.apache.log4j.Logger;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.motechproject.tama.ivr.repository.AllCallLogs;
import org.motechproject.tama.ivr.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class EditPatientIdInCallLogsDoc extends AllCallLogs {

	private static final Logger LOGGER = Logger
			.getLogger(EditPatientIdInCallLogsDoc.class);

	@Autowired
	protected EditPatientIdInCallLogsDoc(
			@Qualifier("tamaDbConnector") CouchDbConnector db) {
		super(db);
	}

	public void editPatientId(List<CallLog> callLogs, String docid,
			String oldPatientId, String patientId) {
		List<String> likelyPatientIds = null;
		for (CallLog callLog : callLogs) {
			if (callLog.getPatientDocumentId().equals(docid)) {
				likelyPatientIds = callLog.getLikelyPatientIds();
				for (int i = 0; i < likelyPatientIds.size(); i++) {
					if ((likelyPatientIds.get(i).equals((oldPatientId)))) {
						likelyPatientIds.set(i, patientId);
					}
				}
				callLog.setLikelyPatientIds(likelyPatientIds);
				callLog.patientId(patientId);
				super.update(callLog);
			}
		}
	}

	@Override
	public List<CallLog> findCallLogsForDateRangePatientIdAndClinic(
			CallLogSearch callLogSearch) {

		findTotalNumberOfCallLogsForDateRangePatientIdAndClinic(callLogSearch);
		ComplexKey startKey = ComplexKey.of(callLogSearch.getCallLogType()
				.name(), callLogSearch.getClinicId(), callLogSearch
				.getPatientId(), callLogSearch.getFromDate());
		ComplexKey endKey = ComplexKey.of(
				callLogSearch.getCallLogType().name(),
				callLogSearch.getClinicId(), callLogSearch.getPatientId(),
				callLogSearch.getToDate());
		ViewQuery q = createQuery(
				"find_by_callType_clinicId_patientId_and_date_range")
				.startKey(startKey).endKey(endKey).includeDocs(true)
				.reduce(false);
		List<CallLog> calllogList = db.queryView(q, CallLog.class);
		LOGGER.debug("size of call log " + calllogList.size());
		LOGGER.debug(findTotalNumberOfCallLogsForDateRangePatientIdAndClinic(callLogSearch));
		return calllogList;
	}

}
