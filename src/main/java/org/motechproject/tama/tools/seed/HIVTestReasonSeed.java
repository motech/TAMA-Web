package org.motechproject.tama.tools.seed;

import org.motechproject.tama.domain.HIVTestReason;
import org.motechproject.tama.repository.AllHIVTestReasons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HIVTestReasonSeed extends Seed {

	@Autowired
	private AllHIVTestReasons allHIVTestReasons;
	
	@Override
	public void load() {
		allHIVTestReasons.add(HIVTestReason.newHIVTestReason("Preemployment"));
		allHIVTestReasons.add(HIVTestReason.newHIVTestReason("Pre-operative"));
		allHIVTestReasons.add(HIVTestReason.newHIVTestReason("General Checkup"));
		allHIVTestReasons.add(HIVTestReason.newHIVTestReason("ANC"));
		allHIVTestReasons.add(HIVTestReason.newHIVTestReason("Pre-marital"));
		allHIVTestReasons.add(HIVTestReason.newHIVTestReason("STDs"));
		allHIVTestReasons.add(HIVTestReason.newHIVTestReason("Symptomatic"));
		allHIVTestReasons.add(HIVTestReason.newHIVTestReason("Blood Donation"));
		allHIVTestReasons.add(HIVTestReason.newHIVTestReason("Spouse Detected"));
	}
}