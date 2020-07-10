package be.dezijwegel.bettersleeping.util;

import org.jetbrains.annotations.NotNull;

public class Version implements Comparable<Version>{


    private final int prefix;
    private final int major;
    private final int minor;


    /**
     * Creates the Version where x.y.z matches prefix.major.minor
     * @param prefix Prefix (major major version)
     * @param major Major version number
     * @param minor Minor version number
     */
    public Version(int prefix, int major, int minor)
    {
        this.prefix = prefix;
        this.major = major;
        this.minor = minor;
    }


    /**
     * Create a version from version string 'x.y.z'
     * Will default to 1.0.0 if the format is incorrect!
     * @param versionString Must be in the format 'x.y.z'
     */
    public Version(String versionString)
    {
        String[] subs = versionString.split("/.");
        if (subs.length != 3)
        {
            prefix = 1;
            major = 0;
            minor = 0;
        }
        else
        {
            prefix= Integer.parseInt( subs[0] );
            major = Integer.parseInt( subs[1] );
            minor = Integer.parseInt( subs[2] );
        }
    }


    /**
     * Get a component of this version
     * @return x from version x.y.z
     */
    public int getPrefix()
    {
        return prefix;
    }


    /**
     * Get a component of this version
     * @return y from version x.y.z
     */
    public int getMajor()
    {
        return major;
    }


    /**
     * Get a component of this version
     * @return z from version x.y.z
     */
    public int getMinor()
    {
        return minor;
    }


    @Override
    public int compareTo(@NotNull Version version)
    {
        if (this.prefix > version.prefix)
            return 1;
        else if (this.prefix == version.prefix && this.major > version.major)
            return 1;
        else if (this.prefix == version.prefix && this.major == version.major && this.minor > version.minor)
            return 1;
        else
            return this.prefix==version.prefix && this.major==version.major && this.minor==version.minor ? 0 : -1;
    }

}