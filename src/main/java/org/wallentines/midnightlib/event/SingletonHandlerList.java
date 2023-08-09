package org.wallentines.midnightlib.event;

/**
 * A handler list which will retain its event after it is invoked. All subsequent calls to
 * {@link HandlerList#register(Object, EventHandler) register} will result in the handler being called immediately
 * @param <T> The type of event to handle
 */
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

    /**
     * Clears the stored event
     */
    public void reset() {
        completed = null;
    }
}
