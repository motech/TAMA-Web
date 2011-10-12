package org.motechproject.tama.tools;

import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Clinician;
import org.motechproject.tama.repository.AllClinicians;
import org.motechproject.tama.repository.AllClinics;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SetupClinician {

    public static final String APPLICATION_CONTEXT_XML = "META-INF/spring/applicationContext-tools.xml";
    private AllClinics allClinics;
    private AllClinicians allClinicians;

    public SetupClinician() {
        ApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);

        allClinics = context.getBean(AllClinics.class);
        allClinicians  = context.getBean(AllClinicians.class);
    }

    public static void main(String[] args) {
        if (args.length != 5) {
            System.err.println("Invalid arguments!");
            return;
        }
        SetupClinician setup = new SetupClinician();
        setup.clinician(args[0], args[1], args[2], args[3], args[4]);
    }

private Clinician clinician(String clinicId, String clinicianName, String username, String password, String contactNumber) {
        Clinic clinic = allClinics.get(clinicId);
        Clinician clinician = new Clinician();
        clinician.setClinic(clinic);
        clinician.setName(clinicianName);
        clinician.setRole(Clinician.Role.Doctor);
        clinician.setUsername(username);
        clinician.setPassword(password);
        clinician.setContactNumber(contactNumber);

        allClinicians.add(clinician);

        return clinician;
    }
}
