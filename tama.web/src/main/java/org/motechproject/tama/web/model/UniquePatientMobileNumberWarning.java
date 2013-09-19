package org.motechproject.tama.web.model;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;

import java.util.ArrayList;
import java.util.List;

public class UniquePatientMobileNumberWarning {
    private AllPatients allPatients;

    public UniquePatientMobileNumberWarning(AllPatients allPatients) {
        this.allPatients = allPatients;
    }

    public boolean isDuplicate(String mobileNumber) {
        List<Patient> patients = allPatients.findAllByMobileNumber(mobileNumber);
        return CollectionUtils.isEmpty(patients);
    }

    public List<Patient> shouldDisplayWarningForPatientsMobileNumberDuplicate(String mobileNumber, String patientDocumentID, String clinicID) {
        List<Patient> patients = allPatients.findAllByMobileNumber(mobileNumber);
        List<Patient> patientsWithNonUniqueMobileNumbers = new ArrayList<>();
        for (Patient patient : patients) {
            if (!((patient.hasSamePatientDocumentID(patientDocumentID) && patient.hasSamePhoneNumber(mobileNumber) && patient.hasSameClinicId(clinicID)))) {
                patientsWithNonUniqueMobileNumbers.add(patient);
            }
        }
        return patientsWithNonUniqueMobileNumbers;
    }

    public List<Patient> shouldDisplayWarningForPatientsMobileNumberDuplicateWhenPatientIdIsPassed(String mobileNumber, String patientDocumentID, String clinicID) {
        List<Patient> patients = allPatients.findAllByMobileNumber(mobileNumber);
        List<Patient> patientsWithNonUniqueMobileNumbers = new ArrayList<>();
        for (Patient patient : patients) {
            if (!((patient.hasSamePatientDocumentID(patientDocumentID) && patient.hasSamePhoneNumber(mobileNumber) && patient.hasSameClinicId(clinicID)))) {
                patientsWithNonUniqueMobileNumbers.add(patient);
            }
        }
        return patientsWithNonUniqueMobileNumbers;
    }

    public List<Patient> shouldDisplayWarningForPatientsMobileNumberDuplicateWhenPatientIdClinicNameArePassed(String mobileNumber, String patientID, String clinicName) {
        List<Patient> patients = allPatients.findAllByMobileNumber(mobileNumber);
        List<Patient> patientsWithNonUniqueMobileNumbers = new ArrayList<>();
        for (Patient patient : patients) {
            if (!((patient.hasSamePatientID(patientID) && patient.hasSamePhoneNumber(mobileNumber) && patient.hasSameClinicName(clinicName)))) {
                patientsWithNonUniqueMobileNumbers.add(patient);
            }
        }
        return patientsWithNonUniqueMobileNumbers;
    }


    public List<Patient> shouldDisplayWarningForPatientsMobileNumberDuplicateWhenClinicNameIsPassed(String mobileNumber, String patientID, String clinicName) {
        List<Patient> patients = allPatients.findAllByMobileNumber(mobileNumber);
        List<Patient> patientsWithNonUniqueMobileNumbers = new ArrayList<>();
        for (Patient patient : patients) {
            if (!((patient.hasSamePatientDocumentID(patientID) && patient.hasSamePhoneNumber(mobileNumber) && patient.hasSameClinicName(clinicName)))) {
                    patientsWithNonUniqueMobileNumbers.add(patient);
            }
        }
        return patientsWithNonUniqueMobileNumbers;
    }

    public List<Patient> shouldDisplayWarningForPatientsMobileNumberDuplicateWhenIdOfClinicIsPassed(String mobileNumber, String patientID, String idOfClinic) {
        List<Patient> patients = allPatients.findAllByMobileNumber(mobileNumber);
        List<Patient> patientsWithNonUniqueMobileNumbers = new ArrayList<>();
        for (Patient patient : patients) {
            if (!((patient.hasSamePatientID(patientID) && patient.hasSamePhoneNumber(mobileNumber) && patient.hasSameIdClinicId(idOfClinic)))) {
                patientsWithNonUniqueMobileNumbers.add(patient);
            }
        }
        return patientsWithNonUniqueMobileNumbers;
    }


    public List<Patient> findAllMobileNumbersWhichMatchTheGivenNumber(String mobileNumber, String patientID, String clinicID) {
        List<Patient> patientsWithNonUniqueMobileNumbers = shouldDisplayWarningForPatientsMobileNumberDuplicateWhenPatientIdClinicNameArePassed(mobileNumber, patientID, clinicID);
        return CollectionUtils.isEmpty(patientsWithNonUniqueMobileNumbers) ? null : patientsWithNonUniqueMobileNumbers;
    }

    public List<Patient> findAllMobileNumbersWhichMatchTheGivenNumberCreateClinicVisit(String mobileNumber, String patientID, String clinicName,String type) {
        List<Patient> patientsWithNonUniqueMobileNumbers = shouldDisplayWarningForPatientsMobileNumberDuplicateWhenClinicNameIsPassed(mobileNumber, patientID, clinicName);
        return CollectionUtils.isEmpty(patientsWithNonUniqueMobileNumbers) ? null : patientsWithNonUniqueMobileNumbers;
    }

    public List<Patient> findAllMobileNumbersWhichMatchTheGivenNumberOnUpdate(String mobileNumber, String patientID, String clinicID) {
        List<Patient> patientsWithNonUniqueMobileNumbers = shouldDisplayWarningForPatientsMobileNumberDuplicate(mobileNumber, patientID, clinicID);
        return CollectionUtils.isEmpty(patientsWithNonUniqueMobileNumbers) ? null : patientsWithNonUniqueMobileNumbers;
    }

    public boolean checkIfGivenMobileNumberIsUnique(String mobileNumber, String patientID, String clinicID) {
        List<Patient> patientsWithNonUniqueMobileNumbers = shouldDisplayWarningForPatientsMobileNumberDuplicateWhenIdOfClinicIsPassed(mobileNumber, patientID, clinicID);
        return CollectionUtils.isNotEmpty(patientsWithNonUniqueMobileNumbers);
    }


}
