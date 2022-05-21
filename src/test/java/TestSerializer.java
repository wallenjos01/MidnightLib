import org.junit.Assert;
import org.junit.Test;
import org.wallentines.midnightlib.config.ConfigSection;
import org.wallentines.midnightlib.config.serialization.ConfigSerializer;

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

    @Test
    public void testObjectConfig() {

        ConfigSerializer<TestClass> config =
                ConfigSerializer.create(
                        ConfigSerializer.entry(String.class,"str", TestClass::getStringMember),
                        ConfigSerializer.entry(Integer.class,"int", TestClass::getIntMember),
                        TestClass::new);


        TestClass test = new TestClass("hello", 12);
        ConfigSection sec = config.serialize(test);

        ConfigSection conf = new ConfigSection().with("str", "hello").with("int", 12);
        TestClass test2 = config.deserialize(conf);
        TestClass test3 = config.deserialize(sec);

        Assert.assertEquals(test, test2);
        Assert.assertEquals(test, test3);

    }

}
