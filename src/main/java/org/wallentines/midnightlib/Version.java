package org.wallentines.midnightlib;

import org.wallentines.mdcfg.serializer.InlineSerializer;
import org.wallentines.mdcfg.serializer.Serializer;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version {

    private final int major;
    private final int minor;
    private final int patch;
    private final String[] preRelease;
    private final String[] buildMetadata;

    private Version(int major, int minor, int patch, String[] preRelease, String[] buildMetadata) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.preRelease = preRelease;
        this.buildMetadata = buildMetadata;
    }

    public Version(int major, int minor, int patch) {
        this(major, minor, patch, null, null);
    }

    public int getMajorVersion() {
        return major;
    }

    public int getMinorVersion() {
        return minor;
    }

    public int getPatchVersion() {
        return patch;
    }

    public String[] getPreReleaseData() {
        return preRelease;
    }

    public String[] getBuildMetadata() {
        return buildMetadata;
    }

    public boolean isGreater(Version other) {

        return major > other.major ||
                major == other.major && minor > other.minor ||
                major == other.major && minor == other.minor && patch > other.patch;

    }

    public boolean isGreaterOrEqual(Version other) {

        return major > other.major ||
                major == other.major && minor > other.minor ||
                major == other.major && minor == other.minor && patch > other.patch ||
                major == other.major && minor == other.minor && patch == other.patch;

    }

    @Override
    public String toString() {

        StringBuilder out = new StringBuilder();
        out.append(major).append(".").append(minor).append(".").append(patch);

        if(preRelease != null) {
            out.append("-").append(String.join(".", preRelease));
        }
        if(buildMetadata != null) {
            out.append("+").append(String.join(".", buildMetadata));
        }

        return out.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Version version = (Version) o;
        return major == version.major && minor == version.minor && patch == version.patch && Arrays.equals(preRelease, version.preRelease) && Arrays.equals(buildMetadata, version.buildMetadata);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(major, minor, patch);
        result = 31 * result + Arrays.hashCode(preRelease);
        result = 31 * result + Arrays.hashCode(buildMetadata);
        return result;
    }

    private static final Pattern NO_PNTS = Pattern.compile("^(0|[1-9]\\d*)");
    private static final Pattern ONE_PNT = Pattern.compile("^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)");
    private static final Pattern SEM_VER = Pattern.compile("^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$");

    public static Version fromString(String s) {

        Matcher matcher = SEM_VER.matcher(s);
        if(!matcher.find()) {
            Matcher onePnt = ONE_PNT.matcher(s);
            if(!onePnt.find()) {
                Matcher noPnts = NO_PNTS.matcher(s);
                if(!noPnts.find()) {
                    return null;
                }
                return new Version(Integer.parseInt(s), 0, 0, null, null);
            }
            return new Version(Integer.parseInt(onePnt.group(1)), Integer.parseInt(onePnt.group(2)), 0, null, null);
        }

        int major = Integer.parseInt(matcher.group(1));
        int minor = Integer.parseInt(matcher.group(2));
        int patch = Integer.parseInt(matcher.group(3));

        String[] preRelease = null;
        String[] buildMetadata = null;

        String pr = matcher.group(4);
        String bm = matcher.group(5);
        if(pr != null) preRelease = pr.split("\\.");
        if(bm != null) buildMetadata = bm.split("\\.");

        return new Version(major, minor, patch, preRelease, buildMetadata);
    }
    public static final Serializer<Version> SERIALIZER = InlineSerializer.of(Version::toString, Version::fromString);
}

