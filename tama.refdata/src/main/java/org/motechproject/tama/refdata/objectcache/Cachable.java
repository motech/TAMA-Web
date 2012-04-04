package org.motechproject.tama.refdata.objectcache;

import org.motechproject.tama.common.repository.AbstractCouchRepository;

import java.util.*;

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
        List<T> all = new ArrayList<T>(objectMap.values());
        Collections.sort(all, new Comparator<T>() {
            @Override
            public int compare(T t1, T t2) {
                return compareTo(t1, t2);
            }
        });
        return all;
    }

    protected abstract int compareTo(T t1, T t2);

    private void populateObjectMap() {
         for (T t : getAllFromRepository()) {
             this.objectMap.put(getKey(t), t);
         }
     }

    private List<T> getAllFromRepository() {
        return repository.getAll();
    }
}
