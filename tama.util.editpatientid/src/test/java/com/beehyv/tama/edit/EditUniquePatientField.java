package com.beehyv.tama.edit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.ektorp.CouchDbConnector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.UniquePatientField;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllUniquePatientFields;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/applicationContext.xml"})
public class EditUniquePatientField {

	@Autowired
    protected CouchDbConnector tamaDbConnector;
	
	@Autowired
	private AllPatients allPatients;
	
	@Autowired
	private AllUniquePatientFields allUniquePatientFields;
	
	@Test
	public void addConstraints()throws IOException{
		
		File file = new File("/home/abhinav/Desktop/karthik/patients1.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
	 FileWriter fw = new FileWriter(file.getAbsoluteFile());
	 BufferedWriter bw = new BufferedWriter(fw);
		
		
		List<Patient> patients = allPatients.getAll();
		//List<Patient> duplicatePatients = new ArrayList<Patient>();
		for(Patient patient : patients){
			
			
			List<UniquePatientField> uniqueFields = allUniquePatientFields.get(patient);
			//int count = 0;
			System.out.printf("\n\n For Patient Id: %s :::",patient.getPatientId());
			
			bw.write(String.format("\n\n For Patient Id: %s :::",patient.getPatientId()));
			if(uniqueFields.isEmpty()){
				System.out.printf("--->>>have nothing.");
				bw.write("--->>>have nothing.");
				
				if(checkDummyPatientId(patient)){
					bw.write("--->>>THIS IS DUMY");
				}
				else{
				for (String field : patient.uniqueFields()) {
			             allUniquePatientFields.add(new UniquePatientField(field, patient.getId()));
			             System.out.println("should be creating UniquePatientFieldDoc");
			            }}
			}
			if(uniqueFields.size() == 2){
				System.out.printf("--->>>have both.");
				bw.write("--->>>have both.");
			}
			if(uniqueFields.size() == 1){
				for(UniquePatientField uniqueField:uniqueFields){
					if(uniqueField.getId().contains(Patient.CLINIC_AND_PATIENT_ID_UNIQUE_CONSTRAINT)){
						System.out.printf("--->>>have ClinicId/PatientId.");
						bw.write("--->>>have ClinicId/PatientId.");
						if(checkDummyPaasscode(patient)){
							bw.write("--->>>THIS HAS DUMY PASSCODE & PHONENUMBER.");
						}
						else
						{
						allUniquePatientFields.add(new UniquePatientField(patient.phoneNumberAndPasscode(), patient.getId()));
						}  
					}
					if(uniqueField.getId().contains(Patient.PHONE_NUMBER_AND_PASSCODE_UNIQUE_CONSTRAINT)){
						System.out.printf("--->>>have Phone no/Passcode.");
						bw.write("--->>>have Phone no/Passcode.");
			            
						allUniquePatientFields.add(new UniquePatientField(patient.clinicAndPatientId(), patient.getId()));

					}
				}
				System.out.printf("--->>>have Only one.");
				bw.write("--->>>have Only one.");

			}
			
		}
		
		bw.close();
	}
	
	public boolean checkDummyPatientId(Patient patient){
		
		List<Patient> patients = allPatients.getAll();
		for(Patient dbPatient:patients){
			if(dbPatient.getPatientId().equals(patient.getPatientId()) && !dbPatient.getId().equals(patient.getId()) && dbPatient.getClinic_id().equals(patient.getClinic_id())){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkDummyPaasscode(Patient patient){
		List<Patient> patients = allPatients.getAll();
		for(Patient dbPatient:patients){
			if(dbPatient.getMobilePhoneNumber().equals(patient.getMobilePhoneNumber()) && 
					dbPatient.getPatientPreferences().getPasscode().equals(patient.getPatientPreferences().getPasscode())
					&& !dbPatient.getId().equals(patient.getId())){
				return true;
			}
		}
		
		return false;
	}
	
}
