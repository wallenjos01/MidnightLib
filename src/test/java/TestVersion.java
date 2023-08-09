import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wallentines.mdcfg.ConfigObject;
import org.wallentines.mdcfg.ConfigPrimitive;
import org.wallentines.mdcfg.serializer.ConfigContext;
import org.wallentines.mdcfg.serializer.SerializeResult;
import org.wallentines.midnightlib.Version;

public class TestVersion {

    @Test
    public void testVersion() {

        Version version = Version.fromString("1.0.0-bruh.0+gamer");

        Assertions.assertNotNull(version);
        Assertions.assertEquals(1, version.getMajorVersion());
        Assertions.assertEquals(0, version.getMinorVersion());
        Assertions.assertEquals(0, version.getPatchVersion());
        Assertions.assertEquals("bruh.0", version.getPreReleaseData());
        Assertions.assertEquals("gamer", version.getBuildMetadata());

        Version ver1 = Version.fromString("1.0");
        Assertions.assertNotNull(ver1);
        Assertions.assertEquals(0, ver1.getPatchVersion());

        Version ver0 = Version.fromString("1");
        Assertions.assertNotNull(ver0);
        Assertions.assertEquals(0, ver0.getMinorVersion());
        Assertions.assertEquals(0, ver0.getPatchVersion());

        Assertions.assertEquals(ver0, ver1);
        Assertions.assertFalse(ver1.isGreater(ver0));
        Assertions.assertTrue(ver1.isGreaterOrEqual(ver0));

        Assertions.assertTrue(ver1.isGreater(version));
        Assertions.assertTrue(ver1.isGreaterOrEqual(version));

        Version ver2 = new Version(2,0,0);
        Assertions.assertTrue(ver2.isGreater(version));
        Assertions.assertTrue(ver2.isGreaterOrEqual(version));


        ConfigObject unparsed = new ConfigPrimitive("1.2.0-beta1");
        SerializeResult<Version> parsed = Version.SERIALIZER.deserialize(ConfigContext.INSTANCE, unparsed);

        Assertions.assertTrue(parsed.isComplete());

        Version parsedVersion = parsed.getOrThrow();

        Assertions.assertEquals(1, parsedVersion.getMajorVersion());
        Assertions.assertEquals(2, parsedVersion.getMinorVersion());
        Assertions.assertEquals(0, parsedVersion.getPatchVersion());

        Assertions.assertEquals("beta1", parsedVersion.getPreReleaseData());

    }

}
