package org.motechproject.tama.tools.seed;

import org.motechproject.tama.domain.Gender;
import org.motechproject.tama.repository.Genders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GenderSeed extends Seed {

	@Autowired
	private Genders genders;
	
	@Override
	public void load() {
		genders.add(Gender.newGender("Male"));
		genders.add(Gender.newGender("Female"));
	}
}