package org.motechproject.tama.web.model;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;

import java.util.ArrayList;
import java.util.List;

public class UniquePatientMobileNumberWarning {
    private AllPatients allPatients;

    private static final String OF = "   of ";
    private static final String CLINIC = "Clinic";
    private static final String PATIENT = "patient";

    public UniquePatientMobileNumberWarning(AllPatients allPatients) {
        this.allPatients = allPatients;
    }

    public boolean isDuplicate(String mobileNumber) {
        List<Patient> patients = allPatients.findAllByMobileNumber(mobileNumber);
        return CollectionUtils.isEmpty(patients);
    }

    public List<String> shouldDisplayWarningForPatientsMobileNumberDuplicate(String mobileNumber, String patientDocumentID, String clinicID) {
        List<Patient> patients = allPatients.findAllByMobileNumber(mobileNumber);
        List<String> patientsWithNonUniqueMobileNumbers = new ArrayList<>();
        for (Patient patient : patients) {
            if (!((patient.hasSamePatientDocumentID(patientDocumentID) && patient.hasSamePhoneNumber(mobileNumber) && patient.hasSameClinicId(clinicID)))) {
                patientsWithNonUniqueMobileNumbers.add(patient.getPatientId());
            }
        }
        return patientsWithNonUniqueMobileNumbers;
    }

    public List<String> shouldDisplayWarningForPatientsMobileNumberDuplicateWhenPatientIdIsPassed(String mobileNumber, String patientDocumentID, String clinicID) {
        List<Patient> patients = allPatients.findAllByMobileNumber(mobileNumber);
        List<String> patientsWithNonUniqueMobileNumbers = new ArrayList<>();
        for (Patient patient : patients) {
            if (!((patient.hasSamePatientDocumentID(patientDocumentID) && patient.hasSamePhoneNumber(mobileNumber) && patient.hasSameClinicId(clinicID)))) {
                patientsWithNonUniqueMobileNumbers.add(patient.getPatientId());
            }
        }
        return patientsWithNonUniqueMobileNumbers;
    }

    public List<String> shouldDisplayWarningForPatientsMobileNumberDuplicateWhenPatientIdClinicNameArePassed(String mobileNumber, String patientID, String clinicName,String type) {
        List<Patient> patients = allPatients.findAllByMobileNumber(mobileNumber);
        List<String> patientsWithNonUniqueMobileNumbers = new ArrayList<>();
        for (Patient patient : patients) {
            if (!((patient.hasSamePatientID(patientID) && patient.hasSamePhoneNumber(mobileNumber) && patient.hasSameClinicName(clinicName)))) {
                if(PATIENT.equals(type)){
                patientsWithNonUniqueMobileNumbers.add(patient.getPatientId());
                }
                else if(CLINIC.equals(type)){
                    patientsWithNonUniqueMobileNumbers.add(patient.getClinic().getName());
                }
            }
        }
        return patientsWithNonUniqueMobileNumbers;
    }


    public List<String> shouldDisplayWarningForPatientsMobileNumberDuplicateWhenClinicNameIsPassed(String mobileNumber, String patientID, String clinicName,String type) {
        List<Patient> patients = allPatients.findAllByMobileNumber(mobileNumber);
        List<String> patientsWithNonUniqueMobileNumbers = new ArrayList<>();
        for (Patient patient : patients) {
            if (!((patient.hasSamePatientDocumentID(patientID) && patient.hasSamePhoneNumber(mobileNumber) && patient.hasSameClinicName(clinicName)))) {
                if(PATIENT.equals(type)){
                    patientsWithNonUniqueMobileNumbers.add(patient.getPatientId());
                }
                else if(CLINIC.equals(type)){
                    patientsWithNonUniqueMobileNumbers.add(patient.getClinic().getName());
                }
            }
        }
        return patientsWithNonUniqueMobileNumbers;
    }

    public List<String> shouldDisplayWarningForPatientsMobileNumberDuplicateWhenIdOfClinicIsPassed(String mobileNumber, String patientID, String idOfClinic) {
        List<Patient> patients = allPatients.findAllByMobileNumber(mobileNumber);
        List<String> patientsWithNonUniqueMobileNumbers = new ArrayList<>();
        for (Patient patient : patients) {
            if (!((patient.hasSamePatientID(patientID) && patient.hasSamePhoneNumber(mobileNumber) && patient.hasSameIdClinicId(idOfClinic)))) {
                patientsWithNonUniqueMobileNumbers.add(patient.getPatientId());
            }
        }
        return patientsWithNonUniqueMobileNumbers;
    }


    public List<String> findAllMobileNumbersWhichMatchTheGivenNumber(String mobileNumber, String patientID, String clinicID,String type) {
        List<String> patientsWithNonUniqueMobileNumbers = shouldDisplayWarningForPatientsMobileNumberDuplicateWhenPatientIdClinicNameArePassed(mobileNumber, patientID, clinicID,type);
        return CollectionUtils.isEmpty(patientsWithNonUniqueMobileNumbers) ? null : patientsWithNonUniqueMobileNumbers;
    }

    public List<String> findAllMobileNumbersWhichMatchTheGivenNumberCreateClinicVisit(String mobileNumber, String patientID, String clinicName,String type) {
        List<String> patientsWithNonUniqueMobileNumbers = shouldDisplayWarningForPatientsMobileNumberDuplicateWhenClinicNameIsPassed(mobileNumber, patientID, clinicName,type);
        return CollectionUtils.isEmpty(patientsWithNonUniqueMobileNumbers) ? null : patientsWithNonUniqueMobileNumbers;
    }

    public List<String> findAllMobileNumbersWhichMatchTheGivenNumberOnUpdate(String mobileNumber, String patientID, String clinicID) {
        List<String> patientsWithNonUniqueMobileNumbers = shouldDisplayWarningForPatientsMobileNumberDuplicate(mobileNumber, patientID, clinicID);
        return CollectionUtils.isEmpty(patientsWithNonUniqueMobileNumbers) ? null : patientsWithNonUniqueMobileNumbers;
    }

    public boolean checkIfGivenMobileNumberIsUnique(String mobileNumber, String patientID, String clinicID) {
        List<String> patientsWithNonUniqueMobileNumbers = shouldDisplayWarningForPatientsMobileNumberDuplicateWhenIdOfClinicIsPassed(mobileNumber, patientID, clinicID);

        return CollectionUtils.isNotEmpty(patientsWithNonUniqueMobileNumbers);
    }


}
