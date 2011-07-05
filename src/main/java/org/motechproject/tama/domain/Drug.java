package org.motechproject.tama.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

import org.ektorp.support.TypeDiscriminator;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
@RooEntity
@TypeDiscriminator("doc.documentType == 'Drug'")

public class Drug {

    @NotNull
    private String name;

    private String regimenCompositionId;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Brand> brands = new HashSet<Brand>();
    
	protected Drug() {
	}

	public Drug(String name) {
		this.name = name;
	}

	public void addBrand(Brand brand) {
		brands.add(brand);
	}

	public void removeBrand(Brand brand) {
		brands.remove(brand);
	}
}
