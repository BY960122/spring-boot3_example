package com.example.event;

import org.springframework.context.ApplicationEvent;

public abstract class AbstractGenericEvent<T> extends ApplicationEvent {

    AbstractGenericEvent(T source) {
        super(source);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getSource() {
        return (T)super.getSource();
    }

}