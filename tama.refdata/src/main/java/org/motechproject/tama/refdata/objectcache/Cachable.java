package org.motechproject.tama.refdata.objectcache;

import org.motechproject.tama.common.repository.AbstractCouchRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Cachable<T> {
    protected HashMap<String, T> objectMap = new HashMap<String, T>();
    protected AbstractCouchRepository repository;

    public Cachable(AbstractCouchRepository repository) {
        this.repository = repository;
        populateObjectMap();
    }

    protected abstract String getKey(T t);

    public T getBy(String key){
        return objectMap.get(key);
    }

    public void refresh(){
        objectMap.clear();
        populateObjectMap();
    }

    public List<T> getAll() {
        return new ArrayList<T>(objectMap.values());
    }

    private void populateObjectMap() {
         for (T t : getAllFromRepository()) {
             this.objectMap.put(getKey(t), t);
         }
     }

    private List<T> getAllFromRepository() {
        return repository.getAll();
    }
}
