/*
 * Created by brightSPARK Labs
 * www.brightsparklabs.com
 */

package com.brightsparklabs.asanti.decoder.builtin;

import com.brightsparklabs.asanti.common.DecodeException;
import com.brightsparklabs.asanti.decoder.AsnByteDecoder;
import com.brightsparklabs.asanti.model.schema.AsnBuiltinType;
import com.brightsparklabs.asanti.validator.AsnByteValidator;
import com.brightsparklabs.asanti.validator.FailureType;
import com.brightsparklabs.asanti.validator.builtin.GeneralizedTimeValidator;
import com.brightsparklabs.asanti.validator.failure.ByteValidationFailure;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Decoder for data of type {@link AsnBuiltinType#GeneralizedTime}
 *
 * @author brightSPARK Labs
 */
public class GeneralizedTimeDecoder extends AbstractBuiltinTypeDecoder<Timestamp>
{
    // -------------------------------------------------------------------------
    // INSTANCE VARIABLES
    // -------------------------------------------------------------------------

    /** singleton instance */
    private static GeneralizedTimeDecoder instance;

    /** parser for the "core" of what an ASN.1 GeneralizedTime MUST consist of */
    private static final DateTimeFormatter core = new DateTimeFormatterBuilder().appendYear(4, 4)
            .appendMonthOfYear(2)
            .appendDayOfMonth(2)
            .appendHourOfDay(2)
            .toFormatter();

    /** a comma as a separator - decimal place */
    private static final DateTimeParser comma = new DateTimeFormatterBuilder().appendLiteral(",")
            .toParser();

    /** a dot as a separator - decimal place */
    private static final DateTimeParser dot = new DateTimeFormatterBuilder().appendLiteral(".")
            .toParser();

    /** the collection all of decimal place separators to parse for */
    private static final DateTimeParser[] decimal = { dot, comma };

    /** a time zone offset parser, specifying "Z" as no timezone, ie UTC */
    private static final DateTimeParser offset
            = new DateTimeFormatterBuilder().appendTimeZoneOffset("", "Z", false, 1, 2).toParser();

    /** parser option for when only up to the Hours are defined, so optional decimals and offset */
    private static final DateTimeFormatter uptoHours = new DateTimeFormatterBuilder().append(core)
            .appendOptional(new DateTimeFormatterBuilder().append(null, decimal)
                    .appendFractionOfHour(1, 18)
                    .toParser())
            .appendOptional(offset)
            .toFormatter();

    /** parser option for when only up to the Minutes are defined, so optional decimals and offset */
    private static final DateTimeFormatter uptoMinutes = new DateTimeFormatterBuilder().append(core)
            .appendMinuteOfHour(2)
            .appendOptional(new DateTimeFormatterBuilder().append(null, decimal)
                    .appendFractionOfMinute(1, 18)
                    .toParser())
            .appendOptional(offset)
            .toFormatter();

    /**
     * parser option for when full hours, minutes and seconds are defined, optional decimals and
     * offset
     */
    private static final DateTimeFormatter uptoSeconds = new DateTimeFormatterBuilder().append(core)
            .appendMinuteOfHour(2)
            .appendSecondOfMinute(2)
            .appendOptional(new DateTimeFormatterBuilder().append(null, decimal)
                    .appendFractionOfSecond(1, 18)
                    .toParser())
            .appendOptional(offset)
            .toFormatter();

    /** the collection of parsers to try - this is essentially how you do an "OR" with Joda */
    private static final DateTimeParser[] options = { uptoSeconds.getParser(),
                                                      uptoMinutes.getParser(),
                                                      uptoHours.getParser() };

    /**
     * The parser to use, it is an OR of the three precisions, each of which has its optional
     * components
     */
    private static final DateTimeFormatter parser = new DateTimeFormatterBuilder().append(null,
            options).toFormatter();

    /** a regex to use to work around Joda milli second limitation, ie extract the nanoseconds */
    private static final Pattern PATTERN_SUB_MILLI_SECONDS = Pattern.compile(
            "^([0-9]{14})(([,\\.])(([0-9]{1,3})([0-9]*)))(Z|((\\+|\\-)[0-9]+))?");

    /** regex to use to protect us from passing more than 18 decimal places to Joda */
    private static final Pattern PATTERN_18_DECIMAL_PLACES = Pattern.compile(
            "^([0-9]{10,12})(([,\\.])(([0-9]{1,18})([0-9]*)))(Z|((\\+|\\-)[0-9]+))?");

    // -------------------------------------------------------------------------
    // CONSTRUCTION
    // -------------------------------------------------------------------------

    /**
     * Default constructor.
     *
     * <p>This is private, use {@link #getInstance()} to obtain an instance</p>
     */
    private GeneralizedTimeDecoder() {}

    /**
     * Returns a singleton instance of this class
     *
     * @return a singleton instance of this class
     */
    public static GeneralizedTimeDecoder getInstance()
    {
        if (instance == null)
        {
            instance = new GeneralizedTimeDecoder();
        }
        return instance;
    }

    // -------------------------------------------------------------------------
    // IMPLEMENTATION: AbstractBuiltinTypeDecoder
    // -------------------------------------------------------------------------

    @Override
    public Timestamp decode(final byte[] bytes) throws DecodeException
    {
        OperationResult<Timestamp, ImmutableSet<ByteValidationFailure>> result = validateAndDecode(
                bytes);
        if (!result.wasSuccessful())
        {
            DecodeException.throwIfHasFailures(result.getFailureReason()
                    .or(ImmutableSet.<ByteValidationFailure>of()));
        }

        return result.getOutput();
    }

    @Override
    public String decodeAsString(final byte[] bytes) throws DecodeException
    {
        // GeneralizedTime is considered a "useful" type that is a specialisation of VisibleString
        // as such we should just return the "raw" string (if it is valid)
        // This is useful given that the decode to Timestamp discards timezone information
        // and may discard precision.

        OperationResult<Timestamp, ImmutableSet<ByteValidationFailure>> result = validateAndDecode(
                bytes);
        if (!result.wasSuccessful())
        {
            DecodeException.throwIfHasFailures(result.getFailureReason()
                    .or(ImmutableSet.<ByteValidationFailure>of()));
        }

        // Now that we know it is valid, return the raw string.
        return AsnByteDecoder.decodeAsVisibleString(bytes);
    }

    // -------------------------------------------------------------------------
    // PUBLIC METHODS
    // -------------------------------------------------------------------------

    /**
     * Validates and decodes the GeneralizedTime bytes. Method was added to avoid calling
     * parseDateTime multiple times.
     *
     * @param bytes
     *         bytes to be decoded.
     *
     * @return OperationResult that will contain a Timestamp if successful, or a
     * ByteValidationFailure otherwise
     */
    public static OperationResult<Timestamp, ImmutableSet<ByteValidationFailure>> validateAndDecode(
            final byte[] bytes)
    {
        // GeneralizedTime is considered a "useful" type that is a specialisation of VisibleString
        // as such we should check it against VisibleString first.

        final ImmutableSet<ByteValidationFailure> byteValidationFailures
                = AsnByteValidator.validateAsVisibleString(bytes);

        if (!byteValidationFailures.isEmpty())
        {
            // there are failures, so bail early.
            return OperationResult.createUnsuccessfulInstance(null, byteValidationFailures);
        }

        try
        {
            final String rawDateTime = AsnByteDecoder.decodeAsVisibleString(bytes);

            // There are a few things that Joda-Time is not handling that we need to.

            // It can't handle smaller than milliseconds, and we need to go to nano
            // So we'll parse out the sub-milliseconds ourselves (only for the seconds precision)
            final Matcher matcher = PATTERN_SUB_MILLI_SECONDS.matcher(rawDateTime);
            String replacement = rawDateTime;
            int nanos = 0;
            boolean setNanos = false;
            if (matcher.matches())
            {
                // The decimal places represent sub seconds
                final String subMilliSeconds = Strings.nullToEmpty(matcher.group(6));
                final String milliSeconds = Strings.nullToEmpty(matcher.group(5));

                if (!subMilliSeconds.isEmpty())
                {
                    // We can only handle up to 6 digits (giving 9 total, ie down to Nano seconds)
                    final String trimmedSubMilliSeconds = subMilliSeconds.substring(0,
                            Math.min(subMilliSeconds.length(), 6));
                    BigDecimal bd = new BigDecimal("0." + milliSeconds + trimmedSubMilliSeconds);
                    bd = bd.multiply(BigDecimal.valueOf(1000000000L));
                    nanos = bd.intValue();
                    setNanos = true;

                    // Joda only has milli second precision.  It can parse up to 18 decimals, but
                    // it will discard everything after 3 (in the fractionsOfSeconds case).
                    // By replacing here we are protecting against more than 18 decimal places
                    // which would cause Joda to throw an exception
                    replacement = matcher.replaceAll("$1$3$5$7");
                }
            }
            else
            {
                // Joda will throw an exception with more than 18 decimal places.
                final Matcher matcherLong = PATTERN_18_DECIMAL_PLACES.matcher(rawDateTime);
                replacement = rawDateTime;
                if (matcherLong.matches())
                {
                    // The decimal places represent either sub hours or sub minutes.
                    // This is a rare enough case that we will accept that Joda limits to
                    // millisecond precision.
                    final String more18 = Strings.nullToEmpty(matcherLong.group(6));
                    if (!more18.isEmpty())
                    {
                        // We can only handle up to 18 decimal places (Joda will throw with more,
                        // not that we can parse them)
                        replacement = matcherLong.replaceAll("$1$3$5$7");
                    }
                }
            }

            // Joda doesn't seem to understand that "Z" is not "z" (it seems to be case insensitive)
            // fake it.
            if (replacement.endsWith("z"))
            {
                final String error = GeneralizedTimeValidator.GENERALIZEDTIME_VALIDATION_ERROR
                        + "Invalid format: \"" + rawDateTime + "\" is malformed at \"z\"";
                return OperationResult.createUnsuccessfulInstance(null,
                        ImmutableSet.of(new ByteValidationFailure(bytes.length,
                                FailureType.DataIncorrectlyFormatted,
                                error)));
            }

            // use the Joda-Time parser
            final DateTime dateTime = parser.parseDateTime(replacement);
            Timestamp result = new Timestamp(dateTime.getMillis());
            if (setNanos)
            {
                result.setNanos(nanos);
            }
            return OperationResult.createSuccessfulInstance(result);
        }
        catch (IllegalArgumentException e)
        {
            final String error = GeneralizedTimeValidator.GENERALIZEDTIME_VALIDATION_ERROR
                    + e.getMessage();
            return OperationResult.createUnsuccessfulInstance(null,
                    ImmutableSet.of(new ByteValidationFailure(bytes.length,
                            FailureType.DataIncorrectlyFormatted,
                            error)));
        }
        catch (DecodeException e)
        {
            // In theory we should not get here because we explicitly validated the VisibleString
            // above.
            final String error = GeneralizedTimeValidator.GENERALIZEDTIME_VALIDATION_ERROR
                    + e.getMessage();
            return OperationResult.createUnsuccessfulInstance(null,
                    ImmutableSet.of(new ByteValidationFailure(bytes.length,
                            FailureType.DataIncorrectlyFormatted,
                            error)));
        }
    }

    // -------------------------------------------------------------------------
    // INTERNAL CLASS: OperationResult
    // -------------------------------------------------------------------------

    public static class OperationResult<T, FailureType>
    {
        // -------------------------------------------------------------------------
        // INSTANCE VARIABLES
        // -------------------------------------------------------------------------

        /** whether the operation was successful */
        private final boolean wasSuccessful;

        /** the resulting output from the operation */
        private final T output;

        /** the reason the operation failed (or an empty string if it did not fail) */
        private final Optional<FailureType> failureReason;

        // -------------------------------------------------------------------------
        // CONSTRUCTION
        // -------------------------------------------------------------------------

        /**
         * Default constructor
         *
         * @param wasSuccessful
         *         {@code true} if the operation was successful
         * @param output
         *         the resulting output from the operation
         * @param failureReason
         *         the reason the operation failed (or an empty string if it did not fail)
         */
        private OperationResult(boolean wasSuccessful, T output, FailureType failureReason)
        {
            this.wasSuccessful = wasSuccessful;
            this.output = output;
            this.failureReason = Optional.fromNullable(failureReason);
        }

        /**
         * Convenience method to create a result indicating the operation was successful. Results
         * can be created via:<br> {@code OperationResult<String> result =
         * createSuccessfulInstance(outputString);}
         *
         * <p>Which is more concise than:<br> {@code OperationResult<String> result = new
         * OperationResult<String>(true, outputString, "");}
         *
         * @param output
         *         the resulting output from the operation
         * @param <T>
         *         the type of the result
         * @param <FailureType>
         *         specifies the type of failure object (return in getFailureReason)
         *
         * @return a 'successful' result instance containing the supplied data
         */
        public static <T, FailureType> OperationResult<T, FailureType> createSuccessfulInstance(
                T output)
        {
            return new OperationResult<>(true, output, null);
        }

        /**
         * Convenience method to create a result indicating the operation was unsuccessful. Results
         * can be created via:<br> {@code OperationResult<String> result =
         * createUnsuccessfulInstance(outputString);}
         *
         * <p>Which is more concise than:<br> {@code OperationResult<String> result = new
         * OperationResult<String>(false, outputString, reason);}
         *
         * @param output
         *         the resulting output from the operation
         * @param failureReason
         *         the reason the operation failed
         * @param <T>
         *         the type of the result
         * @param <FailureType>
         *         specifies the type of failure object (return in getFailureReason)
         *
         * @return an 'unsuccessful' result instance containing the supplied data
         */
        public static <T, FailureType> OperationResult<T, FailureType> createUnsuccessfulInstance(
                T output, FailureType failureReason)
        {
            return new OperationResult<>(false, output, failureReason);
        }

        // -------------------------------------------------------------------------
        // PUBLIC METHODS
        // -------------------------------------------------------------------------

        /**
         * Returns {@code true} if the operation was successful
         *
         * @return {@code true} if the operation was successful
         */
        public boolean wasSuccessful()
        {
            return wasSuccessful;
        }

        /**
         * Returns the resulting output from the operation
         *
         * @return the resulting output from the operation
         */
        public T getOutput()
        {
            return output;
        }

        /**
         * Returns the reason the operation failed
         *
         * @return the reason the operation failed (or an empty string if it did not fail)
         */
        public Optional<FailureType> getFailureReason()
        {
            return failureReason;
        }
    }
}
