package org.motechproject.tamadomain.domain;

import org.motechproject.tamacommon.domain.BaseEntity;

import javax.validation.constraints.NotNull;

public class Brand extends BaseEntity implements Comparable<Brand> {

    @NotNull
    private String name;

    @NotNull
    private String companyId;

    protected Brand() {
    	
    }
    
    public Brand(String name) {
		this.name = name;
	}
    
    public Brand(String name, Company company) {
    	this(name);
		setCompany(company);
	}

	private void setCompany(Company company) {
		this.companyId = company.getId();
	}

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyId(){
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Brand other = (Brand) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

    @Override
    public int compareTo(Brand o) {
        return name.toLowerCase().compareTo(o.name.toLowerCase());
    }
}
