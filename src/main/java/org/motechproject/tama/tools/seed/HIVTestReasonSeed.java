package org.motechproject.tama.tools.seed;

import org.motechproject.tama.domain.HIVTestReason;
import org.motechproject.tama.repository.HIVTestReasons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HIVTestReasonSeed extends Seed {

	@Autowired
	private HIVTestReasons hivTestReasons;
	
	@Override
	public void load() {
		hivTestReasons.add(HIVTestReason.newHIVTestReason("Preemployment"));
		hivTestReasons.add(HIVTestReason.newHIVTestReason("Pre-operative"));
		hivTestReasons.add(HIVTestReason.newHIVTestReason("General Checkup"));
		hivTestReasons.add(HIVTestReason.newHIVTestReason("ANC"));
		hivTestReasons.add(HIVTestReason.newHIVTestReason("Pre-marital"));
		hivTestReasons.add(HIVTestReason.newHIVTestReason("STDs"));
		hivTestReasons.add(HIVTestReason.newHIVTestReason("Symptomatic"));
		hivTestReasons.add(HIVTestReason.newHIVTestReason("Blood Donation"));
		hivTestReasons.add(HIVTestReason.newHIVTestReason("Spouse Detected"));
	}
}