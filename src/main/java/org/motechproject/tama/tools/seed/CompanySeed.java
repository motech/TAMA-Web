package org.motechproject.tama.tools.seed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.motechproject.tama.domain.Company;
import org.motechproject.tama.repository.Companies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanySeed extends Seed {

	@Autowired
	private Companies companies;
	
	@Override
	public void load() {
		companies.add(new Company("Aurobindo"));
		companies.add(new Company("Cipla"));
		companies.add(new Company("Emcure"));
		companies.add(new Company("Genx"));
		companies.add(new Company("Matrix"));
		companies.add(new Company("Merck"));
		companies.add(new Company("Ranbaxy"));
	}

	public Map<String, Company> loadAllCompanies() {
		Map<String, Company> companyMap = new HashMap<String, Company>();
		List<Company> companyDocs = companies.getAll();

		for (Company company : companyDocs) {
			companyMap.put(company.getName(), company);
		}
		return companyMap;
	}
}
