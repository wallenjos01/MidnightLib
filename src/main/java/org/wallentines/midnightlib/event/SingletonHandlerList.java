package org.wallentines.midnightlib.event;

public class SingletonHandlerList<T> extends HandlerList<T> {

    private T completed = null;

    @Override
    public void invoke(T event) {
        super.invoke(event);
        completed = event;
    }

    @Override
    public void register(Object listener, int priority, EventHandler<T> handler) {
        super.register(listener, priority, handler);
        if(completed != null) {
            handle(handler, completed);
        }
    }

    public void reset() {
        completed = null;
    }
}
