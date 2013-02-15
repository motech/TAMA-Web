package org.motechproject.tama.migration;

import org.motechproject.tama.migration.repository.Paged;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public abstract class Migration<Type> {

    private Paged<Type> allDocuments;
    protected int limit = 100;

    public Migration(Paged<Type> allDocuments) {
        this.allDocuments = allDocuments;
    }

    public void migrate() {
        List<Type> documents;
        int skip = 0;
        do {
            documents = allDocuments.get(skip, limit);
            for (Type document : documents) {
                save(document);
            }
            skip += documents.size();
        } while (isNotEmpty(documents));
    }

    protected abstract void save(Type document);
}
