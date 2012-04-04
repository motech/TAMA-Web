package org.motechproject.tama.refdata.objectcache;

import org.motechproject.tama.refdata.domain.LabTest;
import org.motechproject.tama.refdata.repository.AllLabTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

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
    public List<LabTest> getAll() {
        List<LabTest> all = super.getAll();
        Collections.sort(all, new Comparator<LabTest>() {
            @Override
            public int compare(LabTest labTest1, LabTest labTest2) {
                return labTest1.getName().compareTo(labTest2.getName());
            }
        });
        return all;
    }

    private void populateNameObjectMap() {
        for (LabTest labTest : getAll()) {
            nameObjectMap.put(labTest.getName(), labTest);
        }
    }
}
