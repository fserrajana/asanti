/*
 * Created by brightSPARK Labs
 * www.brightsparklabs.com
 */

package com.brightsparklabs.asanti.decoder.builtin;

import com.brightsparklabs.asanti.common.DecodeException;
import com.brightsparklabs.asanti.model.schema.AsnBuiltinType;
import com.brightsparklabs.asanti.validator.ValidationResult;
import com.brightsparklabs.asanti.validator.bytes.AsnByteValidator;

/**
 * Decoder for data of type {@link AsnBuiltinType#Boolean}
 *
 * @author brightSPARK Labs
 */
public class BooleanDecoder extends AbstractBuiltinTypeDecoder<Boolean>
{
    // -------------------------------------------------------------------------
    // INSTANCE VARIABLES
    // -------------------------------------------------------------------------

    /** singleton instance */
    private static BooleanDecoder instance;

    // -------------------------------------------------------------------------
    // CONSTRUCTION
    // -------------------------------------------------------------------------

    /**
     * Default constructor.
     *
     * <p>This is private, use {@link #getInstance()} to obtain an instance</p>
     */
    private BooleanDecoder() {}

    /**
     * Returns a singleton instance of this class
     *
     * @return a singleton instance of this class
     */
    public static BooleanDecoder getInstance()
    {
        if (instance == null)
        {
            instance = new BooleanDecoder();
        }
        return instance;
    }

    // -------------------------------------------------------------------------
    // IMPLEMENTATION: AbstractBuiltinTypeDecoder
    // -------------------------------------------------------------------------

    @Override
    public Boolean decode(final byte[] bytes) throws DecodeException
    {
        final ValidationResult validationResult = AsnByteValidator.validateAsBoolean(bytes);
        DecodeException.throwIfHasFailures(validationResult);
        return bytes[0] != 0;
    }
}