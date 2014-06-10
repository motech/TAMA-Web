package org.motechproject.tama.facility.repository;

import java.util.ArrayList;
import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.motechproject.tama.common.repository.AllAuditRecords;
import org.motechproject.tama.common.repository.AuditableCouchRepository;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.domain.Clinician;
import org.motechproject.tama.facility.domain.MonitoringAgent;
import org.motechproject.tama.facility.dto.MonitoringAgentDto;
import org.motechproject.tama.facility.reporting.ClinicianRequestMapper;
import org.motechproject.tama.facility.reporting.MonitoringAgentRequestMapper;
import org.motechproject.tama.reporting.service.MonitoringAgentReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AllMonitoringAgents extends
		AuditableCouchRepository<MonitoringAgent> {

	private MonitoringAgentReportingService monitoringAgentReportingService;
	@Autowired
	AllClinics allClinics;

	@Autowired
	public AllMonitoringAgents(
			@Qualifier("tamaDbConnector") CouchDbConnector db,
			AllAuditRecords allAuditRecords,
			MonitoringAgentReportingService monitoringAgentReportingService) {
		super(MonitoringAgent.class, db, allAuditRecords);
		this.monitoringAgentReportingService = monitoringAgentReportingService;
		initStandardDesignDocument();
	}

	@GenerateView
	public MonitoringAgent findByName(String name) {
		ViewQuery q = createQuery("by_monitoringAgentName").key(name)
				.includeDocs(true);
		List<MonitoringAgent> monitoringAgents = db.queryView(q,
				MonitoringAgent.class);
		if (monitoringAgents.isEmpty())
			return null;
		MonitoringAgent monitoringAgent = monitoringAgents.get(0);
		return monitoringAgent;
	}

	@Override
	public void add(MonitoringAgent monitoringAgent, String userName) {
		add(monitoringAgent, userName, true);
	}

	protected void add(MonitoringAgent monitoringAgent, String userName,
			boolean report) {
		super.add(monitoringAgent, userName);
		if (report) {
			monitoringAgentReportingService
					.save(new MonitoringAgentRequestMapper(monitoringAgent)
							.map());
		}
	}

	@Override
	public void remove(MonitoringAgent monitoringAgent, String userName) {
		super.remove(monitoringAgent, userName);
	}

	@Override
	public void update(MonitoringAgent monitoringAgent, String userName) {
		MonitoringAgent dbMonitoringAgent = get(monitoringAgent.getId());
		monitoringAgent.setRevision(dbMonitoringAgent.getRevision());
		super.update(monitoringAgent, userName);
		monitoringAgentReportingService
				.update(new MonitoringAgentRequestMapper(monitoringAgent).map());
	}

	@Override
	public MonitoringAgent get(String id) {
		MonitoringAgent monitoringAgent = super.get(id);
		return monitoringAgent;
	}

	@Override
	public List<MonitoringAgent> getAll() {
		List<MonitoringAgent> monitoringAgentList = super.getAll();
		return monitoringAgentList;
	}

	/*
	 * public MonitoringAgentDto getMonitoringAgentDto(MonitoringAgent
	 * monitoringAgent){ MonitoringAgentDto monitoringAgentDto = new
	 * MonitoringAgentDto();
	 * monitoringAgentDto.setContactNumber(monitoringAgent.getContactNumber());
	 * monitoringAgentDto.setName(monitoringAgent.getName()); List<Clinic>
	 * clinics = allClinics.getAll(); allClinics.loadDependencies(clinics);
	 * for(Clinic clinic:clinics){ if(clinic.getMonitoringAgentId() != null){
	 * if(clinic.getMonitoringAgentId().equals(monitoringAgent.getId())){
	 * monitoringAgentDto.addClinicName(clinic.getName()); } } }
	 * monitoringAgentDto.setClinics();
	 * System.out.println(monitoringAgentDto.getClinics()); return
	 * monitoringAgentDto; }
	 */

	public MonitoringAgent getClinicNames(MonitoringAgent dbMonitoringAgent) {
		List<Clinic> clinics = allClinics.getAll();
		List<Clinic> addedClinics = new ArrayList<Clinic>();
		allClinics.loadDependencies(clinics);
		for (Clinic clinic : clinics) {
			if (clinic.getMonitoringAgentId() != null) {
				if (clinic.getMonitoringAgentId().equals(
						dbMonitoringAgent.getId())) {
					if (!addedClinics.contains(clinic)) {
						dbMonitoringAgent.addClinics(clinic.getName());
						addedClinics.add(clinic);
					}
				}
			}
		}
		return dbMonitoringAgent;
	}

	/*
	 * public List<String> getClinicNames(String id){ List<String> clinicNames =
	 * new ArrayList<String>(); MonitoringAgent monitoringAgent = super.get(id);
	 * for(Clinic clinic:monitoringAgent.getClinics()){
	 * clinicNames.add(clinic.getName()); } return clinicNames; }
	 */

	/*
	 * public void addClinicNameToMonitoringAgent(Clinic clinic,String user){
	 * if(
	 * !clinic.getMonitoringAgent().getClinicsOfAgent().contains(clinic.getName
	 * ())){
	 * update(clinic.getMonitoringAgent().addClinic(clinic.getName()),user); } }
	 */

	// TODO write a query to fetch the list of clinic Id's and loop to find the
	// exact clinic ID

}
