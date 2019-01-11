package de.lebk.thirtyone.client;

public interface ChangeListener<T>
{
    void changed(T oldValue, T newValue);
}
