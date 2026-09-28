package org.aitu;

public abstract class StudentTaskView {
    protected final PlatformImplementor platform;

    public StudentTaskView(PlatformImplementor platform) {
        this.platform = platform;
    }

    public abstract void render();
}
