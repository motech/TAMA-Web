// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.motechproject.tama;

import java.lang.String;

privileged aspect Patient_Roo_ToString {
    
    public String Patient.toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DateOfBirth: ").append(getDateOfBirth()).append(", ");
        sb.append("Gender: ").append(getGender()).append(", ");
        sb.append("Id: ").append(getId()).append(", ");
        sb.append("Initials: ").append(getInitials()).append(", ");
        sb.append("MobilePhoneNumber: ").append(getMobilePhoneNumber()).append(", ");
        sb.append("PatientId: ").append(getPatientId()).append(", ");
        sb.append("PrincipalDoctor: ").append(getPrincipalDoctor()).append(", ");
        sb.append("TravelTimeToClinicInDays: ").append(getTravelTimeToClinicInDays()).append(", ");
        sb.append("TravelTimeToClinicInHours: ").append(getTravelTimeToClinicInHours()).append(", ");
        sb.append("TravelTimeToClinicInMinutes: ").append(getTravelTimeToClinicInMinutes()).append(", ");
        sb.append("Version: ").append(getVersion());
        return sb.toString();
    }
    
}
