import org.junit.Assert;
import org.junit.Test;
import org.wallentines.midnightlib.config.ConfigSection;
import org.wallentines.midnightlib.config.serialization.ConfigSerializer;
import org.wallentines.midnightlib.config.serialization.PrimitiveSerializers;

import java.util.*;

public class TestSerializer {

    private static class TestClass {

        private final String stringMember;
        private final int intMember;

        public TestClass(String stringMember, int intMember) {
            this.stringMember = stringMember;
            this.intMember = intMember;
        }

        public String getStringMember() {
            return stringMember;
        }

        public int getIntMember() {
            return intMember;
        }

        @Override
        public boolean equals(Object other) {

            if(!(other instanceof TestClass)) return false;
            TestClass test = (TestClass) other;

            return test.stringMember.equals(stringMember) && test.intMember == intMember;
        }
    }

    private static class ComplexTestClass {

        private final String stringMember;
        private final int intMember;

        private final TestClass classMember;

        private final List<TestClass> listMember = new ArrayList<>();

        private final Map<UUID, TestClass> mapMember;

        public ComplexTestClass(String stringMember, int intMember, TestClass classMember, Collection<TestClass> listMember, Map<UUID, TestClass> mapMember) {
            this.stringMember = stringMember;
            this.intMember = intMember;
            this.classMember = classMember;
            this.listMember.addAll(listMember);
            this.mapMember = mapMember;
        }

        public String getStringMember() {
            return stringMember;
        }

        public int getIntMember() {
            return intMember;
        }

        public TestClass getClassMember() {
            return classMember;
        }

        public List<TestClass> getListMember() {
            return listMember;
        }

        public Map<UUID, TestClass> getMapMember() {
            return mapMember;
        }

        @Override
        public boolean equals(Object other) {

            if(!(other instanceof ComplexTestClass)) return false;
            ComplexTestClass test = (ComplexTestClass) other;

            return test.stringMember.equals(stringMember) &&
                    test.intMember == intMember &&
                    classMember.equals(test.classMember) &&
                    compareLists(listMember, test.listMember) &&
                    compareMaps(mapMember, test.mapMember);
        }
    }

    private static boolean compareLists(List<?> l1, List<?> l2) {

        if(l1.size() != l2.size()) return false;
        for(int i = 0 ; i < l1.size() ; i++) {
            if(!l1.get(i).equals(l2.get(i))) return false;
        }
        return true;
    }

    private static <K,V> boolean compareMaps(Map<K, V> m1, Map<K, V> m2) {

        if(m1.size() != m2.size()) return false;
        for(K k : m1.keySet()) {
            if(!m2.containsKey(k) || !m1.get(k).equals(m2.get(k))) return false;
        }
        return true;
    }

    @Test
    public void testObjectConfig() {

        // Basic Object
        ConfigSerializer<TestClass> config =
                ConfigSerializer.create(
                        PrimitiveSerializers.STRING.entry("str", TestClass::getStringMember),
                        PrimitiveSerializers.INT.entry("int", TestClass::getIntMember),
                        TestClass::new);


        TestClass test = new TestClass("hello", 12);
        ConfigSection sec = config.serialize(test);

        ConfigSection conf = new ConfigSection().with("str", "hello").with("int", 12);
        TestClass test2 = config.deserialize(conf);
        TestClass test3 = config.deserialize(sec);

        Assert.assertEquals(test, test2);
        Assert.assertEquals(test, test3);


        // Complex Object
        ConfigSerializer<ComplexTestClass> complexSerializer = ConfigSerializer.create(
                PrimitiveSerializers.STRING.entry("str", ComplexTestClass::getStringMember),
                PrimitiveSerializers.INT.entry( "int", ComplexTestClass::getIntMember),
                config.entry("class", ComplexTestClass::getClassMember),
                config.listOf().entry("list", ComplexTestClass::getListMember),
                config.mapOf(PrimitiveSerializers.UUID).entry("map", ComplexTestClass::getMapMember),
                ComplexTestClass::new
        );

        ComplexTestClass ctest = new ComplexTestClass(
                "world",
                21,
                test,
                List.of(test, test2, test3),
                Map.of(UUID.nameUUIDFromBytes("test".getBytes()), test, UUID.nameUUIDFromBytes("test2".getBytes()), test2));

        ConfigSection serialized = complexSerializer.serialize(ctest);
        ComplexTestClass deserialized = complexSerializer.deserialize(serialized);

        Assert.assertEquals(ctest, deserialized);
    }

}
