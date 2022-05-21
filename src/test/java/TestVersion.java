import org.junit.Assert;
import org.junit.Test;
import org.wallentines.midnightlib.Version;

public class TestVersion {

    @Test
    public void testVersion() {

        Version version = Version.SERIALIZER.deserialize("1.0.0-bruh.0+gamer");

        Assert.assertEquals(version.getMajorVersion(), 1);
        Assert.assertEquals(version.getMinorVersion(), 0);
        Assert.assertEquals(version.getPatchVersion(), 0);
        Assert.assertEquals(version.getPreReleaseData().length, 2);
        Assert.assertEquals(version.getBuildMetadata().length, 1);
        Assert.assertEquals(version.getPreReleaseData()[0], "bruh");
        Assert.assertEquals(version.getPreReleaseData()[1], "0");
        Assert.assertEquals(version.getBuildMetadata()[0], "gamer");

        Version ver1 = Version.SERIALIZER.deserialize("1.0");
        Assert.assertEquals(0, ver1.getPatchVersion());

        Version ver0 = Version.SERIALIZER.deserialize("1");;
        Assert.assertEquals(0, ver0.getMinorVersion());
        Assert.assertEquals(0, ver0.getPatchVersion());

    }

}
