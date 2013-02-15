package org.motechproject.tama.migration.repository;

import java.util.List;

public interface Paged<Type> {

    public List<Type> get(int skip, int limit);
}
