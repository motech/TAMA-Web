package org.motechproject.tamaperformance.datasetup;

import org.mockito.Mock;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.builder.ClinicianBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.domain.Clinician;
import org.motechproject.tama.refdata.domain.City;
import org.motechproject.tama.refdata.repository.AllCities;
import org.motechproject.tama.web.ClinicController;
import org.motechproject.tama.web.ClinicianController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

import static org.mockito.MockitoAnnotations.initMocks;

@Component
public class ClinicanSetupService {

    final Logger log = Logger.getLogger(this.getClass().getName());

    @Mock
    BindingResult bindingResult;
    @Mock
    Model uiModel;
    @Mock
    HttpServletRequest request;

    @Autowired
    ClinicController clinicController;
    @Autowired
    ClinicianController clinicianController;
    @Autowired
    AllCities allCities;

    public ClinicanSetupService() {
        initMocks(this);
    }

    public void createClinicians(int numberOfClinician) {
        City city = allCities.getAll().get(0);
        for (int i = 0; i < numberOfClinician; i++) {
            Clinic clinic = ClinicBuilder.startRecording().withDefaults().withName("clinic" + i).withCity(city).build();
            clinicController.create(clinic, bindingResult, uiModel, request);

            Clinician clinician = ClinicianBuilder.startRecording().withDefaults().withName("clinician" + i).withUserName("clinician" + i).withClinic(clinic).build();
            clinicianController.create(clinician, bindingResult, uiModel, request);
        }
    }
}
