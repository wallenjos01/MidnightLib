package org.wallentines.midnightlib.requirement;

public interface RequirementType<T> {

    boolean check(T data, Requirement<T> req, String value);



}
