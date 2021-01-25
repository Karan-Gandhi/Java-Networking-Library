package com.karangandhi.networking.core;

import java.util.Objects;

/**
 * This is a class that acts as a lock to avoid weird errors happening when many threads access
 * a single object
 */
public class ObjectLock {
    public boolean isLocked;

    /**
     * Creates a instance of ObjectLock
     */
    public ObjectLock() {
        isLocked = false;
    }

    /**
     * Returns if the object is locked
     *
     * @return      The status if the object is locked
     */
    public boolean isLocked() {
        return isLocked;
    }

    /**
     * Sets the lock status of the object
     *
     * @param locked        The lock status of the object
     */
    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    @Override
    public String toString() {
        return "ObjectLock{" +
                "isLocked=" + isLocked +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectLock that = (ObjectLock) o;
        return isLocked == that.isLocked;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isLocked);
    }
}
