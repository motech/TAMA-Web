package org.motechproject.tama.web.model;


import org.apache.commons.collections.CollectionUtils;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;

import java.util.ArrayList;
import java.util.List;

public class UniquePatientMobileNumberWarning {


   private AllPatients allPatients;

   private List<String> patientsWithNonUniqueMobileNumbers = null;

   private static final String OF=" of";

   private static final String PATIENT="Patient - "  ;

   private  static final String CLINIC= " Clinic - ";




    public UniquePatientMobileNumberWarning(AllPatients allPatients)
    {
            this.allPatients=allPatients;
    }

    public boolean checkIfMobileNumberIsDuplicateOrNot(String mobileNumber)
    {
        List<Patient> patients = allPatients.findAllByMobileNumber(mobileNumber);
        if(!CollectionUtils.isEmpty(patients))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean checkIfMobileNumberIsDuplicateOrNotOnUpdate(String mobileNumber)
    {
        List<Patient> patients = allPatients.findAllByMobileNumber(mobileNumber);
        if(!CollectionUtils.isEmpty(patients))
        {
            if( patients.size()>1)
            {
             return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return true;
        }
    }


    public List<String> findAllMobileNumbersWhichMatchTheGivenNumber(String mobileNumber,String patientID,String clinicID)
    {
        List<Patient> patients = allPatients.findAllByMobileNumber(mobileNumber);
        String message = null;
        if(!CollectionUtils.isEmpty(patients))
        {
            patientsWithNonUniqueMobileNumbers = new ArrayList<>();


            for(int i=0;i<patients.size();i++)
            {
                if(!(patientID.equals(patients.get(i).getPatientId()) && mobileNumber.equals(patients.get(i).getMobilePhoneNumber()) && clinicID.equals(patients.get(i).getClinic().getName())))
                {
                 message  = PATIENT+patients.get(i).getPatientId()+OF+CLINIC+patients.get(i).getClinic().getName();
                 patientsWithNonUniqueMobileNumbers.add(message);
                }
            }
        }
        if(patientsWithNonUniqueMobileNumbers==null || patientsWithNonUniqueMobileNumbers.size()==0)
        {
            return null;
        }
        return patientsWithNonUniqueMobileNumbers;
    }


    public List<String> findAllMobileNumbersWhichMatchTheGivenNumberCreateClinicVisit(String mobileNumber,String patientID,String clinicID)
    {
        List<Patient> patients = allPatients.findAllByMobileNumber(mobileNumber);
        String message = null;
        if(!CollectionUtils.isEmpty(patients))
        {
            patientsWithNonUniqueMobileNumbers = new ArrayList<>();


            for(int i=0;i<patients.size();i++)
            {
                if(!(patientID.equals(patients.get(i).getId()) && mobileNumber.equals(patients.get(i).getMobilePhoneNumber()) && clinicID.equals(patients.get(i).getClinic().getName())))
                {
                    message  = PATIENT+patients.get(i).getPatientId()+OF+CLINIC+patients.get(i).getClinic().getName();
                    patientsWithNonUniqueMobileNumbers.add(message);
                }
            }
        }
        if(patientsWithNonUniqueMobileNumbers==null || patientsWithNonUniqueMobileNumbers.size()==0)
        {
            return null;
        }
        return patientsWithNonUniqueMobileNumbers;
    }

    public List<String> findAllMobileNumbersWhichMatchTheGivenNumberOnUpdate(String mobileNumber,String patientID,String clinicID)
    {
        List<Patient> patients = allPatients.findAllByMobileNumber(mobileNumber);
        String message = null;
        if(!CollectionUtils.isEmpty(patients))
        {
            patientsWithNonUniqueMobileNumbers = new ArrayList<>();


            for(int i=0;i<patients.size();i++)
            {
                if(!(patientID.equals(patients.get(i).getPatientId()) && mobileNumber.equals(patients.get(i).getMobilePhoneNumber()) && clinicID.equals(patients.get(i).getClinic().getId())))
                {
                    message  = PATIENT+patients.get(i).getPatientId()+OF+CLINIC+patients.get(i).getClinic().getName();
                    patientsWithNonUniqueMobileNumbers.add(message);
                }
            }
        }
        if(patientsWithNonUniqueMobileNumbers==null || patientsWithNonUniqueMobileNumbers.size()==0)
        {
            return null;
        }
        return patientsWithNonUniqueMobileNumbers;
    }

    public boolean checkIfGivenMobileNumberIsUnique(String mobileNumber,String patientID,String clinicID)
    {
        List<Patient> patients = allPatients.findAllByMobileNumber(mobileNumber);
        String message = null;
        if(!CollectionUtils.isEmpty(patients))
        {
            patientsWithNonUniqueMobileNumbers = new ArrayList<>();


            for(int i=0;i<patients.size();i++)
            {
                if(!(patientID.equals(patients.get(i).getPatientId()) && mobileNumber.equals(patients.get(i).getMobilePhoneNumber()) && clinicID.equals(patients.get(i).getClinic().getId())))
                {
                    message  = PATIENT+patients.get(i).getPatientId()+OF+CLINIC+patients.get(i).getClinic().getName();
                    patientsWithNonUniqueMobileNumbers.add(message);
                }
            }
        }
        if(patientsWithNonUniqueMobileNumbers==null || patientsWithNonUniqueMobileNumbers.size()==0)
        {
            return false;
        }
        else
        {
          return  true;
        }
    }



}
