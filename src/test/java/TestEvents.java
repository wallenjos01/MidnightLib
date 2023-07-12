import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wallentines.midnightlib.event.Event;
import org.wallentines.midnightlib.event.HandlerList;
import org.wallentines.midnightlib.event.SingletonHandlerList;

import java.util.concurrent.atomic.AtomicInteger;

public class TestEvents {

    private static class TestEvent {

        String value;

        public TestEvent(String value) {
            this.value = value;
        }
    }

    @Test
    public void testHandlerList() {

        HandlerList<TestEvent> handlers = new HandlerList<>();

        TestEvent event = new TestEvent("orig");
        handlers.invoke(event);

        Assertions.assertEquals("orig", event.value);

        handlers.register(this, ev -> ev.value = "modified");

        handlers.invoke(event);
        Assertions.assertEquals("modified", event.value);

        handlers.unregisterAll(this);
        event = new TestEvent("orig");
        handlers.invoke(event);

        Assertions.assertEquals("orig", event.value);

    }

    @Test
    public void testGlobalEvents() {

        Event.unregisterAll(this);

        TestEvent event = new TestEvent("orig");
        Event.invoke(event);
        Assertions.assertEquals("orig", event.value);


        Event.register(TestEvent.class, this, ev -> ev.value = "modified");
        Event.invoke(event);
        Assertions.assertEquals("modified", event.value);

        Event.unregisterAll(this);
        event = new TestEvent("orig");
        Event.invoke(event);
        Assertions.assertEquals("orig", event.value);

        AtomicInteger handled = new AtomicInteger();

        Event.register(TestEvent.class, this, ev -> {
            ev.value = "modified";
            handled.getAndIncrement();
        });
        Event.register(TestEvent.class, this, ev -> handled.getAndIncrement());

        Event.invoke(event);
        Assertions.assertEquals("modified", event.value);
        Assertions.assertEquals(2, handled.get()); // Ensure all registered handlers are triggered

        Event.unregisterAll(TestEvent.class);
        event = new TestEvent("orig");
        Event.invoke(event);
        Assertions.assertEquals("orig", event.value);

    }

    @Test
    public void testPriority() {

        HandlerList<TestEvent> handlers = new HandlerList<>();

        TestEvent event = new TestEvent("orig");
        handlers.invoke(event);
        Assertions.assertEquals("orig", event.value);

        handlers.register(this, 2, ev -> ev.value = "default");
        handlers.register(this, 3, ev -> ev.value = "first");
        handlers.register(this, 3, ev -> ev.value = "priority");
        handlers.register(this, 1, ev -> ev.value = "modified");

        handlers.invoke(event);
        Assertions.assertEquals("first", event.value);

    }

    @Test
    public void testSingleton() {

        SingletonHandlerList<TestEvent> handlers = new SingletonHandlerList<>();

        TestEvent event = new TestEvent("value");

        handlers.register(this, ev -> {
            Assertions.assertEquals("value", ev.value);
            ev.value = "changed";
        });

        handlers.invoke(event);

        handlers.register(this, ev -> {
            Assertions.assertEquals("changed", ev.value);
            ev.value = "completed";
        });

        Assertions.assertEquals("completed", event.value);

        // Reset
        event = new TestEvent("value");

        handlers.reset();
        handlers.unregisterAll();

        handlers.register(this, ev -> {
            Assertions.assertEquals("value", ev.value);
            ev.value = "reset";
        });

        Assertions.assertEquals("value", event.value);
        handlers.invoke(event);
        Assertions.assertEquals("reset", event.value);

    }

}
