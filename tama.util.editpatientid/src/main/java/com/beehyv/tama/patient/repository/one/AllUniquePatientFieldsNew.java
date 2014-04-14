package com.beehyv.tama.patient.repository.one;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.patient.repository.AllUniquePatientFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.motechproject.tama.patient.domain.Patient;

@Component
public class AllUniquePatientFieldsNew extends AllUniquePatientFields{
	
	
	@Autowired
	public AllUniquePatientFieldsNew(@Qualifier("tamaDbConnector") CouchDbConnector db) {
		super(db);
		// TODO Auto-generated constructor stub
	}
	
	
	public void updatingNewPatientIds(Patient patient)
	{
		remove(patient);
		add(patient);
    }

}
