import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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

        Assertions.assertTrue(ver1.isGreater(version));

    }

}
