package org.motechproject.tama.tools;

import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.refdata.domain.City;
import org.motechproject.tama.refdata.repository.AllCities;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class SetupClinic {

    public static final String APPLICATION_CONTEXT_XML = "applicationToolsContext.xml";
    private AllClinics allClinics;
    private AllCities allCities;

    public SetupClinic() {
        ApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);

        allClinics = context.getBean(AllClinics.class);
        allCities = context.getBean(AllCities.class);
    }

    public static void main(String[] args) {
        if (args.length != 5) {
            System.err.println("Invalid arguments!");
            return;
        }
        SetupClinic setup = new SetupClinic();
        setup.clinic(args[0], args[1], args[2], args[3], args[4]);
    }

    private Clinic clinic(String id, String name, String address, String cityName, String phone) {
        City city = allCities.findByName(cityName);

        Clinic clinic = new Clinic(id);
        clinic.setName(name);
        clinic.setAddress(address);
        clinic.setCity(city);
        clinic.setPhone(phone);

        Clinic.ClinicianContact clinicianContact = new Clinic.ClinicianContact();
        clinicianContact.setName("Blah");
        clinicianContact.setPhoneNumber("1111111111");
        List<Clinic.ClinicianContact> clinicianContacts = new ArrayList<Clinic.ClinicianContact>();
        clinicianContacts.add(clinicianContact);

        clinic.setClinicianContacts(clinicianContacts);

        allClinics.add(clinic);
        return clinic;
    }
}
