package org.motechproject.tamatools.tools.seed;

import org.motechproject.tamadomain.domain.Brand;
import org.motechproject.tamadomain.domain.*;
import org.motechproject.tamadomain.domain.Company;
import org.motechproject.tamadomain.domain.Drug;
import org.motechproject.tamadomain.repository.AllDrugs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DrugSeed extends Seed {

	@Autowired
	private AllDrugs drugs;
	
	@Autowired
	private CompanySeed companySeed;
	
	@Override
	public void load() {
		Map <String, Company> allCompanies = companySeed.loadAllCompanies();
		
		Drug drug1 = new Drug("AZT+3TC+NVP");
		drug1.addBrand(new Brand("Duovir-N",allCompanies.get("Cipla")));
		drug1.addBrand(new Brand("Zidovex-LN",allCompanies.get("Aurobindo")));
		drug1.addBrand(new Brand("Virocom-N",allCompanies.get("Ranbaxy")));
		drug1.addBrand(new Brand("Zidolam-N",allCompanies.get("Genx")));
		drug1.addBrand(new Brand("Lazid-N",allCompanies.get("Emcure")));
		drugs.add(drug1);
		
		
		Drug drug2 = new Drug("AZT+3TC");
		drug2.addBrand(new Brand("Duovir",allCompanies.get("Cipla")));
		drug2.addBrand(new Brand("Zidovex-L",allCompanies.get("Aurobindo")));
		drug2.addBrand(new Brand("Virocom",allCompanies.get("Ranbaxy")));
		drug2.addBrand(new Brand("Zidolam",allCompanies.get("Genx")));
		drug2.addBrand(new Brand("Lazid",allCompanies.get("Emcure")));
		drug2.addBrand(new Brand("Combivir",allCompanies.get("Merck")));
		drugs.add(drug2);
		
		Drug drug3 = new Drug("NVP");
		drug3.addBrand(new Brand("Nevimune",allCompanies.get("Cipla")));
		drug3.addBrand(new Brand("Nevirex",allCompanies.get("Aurobindo")));
		drug3.addBrand(new Brand("Nevivir",allCompanies.get("Genx")));
		drug3.addBrand(new Brand("Nevir",allCompanies.get("Emcure")));
		drug3.addBrand(new Brand("Viramune",allCompanies.get("Merck")));
		drugs.add(drug3);
		
		Drug drug4 = new Drug("d4T+3TC+NVP");
		drug4.addBrand(new Brand("Triomune",allCompanies.get("Cipla")));
		drug4.addBrand(new Brand("Stavex-LN",allCompanies.get("Aurobindo")));
		drug4.addBrand(new Brand("Virolans30",allCompanies.get("Ranbaxy")));
		drug4.addBrand(new Brand("Nevilast30",allCompanies.get("Genx")));
		drug4.addBrand(new Brand("Emtri30",allCompanies.get("Emcure")));
		drugs.add(drug4);

		Drug drug5 = new Drug("d4T+3TC");
		drug5.addBrand(new Brand("Lamivir-S",allCompanies.get("Cipla")));
		drug5.addBrand(new Brand("Stavex-L",allCompanies.get("Aurobindo")));
		drug5.addBrand(new Brand("Virolans",allCompanies.get("Ranbaxy")));
		drug5.addBrand(new Brand("Lamistar",allCompanies.get("Genx")));
		drugs.add(drug5);

		Drug drug6 = new Drug("EFV");
		drug6.addBrand(new Brand("Efavir",allCompanies.get("Cipla")));
		drug6.addBrand(new Brand("Viranz",allCompanies.get("Aurobindo")));
		drug6.addBrand(new Brand("Efferven",allCompanies.get("Ranbaxy")));
		drug6.addBrand(new Brand("Estiva",allCompanies.get("Genx")));
		drug6.addBrand(new Brand("Efcure",allCompanies.get("Emcure")));
		drug6.addBrand(new Brand("Stocrin",allCompanies.get("Merck")));
		drugs.add(drug6);
		
		Drug drug7 = new Drug("TDF+3TC");
		drug7.addBrand(new Brand("Tenvir-L",allCompanies.get("Cipla")));
		drug7.addBrand(new Brand("Tavin-L",allCompanies.get("Emcure")));
		drug7.addBrand(new Brand("Ricovir-L",allCompanies.get("Matrix")));
		drugs.add(drug7);
		
		Drug drug8 = new Drug("TDF+FTC");
		drug8.addBrand(new Brand("Tenvir-EM",allCompanies.get("Cipla")));
		drug8.addBrand(new Brand("Forstavir-EM",allCompanies.get("Aurobindo")));
		drug8.addBrand(new Brand("Tavin EM",allCompanies.get("Emcure")));
		drugs.add(drug8);
		
		Drug drug9 = new Drug("TDF+3TC+EFV");
		drug9.addBrand(new Brand("Trioday",allCompanies.get("Cipla")));
		drug9.addBrand(new Brand("Telura",allCompanies.get("Matrix")));
		drugs.add(drug9);
		
		Drug drug10 = new Drug("TDF+FTC+EFV");
		drug10.addBrand(new Brand("Viraday",allCompanies.get("Cipla")));
		drug10.addBrand(new Brand("Forstavir-3",allCompanies.get("Aurobindo")));
		drug10.addBrand(new Brand("Vonavir",allCompanies.get("Emcure")));
		drug10.addBrand(new Brand("Teevir",allCompanies.get("Matrix")));
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