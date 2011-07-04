package org.motechproject.tama.tools;


import org.motechproject.tama.domain.Gender;
import org.motechproject.tama.domain.IVRLanguage;
import org.motechproject.tama.repository.Genders;
import org.motechproject.tama.repository.IVRLanguages;
import org.springframework.beans.factory.annotation.Autowired;


public class SeedData {
	
    @Autowired
    private Genders genders;

    @Autowired
    private IVRLanguages languages;

    public void init() {
        loadGenders();
        loadLanguages();
    }

	private void loadLanguages() {
        languages.add(language("Tamil"));
        languages.add(language("Manipuri"));
        languages.add(language("Kannada"));
        languages.add(language("Hindi"));
        languages.add(language("Telugu"));
        languages.add(language("Marathi"));
        languages.add(language("English"));
    }

    private void loadGenders() {
        genders.add(gender("Male"));
        genders.add(gender("Female"));
    }

    private Gender gender(String type) {
        Gender gender = new Gender();
        gender.setType(type);
        return gender;
    }

    private IVRLanguage language(String name) {
        IVRLanguage ivrLanguage = new IVRLanguage();
        ivrLanguage.setName(name);
        return ivrLanguage;
    }
}
