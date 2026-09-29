package org.aitu;

public interface PlatformCreator {
    PlatformImplementor create(String rawData);
}