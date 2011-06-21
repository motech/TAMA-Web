// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.motechproject.tama;

import java.util.List;
import java.util.Random;
import org.motechproject.tama.Title;
import org.springframework.stereotype.Component;

privileged aspect TitleDataOnDemand_Roo_DataOnDemand {
    
    declare @type: TitleDataOnDemand: @Component;
    
    private Random TitleDataOnDemand.rnd = new java.security.SecureRandom();
    
    private List<Title> TitleDataOnDemand.data;
    
    public Title TitleDataOnDemand.getNewTransientTitle(int index) {
        org.motechproject.tama.Title obj = new org.motechproject.tama.Title();
        setType(obj, index);
        return obj;
    }
    
    public void TitleDataOnDemand.setType(Title obj, int index) {
        java.lang.String type = "type_" + index;
        obj.setType(type);
    }
    
    public Title TitleDataOnDemand.getSpecificTitle(int index) {
        init();
        if (index < 0) index = 0;
        if (index > (data.size() - 1)) index = data.size() - 1;
        Title obj = data.get(index);
        return Title.findTitle(obj.getId());
    }
    
    public Title TitleDataOnDemand.getRandomTitle() {
        init();
        Title obj = data.get(rnd.nextInt(data.size()));
        return Title.findTitle(obj.getId());
    }
    
    public boolean TitleDataOnDemand.modifyTitle(Title obj) {
        return false;
    }
    
    public void TitleDataOnDemand.init() {
        data = org.motechproject.tama.Title.findTitleEntries(0, 10);
        if (data == null) throw new IllegalStateException("Find entries implementation for 'Title' illegally returned null");
        if (!data.isEmpty()) {
            return;
        }
        
        data = new java.util.ArrayList<org.motechproject.tama.Title>();
        for (int i = 0; i < 10; i++) {
            org.motechproject.tama.Title obj = getNewTransientTitle(i);
            obj.persist();
            obj.flush();
            data.add(obj);
        }
    }
    
}
