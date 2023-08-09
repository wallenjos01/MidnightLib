package org.wallentines.midnightlib;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wallentines.mdcfg.serializer.InlineSerializer;
import org.wallentines.mdcfg.serializer.Serializer;

import java.util.Objects;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class which represents a semantic Version
 */
public class Version implements Comparable<Version> {


    private static final Pattern NO_PNTS = Pattern.compile("^(0|[1-9]\\d*)");
    private static final Pattern ONE_PNT = Pattern.compile("^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)");

    // From https://semver.org/
    private static final Pattern SEM_VER = Pattern.compile("^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$");


    private final int major;
    private final int minor;
    private final int patch;
    private final String preRelease;
    private final String buildMetadata;

    private Version(int major, int minor, int patch, @Nullable String preRelease, @Nullable String buildMetadata) throws IllegalArgumentException {

        if(major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException("Version components cannot be negative!");
        }

        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.preRelease = preRelease;
        this.buildMetadata = buildMetadata;
    }

    /**
     * Constructs a new version with the given components
     * @param major The major version
     * @param minor The minor version
     * @param patch The patch version
     * @throws IllegalArgumentException If any of the components are negative
     */
    public Version(int major, int minor, int patch) throws IllegalArgumentException {
        this(major, minor, patch, null, null);
    }

    /**
     * Gets the major version component
     * @return The major version
     */
    public int getMajorVersion() {
        return major;
    }

    /**
     * Gets the minor version component
     * @return The minor version
     */
    public int getMinorVersion() {
        return minor;
    }

    /**
     * Gets the patch version component
     * @return The patch version
     */
    public int getPatchVersion() {
        return patch;
    }

    /**
     * Gets the pre-release information
     * @return The pre-release info
     */
    @Nullable
    public String getPreReleaseData() {
        return preRelease;
    }

    /**
     * Gets the build metadata
     * @return The build meta
     */
    @Nullable
    public String getBuildMetadata() {
        return buildMetadata;
    }

    /**
     * Determines if this version is newer than another version
     * @param other The version to compare
     * @return Whether this version is newer
     */
    public boolean isGreater(Version other) {

        return compareTo(other) > 0;
    }

    /**
     * Determines if this version is newer or the same as another version
     * @param other The version to compare
     * @return Whether this version is newer or the same
     */
    public boolean isGreaterOrEqual(Version other) {

        return compareTo(other) >= 0;
    }

    @Override
    public int compareTo(@NotNull Version other) {

        // Compare basic parts
        int comp = Integer.compare(major, other.major);
        if(comp != 0) return comp;

        comp = Integer.compare(minor, other.minor);
        if(comp != 0) return comp;

        comp = Integer.compare(patch, other.patch);
        if(comp != 0) return comp;

        // Compare pre-release info
        if(preRelease != null || other.preRelease != null) {

            if(preRelease == null) {
                return 1;
            }
            if(other.preRelease == null) {
                return -1;
            }

            StringTokenizer first = new StringTokenizer(preRelease, ".");
            StringTokenizer second = new StringTokenizer(other.preRelease, ".");

            while(first.hasMoreElements()) {

                if(!second.hasMoreElements()) {
                    return 1;
                }

                String firstToken = first.nextToken();
                String secondToken = second.nextToken();

                boolean firstIsInt = NO_PNTS.matcher(firstToken).matches();
                boolean secondIsInt = NO_PNTS.matcher(secondToken).matches();
                if(firstIsInt || secondIsInt) {

                    if(!firstIsInt) {
                        return 1;
                    }
                    if(!secondIsInt) {
                        return -1;
                    }

                    comp = Integer.compare(Integer.parseInt(firstToken), Integer.parseInt(secondToken));
                    if(comp != 0) return comp;
                }

                comp = firstToken.compareTo(secondToken);
                if(comp != 0) return comp;
            }
        }

        return 0;
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
        return major == version.major &&
                minor == version.minor &&
                patch == version.patch &&
                Objects.equals(preRelease, version.preRelease) &&
                Objects.equals(buildMetadata, version.buildMetadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch, preRelease, buildMetadata);
    }

    /**
     * Parses a Version from a String
     * @param string The string to parse
     * @return A version, or null if the String is in the wrong format
     */
    @Nullable
    public static Version fromString(String string) {

        Matcher matcher = SEM_VER.matcher(string);
        if(!matcher.find()) {
            Matcher onePnt = ONE_PNT.matcher(string);
            if(!onePnt.find()) {
                Matcher noPnts = NO_PNTS.matcher(string);
                if(!noPnts.find()) {
                    return null;
                }
                return new Version(Integer.parseInt(string), 0, 0, null, null);
            }
            return new Version(Integer.parseInt(onePnt.group(1)), Integer.parseInt(onePnt.group(2)), 0, null, null);
        }

        int major = Integer.parseInt(matcher.group(1));
        int minor = Integer.parseInt(matcher.group(2));
        int patch = Integer.parseInt(matcher.group(3));

        String pr = matcher.group(4);
        String bm = matcher.group(5);

        return new Version(major, minor, patch, pr, bm);
    }
    public static final Serializer<Version> SERIALIZER = InlineSerializer.of(Version::toString, Version::fromString);

}

