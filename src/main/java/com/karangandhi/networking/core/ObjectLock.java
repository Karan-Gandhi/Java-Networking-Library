package com.karangandhi.networking.core;

import java.util.Objects;

public class ObjectLock {
    public boolean isLocked;

    public ObjectLock() {
        isLocked = false;
    }

    public boolean isLocked() {
        return isLocked;
    }

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
