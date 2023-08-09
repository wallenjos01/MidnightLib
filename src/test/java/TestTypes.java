import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wallentines.midnightlib.types.DefaultedSingleton;
import org.wallentines.midnightlib.types.ResettableSingleton;
import org.wallentines.midnightlib.types.Singleton;
import org.wallentines.midnightlib.types.SortedCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestTypes {

    @Test
    public void testSortedCollection() {

        SortedCollection<Integer> ints = new SortedCollection<>();
        ints.add(10);
        ints.add(50);
        ints.add(0);

        List<Integer> out = new ArrayList<>(ints);
        Assertions.assertEquals(0, out.get(0));
        Assertions.assertEquals(10, out.get(1));
        Assertions.assertEquals(50, out.get(2));

    }

    @Test
    public void testSingleton() {

        Singleton<String> singleton = new Singleton<>();

        boolean threw = false;

        try {
            singleton.get();
        } catch (IllegalStateException ex) {
            threw = true;
        }

        Assertions.assertTrue(threw);

        Assertions.assertNull(singleton.getOrNull());
        Assertions.assertEquals("World", singleton.getOr("World"));

        threw = false;
        singleton.set("Hello");

        Assertions.assertEquals("Hello", singleton.get());
        try {
            singleton.set("World");
        } catch (IllegalStateException ex) {
            threw = true;
        }

        Assertions.assertTrue(threw);
    }

    @Test
    public void testDefaultedSingleton() {

        DefaultedSingleton<String> singleton = new DefaultedSingleton<>("Test");

        Assertions.assertEquals("Test", singleton.getDefaultValue());

        boolean threw = false;

        try {
            singleton.get();
        } catch (IllegalStateException ex) {
            threw = true;
        }

        Assertions.assertFalse(threw);

        Assertions.assertNotNull(singleton.getOrNull());
        Assertions.assertEquals("Test", singleton.get());
        Assertions.assertEquals("Test", singleton.getOr("World"));

        singleton.set("Hello");

        Assertions.assertEquals("Hello", singleton.get());
        try {
            singleton.set("World");
        } catch (IllegalStateException ex) {
            threw = true;
        }

        Assertions.assertTrue(threw);

    }

    @Test
    public void testResettableSingleton() {

        ResettableSingleton<String> singleton = new ResettableSingleton<>();

        boolean threw = false;
        final AtomicBoolean reset = new AtomicBoolean(false);

        singleton.resetEvent.register(this, ev -> reset.set(true));

        try {
            singleton.get();
        } catch (IllegalStateException ex) {
            threw = true;
        }

        Assertions.assertTrue(threw);
        threw = false;

        Assertions.assertNull(singleton.getOrNull());
        Assertions.assertEquals("World", singleton.getOr("World"));

        singleton.set("Hello");

        Assertions.assertEquals("Hello", singleton.get());
        singleton.reset();

        Assertions.assertTrue(reset.get());

        try {
            singleton.set("World");
        } catch (IllegalStateException ex) {
            threw = true;
        }

        Assertions.assertFalse(threw);
        Assertions.assertEquals("World", singleton.get());

    }

}
