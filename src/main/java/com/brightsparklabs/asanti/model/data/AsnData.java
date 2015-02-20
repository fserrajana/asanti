/*
 * Created by brightSPARK Labs
 * www.brightsparklabs.com
 */

package com.brightsparklabs.asanti.model.data;

import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Interface for modeling raw data (bytes) read from an ASN.1 binary file
 *
 * @author brightSPARK Labs
 */
public interface AsnData
{
    // -------------------------------------------------------------------------
    // PUBLIC METHODS
    // -------------------------------------------------------------------------

    /**
     * Determines whether the specified tag is contained in the data
     *
     * @param tag
     *            tag to check
     *
     * @return {@code true} if the tag is in the data; {@code false} otherwise
     */
    public boolean contains(String tag);

    /**
     * Determines whether the data contains any tags matching the supplied
     * regular expression
     *
     * @param tag
     *            tag to check
     *
     * @return {@code true} if the tag is in the data; {@code false} otherwise
     */
    public boolean contains(Pattern regex);

    /**
     * Returns all tags found in the ASN data as a set of XPath like strings.
     * <p>
     * E.g. "/1/1/1", "3/1/2", "/2/4/1"
     *
     * @return all tags in the data
     */
    public ImmutableSet<String> getRawTags();

    /**
     * Returns the data (bytes) associated with the specified tag
     *
     * @param rawTag
     *            tag associated with the data
     *
     * @return data associated with the specified tag or an empty byte array if
     *         the tag does not exist
     */
    public byte[] getBytes(String rawTag);

    /**
     * Returns a mapping of all tags to the data (bytes) associated with them
     * <p>
     * E.g. "/1/1/1" => "[0x00, 0x01]", "3/1/2" => "[0x00, 0x01]"
     *
     * @return mapping of all tags to their associated data
     */
    public ImmutableMap<String, byte[]> getBytes();

    /**
     * Returns the data (bytes) from all tags matching the supplied regular
     * expression
     * <p>
     * E.g.
     * <code>getBytesMatching(Pattern.compile("/1/1/.*")) => { "/1/1/1" => "[0x00, 0x01]",
     * "/1/1/2" => "[0x00, 0x01]" } </code>
     *
     * @param regex
     *            regular expression to match tags against
     *
     * @return data associated with the matching tags. Map is of form:
     *         {@code tag => data}
     */
    public ImmutableMap<String, byte[]> getBytesMatching(Pattern regex);
}
