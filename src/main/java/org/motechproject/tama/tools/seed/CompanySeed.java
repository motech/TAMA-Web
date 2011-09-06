package org.motechproject.tama.tools.seed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.motechproject.tama.domain.Company;
import org.motechproject.tama.repository.AllCompanies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanySeed extends Seed {

	@Autowired
	private AllCompanies allCompanies;
	
	@Override
	public void load() {
		allCompanies.add(new Company("Aurobindo"));
		allCompanies.add(new Company("Cipla"));
		allCompanies.add(new Company("Emcure"));
		allCompanies.add(new Company("Genx"));
		allCompanies.add(new Company("Matrix"));
		allCompanies.add(new Company("Merck"));
		allCompanies.add(new Company("Ranbaxy"));
	}

	public Map<String, Company> loadAllCompanies() {
		Map<String, Company> companyMap = new HashMap<String, Company>();
		List<Company> companyDocs = allCompanies.getAll();

		for (Company company : companyDocs) {
			companyMap.put(company.getName(), company);
		}
		return companyMap;
	}
}
