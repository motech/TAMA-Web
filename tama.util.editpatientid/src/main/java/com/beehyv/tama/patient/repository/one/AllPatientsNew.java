package com.beehyv.tama.patient.repository.one;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.functors.AllPredicate;
import org.codehaus.jackson.map.ObjectMapper;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.support.ListFunction;
import org.ektorp.support.View;
import org.ektorp.support.CouchDbRepositorySupport;
import org.motechproject.tama.common.domain.CouchEntity;
import org.motechproject.tama.common.repository.AuditableCouchRepository;
import org.motechproject.tama.common.repository.AllAuditRecords;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllUniquePatientFields;
import org.motechproject.tama.refdata.objectcache.AllGendersCache;
import org.motechproject.tama.refdata.objectcache.AllHIVTestReasonsCache;
import org.motechproject.tama.refdata.objectcache.AllIVRLanguagesCache;
import org.motechproject.tama.refdata.objectcache.AllModesOfTransmissionCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.motechproject.tama.common.repository.AuditableCouchRepository;
@Component
public class AllPatientsNew{

	
	
	@Autowired
	AllAuditRecords allAuditRecords;
	
	@Autowired
	AllUniquePatientFieldsNew allUniquePatientFieldsNew;
	
	@Resource(name="tamaDbConnector")
	private CouchDbConnector tamaDbConnector;
	
	@Autowired
	private AllPatients allPatients ;
	
	
	public void editPatientId(String patientId,String clinicId, String newPatientId){
    	Patient editPatient = allPatients.findByPatientIdAndClinicId(patientId,clinicId);
    	if(editPatient.getPatientId().equals(newPatientId)){
    		System.out.println("same patint id no need to update.");
    	}
    	editPatient.setPatientId(newPatientId);
    	updateUniqueFieldDoc(editPatient,"karthik@beehyv");    	
    	//Patient patient = findByPatientIdAndClinicId(newPatientId,clinicId);
    	//return patient;
    }
	
	public void updateUniqueFieldDoc(Patient entity, String userName) {
        //Patient dbPatient = get(entity.getId());
		
        
        update(entity, userName);
        allUniquePatientFieldsNew.updatingNewPatientIds(entity);
       // auditableCouchRepository.update(entity, userName);
    }
	
	  /* @Override
	    @View(name = "all_patients_join_clinic", map = "function(doc) {" +
	            "                             if (doc.documentType =='Patient') {" +
	            "                                   emit([doc.clinic_id, 1], doc);" +
	            "                             } else if(doc.documentType =='Clinic') {" +
	            "                                   emit([doc._id, 0], doc);" +
	            "                             }" +
	            "                         }")
	    @ListFunction(name = "all_patients_join_clinic_list", function = "function(head, req) { " +
	            "                 var headers = {'Content-Type': 'application/json'};  " +
	            "                 var result;  " +
	            "                 if(req.query.include_docs != 'true') {  " +
	            "                       start({'code': 400, headers: headers});  " +
	            "                       result = {'error': 'I require include_docs=true'};  " +
	            "                 } else {  " +
	            "                        start({'headers': headers});  " +
	            "                        result = {'rows': []};  " +
	            "                        var clinicRow = getRow(); " +
	            "                        while(clinicRow) { " +
	            "                           var nextRow;" +
	            "                           while(nextRow = getRow()){ " +
	            "                               if(nextRow.value.type === 'Patient'){ " +
	            "                                    var patientRow = nextRow; " +
	            "                                    patientRow.value.clinic = clinicRow.value; " +
	            "                                    var ektorpRow={'value':{}};" +
	            "                                    ektorpRow.value=patientRow.value; " +
	            "                                    result.rows.push(ektorpRow);  " +
	            "                               }else{" +
	            "                                    break;" +
	            "                               } " +
	            "                           } " +
	            "                           clinicRow = nextRow; " +
	            "                        }  " +
	            "                 } " +
	            "  send(JSON.stringify(result)); " +
	            "}")
	    public List<Patient> getAll() {
	        ComplexKey startKey = ComplexKey.of(null, null);
	        ComplexKey endKey = ComplexKey.of(ComplexKey.emptyObject(), ComplexKey.emptyObject());
	        List<Patient> patients = new ArrayList<Patient>();
	        try {
	            ViewQuery q = createQuery("all_patients_join_clinic").startKey(startKey).endKey(endKey).includeDocs(true).listName("all_patients_join_clinic_list");
	            ViewResult result = db.queryView(q);
	            ObjectMapper mapper = new ObjectMapper();
	            for (ViewResult.Row row : result) {
	                Patient patient = mapper.readValue(row.getValueAsNode(), Patient.class);
	                //loadPatientDependencies(patient, false);
	                patients.add(patient);
	            }
	        } catch (IOException e) {
	        }
	        return patients;
	    }
	   
	   @Override
	   @View(name = "find_by_patient_id_and_clinic_id", map = "function(doc) {if (doc.documentType =='Patient' && doc.patientId && doc.clinic_id) {emit([doc.patientId.toLowerCase(), doc.clinic_id.toLowerCase()], doc._id);}}")
	    public Patient findByPatientIdAndClinicId(final String patientId, final String clinicId) {
	        ComplexKey key = ComplexKey.of(patientId.toLowerCase(), clinicId.toLowerCase());
	        ViewQuery q = createQuery("find_by_patient_id_and_clinic_id").key(key).includeDocs(true);
	        Patient patient = singleResult(db.queryView(q, Patient.class));
	        //loadPatientDependencies(patient, true);
	        return patient;
	    }
*/
	  
	    public void update(Patient entity, String userName) {
	        Patient dbPatient = get(entity.getId());
	        //allUniquePatientFieldsNew.update(entity,dbPatient);
	        allPatients.update(entity, userName);
	    }
	   
	 
	    public Patient get(String id) {
	        Patient patient = (Patient) allPatients.get(id);
	        //loadPatientDependencies(patient, true);
	        return patient;
	    }

		public List<Patient> getAll() {
			return allPatients.getAll();
					
		}

}
