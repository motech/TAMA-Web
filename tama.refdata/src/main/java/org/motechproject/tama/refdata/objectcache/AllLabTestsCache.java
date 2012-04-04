package org.motechproject.tama.refdata.objectcache;

import org.motechproject.tama.refdata.domain.LabTest;
import org.motechproject.tama.refdata.repository.AllLabTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class AllLabTestsCache extends Cachable<LabTest> {
    private HashMap<String, LabTest> nameObjectMap = new HashMap<String, LabTest>();

    @Autowired
    public AllLabTestsCache(AllLabTests allLabTests) {
        super(allLabTests);
        populateNameObjectMap();
    }

    public LabTest getByName(String name){
        return nameObjectMap.get(name);
    }

    @Override
    public void refresh(){
        super.refresh();
        nameObjectMap.clear();
        populateNameObjectMap();
    }

    @Override
    protected String getKey(LabTest labTest) {
        return labTest.getId();
    }

    @Override
    protected int compareTo(LabTest t1, LabTest t2) {
        return t1.getName().compareTo(t2.getName());
    }

    private void populateNameObjectMap() {
        for (LabTest labTest : getAll()) {
            nameObjectMap.put(labTest.getName(), labTest);
        }
    }
}
