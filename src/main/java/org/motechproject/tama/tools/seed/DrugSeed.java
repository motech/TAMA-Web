package org.motechproject.tama.tools.seed;

import java.util.HashMap;
import java.util.Map;

import org.motechproject.tama.domain.Brand;
import org.motechproject.tama.domain.Company;
import org.motechproject.tama.domain.Drug;
import org.motechproject.tama.repository.Drugs;
import org.springframework.beans.factory.annotation.Autowired;


public class DrugSeed implements Seed {

	@Autowired
	private Drugs drugs;
	
	@Autowired
	private CompanySeed companySeed;
	
	@Override
	public void load() {
		Map <String, Company> companies = companySeed.loadAllCompanies();
		
		Drug drug1 = new Drug("AZT+3TC+NVP");
		drug1.addBrand(new Brand("Duovir-N",companies.get("Cipla")));
		drug1.addBrand(new Brand("Zidovex-LN",companies.get("Aurobindo")));
		drug1.addBrand(new Brand("Virocom-N",companies.get("Ranbaxy")));
		drug1.addBrand(new Brand("Zidolam-N",companies.get("Genx")));
		drug1.addBrand(new Brand("Lazid-N",companies.get("Emcure")));
		drugs.add(drug1);
		
		
		Drug drug2 = new Drug("AZT+3TC");
		drug2.addBrand(new Brand("Duovir",companies.get("Cipla")));
		drug2.addBrand(new Brand("Zidovex-L",companies.get("Aurobindo")));
		drug2.addBrand(new Brand("Virocom",companies.get("Ranbaxy")));
		drug2.addBrand(new Brand("Zidolam",companies.get("Genx")));
		drug2.addBrand(new Brand("Lazid",companies.get("Emcure")));
		drug2.addBrand(new Brand("Combivir",companies.get("Merck")));
		drugs.add(drug2);
		
		Drug drug3 = new Drug("NVP");
		drug3.addBrand(new Brand("Nevimune",companies.get("Cipla")));
		drug3.addBrand(new Brand("Nevirex",companies.get("Aurobindo")));
		drug3.addBrand(new Brand("Nevivir",companies.get("Genx")));
		drug3.addBrand(new Brand("Nevir",companies.get("Emcure")));
		drug3.addBrand(new Brand("Viramune",companies.get("Merck")));
		drugs.add(drug3);
		
		Drug drug4 = new Drug("d4T+3TC+NVP");
		drug4.addBrand(new Brand("Triomune",companies.get("Cipla")));
		drug4.addBrand(new Brand("Stavex-LN",companies.get("Aurobindo")));
		drug4.addBrand(new Brand("Virolans30",companies.get("Ranbaxy")));
		drug4.addBrand(new Brand("Nevilast30",companies.get("Genx")));
		drug4.addBrand(new Brand("Emtri 30",companies.get("Emcure")));
		drugs.add(drug4);

		Drug drug5 = new Drug("d4T+3TC");
		drug5.addBrand(new Brand("Lamivir-S",companies.get("Cipla")));
		drug5.addBrand(new Brand("Stavex-L",companies.get("Aurobindo")));
		drug5.addBrand(new Brand("Virolans",companies.get("Ranbaxy")));
		drug5.addBrand(new Brand("Lamistar",companies.get("Genx")));
		drugs.add(drug5);

		Drug drug6 = new Drug("EFV");
		drug6.addBrand(new Brand("Efavir",companies.get("Cipla")));
		drug6.addBrand(new Brand("Viranz",companies.get("Aurobindo")));
		drug6.addBrand(new Brand("Efferven",companies.get("Ranbaxy")));
		drug6.addBrand(new Brand("Estiva",companies.get("Genx")));
		drug6.addBrand(new Brand("Efcure",companies.get("Emcure")));
		drug6.addBrand(new Brand("Stocrin",companies.get("Merck")));
		drugs.add(drug6);
		
		Drug drug7 = new Drug("TDF+3TC");
		drug7.addBrand(new Brand("Tenvir-L",companies.get("Cipla")));
		drug7.addBrand(new Brand("Tavin-L",companies.get("Emcure")));
		drug7.addBrand(new Brand("Ricovir-L",companies.get("Matrix")));
		drugs.add(drug7);
		
		Drug drug8 = new Drug("TDF+FTC");
		drug8.addBrand(new Brand("Tenvir-EM",companies.get("Cipla")));
		drug8.addBrand(new Brand("Forstavir-EM",companies.get("Aurobindo")));
		drug8.addBrand(new Brand("Tavin EM",companies.get("Emcure")));
		drugs.add(drug8);
		
		Drug drug9 = new Drug("TDF+3TC+EFV");
		drug9.addBrand(new Brand("Trioday",companies.get("Cipla")));
		drug9.addBrand(new Brand("Telura",companies.get("Matrix")));
		drugs.add(drug9);
		
		Drug drug10 = new Drug("TDF+FTC+EFV");
		drug10.addBrand(new Brand("Viraday",companies.get("Cipla")));
		drug10.addBrand(new Brand("Forstavir-3",companies.get("Aurobindo")));
		drug10.addBrand(new Brand("Vonavir",companies.get("Emcure")));
		drug10.addBrand(new Brand("Teevir",companies.get("Matrix")));
		drugs.add(drug10);
		
		
	}

	public Map<String, Drug> loadAll() {
		Map<String, Drug> drugMap = new HashMap<String, Drug>();
		for (Drug drug : drugs.getAll()) {
			drugMap.put(drug.getName(), drug);
		}
		return drugMap;
	}
}