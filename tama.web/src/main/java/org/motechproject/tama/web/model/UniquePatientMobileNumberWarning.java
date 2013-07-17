package org.motechproject.tama.web.model;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;

import java.util.ArrayList;
import java.util.List;

public class UniquePatientMobileNumberWarning {
    private AllPatients allPatients;

    private List<String> patientsWithNonUniqueMobileNumbers = null;

    private static final String OF = " of";

    private static final String PATIENT = "Patient - ";

    private static final String CLINIC = " Clinic - ";


    public UniquePatientMobileNumberWarning(AllPatients allPatients) {
        this.allPatients = allPatients;
    }

    public boolean isDuplicate(String mobileNumber) {
        List<Patient> patients = allPatients.findAllByMobileNumber(mobileNumber);
        return CollectionUtils.isEmpty(patients);
    }

    public boolean shouldDisplayWarningForPatientsMobileNumberDuplicate(String mobileNumber) {
        List<Patient> patients = allPatients.findAllByMobileNumber(mobileNumber);
        if (!CollectionUtils.isEmpty(patients)) {
            if (patients.size() > 1) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }


    public List<String> findAllMobileNumbersWhichMatchTheGivenNumber(String mobileNumber, String patientID, String clinicID) {
        List<Patient> patients = allPatients.findAllByMobileNumber(mobileNumber);

        if (!CollectionUtils.isEmpty(patients)) {
            patientsWithNonUniqueMobileNumbers = new ArrayList<>();


            for (int i = 0; i < patients.size(); i++) {
                if (!((patients.get(i).hasSamePatientID(patientID) && patients.get(i).hasSamePhoneNumber(mobileNumber) && patients.get(i).hasSameClinicName(clinicID)))) {
                    StringBuffer message = new StringBuffer(PATIENT).append(patients.get(i).getPatientId()).append(OF + CLINIC).append(patients.get(i).getClinic().getName());
                    patientsWithNonUniqueMobileNumbers.add(message.toString());
                }
            }
        }
        if (patientsWithNonUniqueMobileNumbers == null || patientsWithNonUniqueMobileNumbers.size() == 0) {
            return null;
        }
        return patientsWithNonUniqueMobileNumbers;
    }


    public List<String> findAllMobileNumbersWhichMatchTheGivenNumberCreateClinicVisit(String mobileNumber, String patientID, String clinicID) {
        List<Patient> patients = allPatients.findAllByMobileNumber(mobileNumber);
        if (!CollectionUtils.isEmpty(patients)) {
            patientsWithNonUniqueMobileNumbers = new ArrayList<>();


            for (int i = 0; i < patients.size(); i++) {
                if (!(patients.get(i).hasSamePatientDocumentID(patientID) && patients.get(i).hasSamePhoneNumber(mobileNumber) && patients.get(i).hasSameClinicName(clinicID))) {
                    StringBuffer message = new StringBuffer(PATIENT).append(patients.get(i).getPatientId()).append(OF + CLINIC).append(patients.get(i).getClinic().getName());
                    patientsWithNonUniqueMobileNumbers.add(message.toString());
                }
            }
        }
        if (patientsWithNonUniqueMobileNumbers == null || patientsWithNonUniqueMobileNumbers.size() == 0) {
            return null;
        }
        return patientsWithNonUniqueMobileNumbers;
    }

    public List<String> findAllMobileNumbersWhichMatchTheGivenNumberOnUpdate(String mobileNumber, String patientID, String clinicID) {
        List<Patient> patients = allPatients.findAllByMobileNumber(mobileNumber);
        if (!CollectionUtils.isEmpty(patients)) {
            patientsWithNonUniqueMobileNumbers = new ArrayList<>();


            for (int i = 0; i < patients.size(); i++) {
                if (!((patients.get(i).hasSamePatientID(patientID) && patients.get(i).hasSamePhoneNumber(mobileNumber) && patients.get(i).hasSameClinicId(clinicID)))) {
                    StringBuffer message = new StringBuffer(PATIENT).append(patients.get(i).getPatientId()).append(OF + CLINIC).append(patients.get(i).getClinic().getName());
                    patientsWithNonUniqueMobileNumbers.add(message.toString());
                }
            }
        }
        if (patientsWithNonUniqueMobileNumbers == null || patientsWithNonUniqueMobileNumbers.size() == 0) {
            return null;
        }
        return patientsWithNonUniqueMobileNumbers;
    }

    public boolean checkIfGivenMobileNumberIsUnique(String mobileNumber, String patientID, String clinicID) {
        List<Patient> patients = allPatients.findAllByMobileNumber(mobileNumber);
        if (!CollectionUtils.isEmpty(patients)) {
            patientsWithNonUniqueMobileNumbers = new ArrayList<>();


            for (int i = 0; i < patients.size(); i++) {
                if (!(patientID.equals(patients.get(i).getPatientId()) && mobileNumber.equals(patients.get(i).getMobilePhoneNumber()) && clinicID.equals(patients.get(i).getClinic().getId()))) {
                    StringBuffer message = new StringBuffer(PATIENT).append(patients.get(i).getPatientId()).append(OF + CLINIC).append(patients.get(i).getClinic().getName());
                    patientsWithNonUniqueMobileNumbers.add(message.toString());
                }
            }
        }
        if (patientsWithNonUniqueMobileNumbers == null || patientsWithNonUniqueMobileNumbers.size() == 0) {
            return false;
        } else {
            return true;
        }
    }


}
