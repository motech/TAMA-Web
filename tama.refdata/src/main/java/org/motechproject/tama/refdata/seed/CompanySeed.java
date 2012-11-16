package org.motechproject.tama.refdata.seed;

import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.tama.refdata.domain.Company;
import org.motechproject.tama.refdata.repository.AllCompanies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CompanySeed {

    @Autowired
    private AllCompanies allCompanies;

    @Seed(version = "1.0", priority = 0)
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
