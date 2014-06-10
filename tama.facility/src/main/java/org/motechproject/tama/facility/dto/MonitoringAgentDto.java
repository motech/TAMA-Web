package org.motechproject.tama.facility.dto;

import java.util.ArrayList;
import java.util.List;

public class MonitoringAgentDto {
	
	private String name;
	
	private String contactNumber;
	
	private List<String> clinicNames = new ArrayList<String>();
	
	private String clinics;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String mobileNumber) {
		this.contactNumber = mobileNumber;
	}

	public List<String> getClinicNames() {
		return clinicNames;
	}

	public void setClinicNames(List<String> clinicNames) {
		this.clinicNames = clinicNames;
	}
	
	public MonitoringAgentDto addClinicName(String newClinic){ 
		if(this.clinicNames.isEmpty()){
			this.clinicNames.add(newClinic);
			return this;
		}
		for(String clinic : this.clinicNames){ 
			if(clinic.equals(newClinic)) 
				return this; 
			}
		this.clinicNames.add(newClinic); 
		return this; 
		}
	
	public void setClinics(){
		StringBuilder buff = new StringBuilder();
		String sep = "";
		for (String clinic : clinicNames) {
		    buff.append(sep);
		    buff.append(clinic);
		    sep = "&";
		}
		this.clinics = buff.toString();
	}
	
	public String getClinics(){
		return this.clinics;
	}
	

}
