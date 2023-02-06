import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wallentines.midnightlib.Version;

public class TestVersion {

    @Test
    public void testVersion() {

        Version version = Version.SERIALIZER.deserialize("1.0.0-bruh.0+gamer");

        Assertions.assertEquals(version.getMajorVersion(), 1);
        Assertions.assertEquals(version.getMinorVersion(), 0);
        Assertions.assertEquals(version.getPatchVersion(), 0);
        Assertions.assertEquals(version.getPreReleaseData().length, 2);
        Assertions.assertEquals(version.getBuildMetadata().length, 1);
        Assertions.assertEquals(version.getPreReleaseData()[0], "bruh");
        Assertions.assertEquals(version.getPreReleaseData()[1], "0");
        Assertions.assertEquals(version.getBuildMetadata()[0], "gamer");

        Version ver1 = Version.SERIALIZER.deserialize("1.0");
        Assertions.assertEquals(0, ver1.getPatchVersion());

        Version ver0 = Version.SERIALIZER.deserialize("1");
        Assertions.assertEquals(0, ver0.getMinorVersion());
        Assertions.assertEquals(0, ver0.getPatchVersion());

    }

}
