package de.lebk.thirtyone.client;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ObservedProperty<T>
{
    private T value;
    private List<ChangeListener<? super T>> listener;

    public ObservedProperty(@NotNull final T initialValue)
    {
        value = Objects.requireNonNull(initialValue);
        listener = new ArrayList<>();
    }

    public void addListener(ChangeListener<? super T> changeListener)
    {
        listener.add(changeListener);
    }

    public void change(T newValue)
    {
        Objects.requireNonNull(newValue);
        listener.forEach(listener -> listener.changed(value, newValue));

        value = newValue;
    }

    public T getValue()
    {
        return value;
    }
}
