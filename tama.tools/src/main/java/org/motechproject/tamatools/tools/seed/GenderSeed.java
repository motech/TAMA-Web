package org.motechproject.tamatools.tools.seed;

import org.motechproject.tamadomain.domain.Gender;
import org.motechproject.tamadomain.repository.AllGenders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GenderSeed extends Seed {

	@Autowired
	private AllGenders allGenders;
	
	@Override
	public void load() {
		allGenders.add(Gender.newGender("Male"));
		allGenders.add(Gender.newGender("Female"));
	}
}