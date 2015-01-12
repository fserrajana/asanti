/*
 * Created by brightSPARK Labs
 * www.brightsparklabs.com
 */

package com.brightsparklabs.asanti.model.schema;

/**
 * Interface for modeling an ASN.1 schema
 *
 * @author brightSPARK Labs
 */
public interface AsnSchema
{

    // -------------------------------------------------------------------------
    // PUBLIC METHODS
    // -------------------------------------------------------------------------

    /**
     * Returns the decoded tag for the supplied raw tag. E.g.
     * {@code getDecodedTag("/1/0/1", "Document")} =>
     * {@code "/Document/header/published/date"}
     *
     * @param rawTag
     *            raw tag to decode
     *
     * @param topLevelTypeName
     *            the name of the top level type in this module from which to
     *            begin decoding the raw tag
     *
     * @return the result of the decode attempt containing the decoded tag
     */
    public DecodeResult<String> getDecodedTag(String rawTag, String topLevelTypeName);

    /**
     * Returns the raw tag for the supplied decoded tag. E.g.
     * {@code getRawTag("/Document/header/published/date")} => {@code "/1/0/1"}
     *
     * @param decodedTag
     *            decoded tag to map back to raw tag
     *
     * @return the result of the decode attempt containing the raw tag
     */
    public DecodeResult<String> getRawTag(String decodedTag);

    /**
     * Gets the data (bytes) associated with the specified tag as a printable
     * string
     *
     * @param tag
     *            tag associated with the data
     *
     * @param data
     *            data associated with the tag
     *
     * @return data associated with the specified tag or an empty string if the
     *         tag does not exist or parameters are {@code null}
     */
    public String getPrintableString(String tag, byte[] data);

    /**
     * Gets the data (bytes) associated with the specified tag as the decoded
     * Java object most appropriate to its type
     *
     * @param tag
     *            tag associated with the data
     *
     * @param data
     *            data associated with the tag
     *
     * @return data associated with the specified tag or an empty byte array if
     *         the tag does not exist or parameters are {@code null}
     */
    public Object getDecodedObject(String tag, byte[] data);
}
