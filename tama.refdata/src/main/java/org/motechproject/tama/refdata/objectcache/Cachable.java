package org.motechproject.tama.refdata.objectcache;

import org.motechproject.tama.common.repository.AbstractCouchRepository;

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

    public List<T> getAll() {
        return repository.getAll();
    }

    public void refresh(){
        objectMap.clear();
        populateObjectMap();
    }

    private void populateObjectMap() {
         for (T t : getAll()) {
             this.objectMap.put(getKey(t), t);
         }
     }
}
