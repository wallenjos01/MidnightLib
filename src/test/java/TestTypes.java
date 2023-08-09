import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wallentines.midnightlib.types.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    @Test
    public void testEither() {

        Either<String, Number> either = Either.left("String");
        Either<String, Number> either2 = Either.right(42);

        Assertions.assertNull(either.right());
        Assertions.assertNull(either2.left());

        Assertions.assertEquals("String", either.left());
        Assertions.assertEquals(42, either2.right());

        Assertions.assertEquals(33, either.rightOr(33));
        Assertions.assertEquals(6, either.rightOrGet(String::length));

        Assertions.assertEquals("Test", either2.leftOr("Test"));
        Assertions.assertEquals("42", either2.leftOrGet(Object::toString));

        boolean threw = false;
        try {
            either.rightOrThrow();
        } catch (IllegalStateException ex) {
            threw = true;
        }

        Assertions.assertTrue(threw);
        threw = false;
        try {
            Assertions.assertEquals("String", either.leftOrThrow());
        } catch (IllegalStateException ex) {
            threw = true;
        }

        Assertions.assertFalse(threw);
        try {
            either2.leftOrThrow();
        } catch (IllegalStateException ex) {
            threw = true;
        }

        Assertions.assertTrue(threw);
        threw = false;
        try {
            Assertions.assertEquals(42, either2.rightOrThrow());
        } catch (IllegalStateException ex) {
            threw = true;
        }

        Assertions.assertFalse(threw);
    }

    @Test
    public void testRandomizedQueue() {

        List<String> objects = List.of("String1", "String2", "String3");

        RandomizedQueue<String> queue = new RandomizedQueue<>();
        queue.addAll(objects);

        Assertions.assertEquals(3, queue.size());

        ArrayList<String> output = new ArrayList<>();
        while(!queue.isEmpty()) {
            output.add(queue.remove());
        }

        Assertions.assertEquals(3, output.size());
        Assertions.assertTrue(output.contains("String1"));
        Assertions.assertTrue(output.contains("String2"));
        Assertions.assertTrue(output.contains("String3"));
    }

    @Test
    public void testRandomizedLoopingQueue() {

        RandomizedQueue<String> queue = new RandomizedLoopingQueue<>(List.of("String1", "String2", "String3"));

        Assertions.assertEquals(3, queue.size());

        ArrayList<String> output = new ArrayList<>();
        Set<String> outputSet = new HashSet<>();
        int count = 0;

        while(!queue.isEmpty()) {
            String add = queue.remove();
            output.add(add);
            outputSet.add(add);
            if(++count == 10) break;
        }

        Assertions.assertEquals(10, count);

        Assertions.assertEquals(10, output.size());
        Assertions.assertTrue(output.contains("String1"));
        Assertions.assertTrue(output.contains("String2"));
        Assertions.assertTrue(output.contains("String3"));


        Assertions.assertEquals(3, outputSet.size());
        Assertions.assertTrue(outputSet.contains("String1"));
        Assertions.assertTrue(outputSet.contains("String2"));
        Assertions.assertTrue(outputSet.contains("String3"));
    }

}
