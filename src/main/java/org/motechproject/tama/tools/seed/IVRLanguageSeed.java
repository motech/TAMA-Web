package org.motechproject.tama.tools.seed;

import org.motechproject.tama.domain.IVRLanguage;
import org.motechproject.tama.repository.IVRLanguages;
import org.springframework.beans.factory.annotation.Autowired;

public class IVRLanguageSeed implements Seed 
{

	@Autowired
	private IVRLanguages languages;
	
	@Override
	public void load() {
		languages.add(new IVRLanguage("Hindi"));
		languages.add(new IVRLanguage("English"));
		languages.add(new IVRLanguage("Telugu"));
		languages.add(new IVRLanguage("Kannada"));
		languages.add(new IVRLanguage("Tamil"));
		languages.add(new IVRLanguage("Manipuri"));
		languages.add(new IVRLanguage("Marathi"));
	}
	
}