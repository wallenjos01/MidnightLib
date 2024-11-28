import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wallentines.midnightlib.event.Event;
import org.wallentines.midnightlib.event.HandlerList;
import org.wallentines.midnightlib.event.SingletonHandlerList;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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


    @Test
    public void testConcurrentInvoke() {

        HandlerList<TestEvent> handlers = new HandlerList<>();
        AtomicInteger handled = new AtomicInteger();
        handlers.register(this, ev -> handled.getAndIncrement());

        ThreadPoolExecutor exe = new ThreadPoolExecutor(8, 100, 5000L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100));
        for(int i = 0; i < 100 ; i++) {
            final int index = i;
            exe.submit(() -> handlers.invoke(new TestEvent(String.valueOf(index))));
        }
        try {
            Thread.sleep(5000L);
            exe.shutdown();
        } catch (Exception ex) {
            Assertions.fail(ex);
        }

        Assertions.assertEquals(100, handled.get());
    }

    @Test
    public void testConcurrentRegister() {

        HandlerList<TestEvent> handlers = new HandlerList<>();
        AtomicInteger handled = new AtomicInteger();

        ThreadPoolExecutor exe = new ThreadPoolExecutor(8, 100, 5000L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100));
        for(int i = 0; i < 100 ; i++) {
            final int index = i;
            exe.submit(() -> {
                TestEvent event = new TestEvent(String.valueOf(index));
                handlers.register(event, ev -> {
                    handled.getAndIncrement();
                    handlers.unregisterAll(event);
                });
            });
        }
        try {
            Thread.sleep(5000L);
            exe.shutdown();
        } catch (Exception ex) {
            Assertions.fail(ex);
        }

        handlers.invoke(new TestEvent(String.valueOf(-1)));

        Assertions.assertEquals(100, handled.get());
    }
}
