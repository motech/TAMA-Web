package org.motechproject.tama.facility.domain;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.TAMAMessages;
import org.motechproject.tama.common.domain.CouchEntity;

@TypeDiscriminator("doc.documentType == 'MonitoringAgent'")
public class MonitoringAgent extends CouchEntity {
	@NotNull
	private String name;
	@NotNull
	@Pattern(regexp = TAMAConstants.MOBILE_NUMBER_REGEX, message = TAMAMessages.MOBILE_NUMBER_REGEX_MESSAGE)
	private String contactNumber;

	
	/*@OneToMany
	private List<Clinic> clinics;*/
	
	private String clinics;

	public MonitoringAgent(){
		
	}
	
	public MonitoringAgent(String id){
		super();
        this.setId(id);
	}
	
	public static MonitoringAgent newMonitoringAgent(){
		return new MonitoringAgent();
	}
	
	/*
	
	public List<String> getClinicsOfAgent() { return clinics; }
	*/
	/*public List<Clinic> getClinics() {
		return this.clinics;
	}

	public void setClinics(List<Clinic> clinics) {
		this.clinics = clinics;
	}*/

	/*
	  private List<String> clinicsOfAgent = new LinkedList<String>();
	 
	  public List<String> getClinicsOfAgent() { return clinicsOfAgent; }
	  
	  public void setClinicsOfAgent(List<String> clinicsOfAgent) {
	  this.clinicsOfAgent = clinicsOfAgent; }
	  
	  public MonitoringAgent addClinic(String newClinic){ for(String clinic :
	  clinicsOfAgent){ if(clinic.equals(newClinic)) return this; }
	  clinicsOfAgent.add(newClinic); return this; }
	 */
	
    @Override
    public String toString() {
        return this.name;
    }
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContactNumber() {
		return this.contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getClinics() {
		return clinics;
	}

	public void setClinics(String clinics) {
		this.clinics = clinics;
	}

	public void addClinics(String newClinic){
		if(this.clinics == null || this.clinics.isEmpty()){
				clinics = newClinic ;
		}
		else{
			clinics = clinics +","+ newClinic;
		}
	}

}
