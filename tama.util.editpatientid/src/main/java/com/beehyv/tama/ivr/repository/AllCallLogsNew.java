package com.beehyv.tama.ivr.repository;

import java.util.List;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.motechproject.tama.ivr.repository.AllCallLogs;
import org.motechproject.tama.ivr.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
public class AllCallLogsNew extends AllCallLogs{
	@Autowired
	protected AllCallLogsNew(@Qualifier("tamaDbConnector")CouchDbConnector db) {
		super(db);
		// TODO Auto-generated constructor stub
	}
	
	 public void editPatientId(CallLogSearch callLogSearch,String patientId)
	    {
	    	List<CallLog> callLogs = findCallLogsForDateRangePatientIdAndClinic(callLogSearch);
	    	List<String>likelyPatientIds = null ;
	    	for(CallLog callLog:callLogs){
	    		likelyPatientIds = callLog.getLikelyPatientIds();
	    		for(int i = 0 ; i < likelyPatientIds.size() ; i ++){
	    			if((likelyPatientIds.get(i).equals((callLogSearch.getPatientId()))))
					{
	    				likelyPatientIds.set(i, patientId)	;
					}
	    		}
	    		callLog.setLikelyPatientIds(likelyPatientIds);
	    		callLog.patientId(patientId);
	    		super.update(callLog);
	    	}
	    	
	    }
	 @Override
	 public List<CallLog> findCallLogsForDateRangePatientIdAndClinic(CallLogSearch callLogSearch) {
	        ComplexKey startKey = ComplexKey.of(callLogSearch.getCallLogType().name(), callLogSearch.getClinicId(), callLogSearch.getPatientId(), callLogSearch.getFromDate());
	        ComplexKey endKey = ComplexKey.of(callLogSearch.getCallLogType().name(), callLogSearch.getClinicId(), callLogSearch.getPatientId(), callLogSearch.getToDate());
	        ViewQuery q = createQuery("find_by_callType_clinicId_patientId_and_date_range")
	                .startKey(startKey).endKey(endKey).includeDocs(true)
	                .reduce(false);
	        return db.queryView(q, CallLog.class);
	    }

}
