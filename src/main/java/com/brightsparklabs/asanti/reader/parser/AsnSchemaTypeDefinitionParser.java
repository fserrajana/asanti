/*
 * Created by brightSPARK Labs
 * www.brightsparklabs.com
 */

package com.brightsparklabs.asanti.reader.parser;

import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.brightsparklabs.asanti.model.schema.AsnSchemaComponentType;
import com.brightsparklabs.asanti.model.schema.AsnSchemaEnumeratedOption;
import com.brightsparklabs.asanti.model.schema.AsnSchemaTypeDefinition;
import com.brightsparklabs.asanti.model.schema.AsnSchemaTypeDefinitionBitString;
import com.brightsparklabs.asanti.model.schema.AsnSchemaTypeDefinitionChoice;
import com.brightsparklabs.asanti.model.schema.AsnSchemaTypeDefinitionEnumerated;
import com.brightsparklabs.asanti.model.schema.AsnSchemaTypeDefinitionIA5String;
import com.brightsparklabs.asanti.model.schema.AsnSchemaTypeDefinitionInteger;
import com.brightsparklabs.asanti.model.schema.AsnSchemaTypeDefinitionNumericString;
import com.brightsparklabs.asanti.model.schema.AsnSchemaTypeDefinitionOctetString;
import com.brightsparklabs.asanti.model.schema.AsnSchemaTypeDefinitionSequence;
import com.brightsparklabs.asanti.model.schema.AsnSchemaTypeDefinitionSequenceOf;
import com.brightsparklabs.asanti.model.schema.AsnSchemaTypeDefinitionSet;
import com.brightsparklabs.asanti.model.schema.AsnSchemaTypeDefinitionSetOf;
import com.brightsparklabs.asanti.model.schema.AsnSchemaTypeDefinitionUTF8String;
import com.brightsparklabs.asanti.model.schema.AsnSchemaTypeDefinitionVisibleString;
import com.brightsparklabs.asanti.model.schema.constraint.AsnSchemaConstraint;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * Logic for parsing a Type Definition from a module within an ASN.1 schema
 *
 * @author brightSPARK Labs
 */
public final class AsnSchemaTypeDefinitionParser
{
    // -------------------------------------------------------------------------
    // CONSTANTS
    // -------------------------------------------------------------------------

    /** pattern to match a SET/SEQUENCE type definition */
    private static final Pattern PATTERN_TYPE_DEFINITION_SEQUENCE =
            Pattern.compile("^SEQUENCE ?\\{(.+)\\} ?(\\(.+\\))?$");

    /** pattern to match a SET/SEQUENCE type definition */
    private static final Pattern PATTERN_TYPE_DEFINITION_SET = Pattern.compile("^SET ?\\{(.+)\\} ?(\\(.+\\))?$");

    /** pattern to match a ENUMERATED type definition */
    private static final Pattern PATTERN_TYPE_DEFINITION_ENUMERATED = Pattern.compile("^ENUMERATED ?\\{(.+)\\}$");

    /** pattern to match a CHOICE type definition */
    private static final Pattern PATTERN_TYPE_DEFINITION_CHOICE = Pattern.compile("^CHOICE ?\\{(.+)\\} ?(\\(.+\\))?$");

    /** pattern to match a SEQUENCE OF type definition */
    private static final Pattern PATTERN_TYPE_DEFINITION_SEQUENCE_OF = Pattern.compile("^SEQUENCE( .+)? OF (.+)$");

    /** pattern to match a SET OF type definition */
    private static final Pattern PATTERN_TYPE_DEFINITION_SET_OF = Pattern.compile("^SET( .+)? OF (.+)$");

    /** pattern to match a CLASS type definition */
    private static final Pattern PATTERN_TYPE_DEFINITION_CLASS = Pattern.compile("^CLASS ?\\{(.+)\\}$");

    /** pattern to match an OCTET STRING type definition */
    private static final Pattern PATTERN_TYPE_DEFINITION_OCTET_STRING =
            Pattern.compile("^OCTET STRING ?(\\((.+)\\))?$");

    /** pattern to match a BIT STRING type definition */
    private static final Pattern PATTERN_TYPE_DEFINITION_BIT_STRING =
            Pattern.compile("^BIT STRING ?(\\{(.+?)\\})? ?(DEFAULT ?\\{(.+?)\\})? ?(\\((.+?)\\))?$");

    /** pattern to match an IA5String type definition */
    private static final Pattern PATTERN_TYPE_DEFINITION_IA5_STRING = Pattern.compile("^IA5String ?(\\((.+)\\))?$");

    /** pattern to match a UTF8 type definition */
    private static final Pattern PATTERN_TYPE_DEFINITION_UTF8_STRING = Pattern.compile("^UTF8String ?(\\((.+)\\))?$");

    /** pattern to match a NumericString type definition */
    private static final Pattern PATTERN_TYPE_DEFINITION_NUMERIC_STRING =
            Pattern.compile("^NumericString ?(\\((.+)\\))?$");

    /** pattern to match a VisibleString type definition */
    private static final Pattern PATTERN_TYPE_DEFINITION_VISIBLE_STRING =
            Pattern.compile("^VisibleString ?(\\((.+)\\))?$");

    /** pattern to match an Integer type definition */
    private static final Pattern PATTERN_TYPE_DEFINITION_INTEGER =
            Pattern.compile("^INTEGER ?(\\{(.+?)\\})? ?(\\((.+?)\\)) ?(DEFAULT ?.)?$");

    // TODO ASN-25 remove this once ASN-25 is completed
    /** pattern to match a PRIMITIVE type definition */
    private static final Pattern PATTERN_TYPE_DEFINITION_PRIMITIVE =
            Pattern.compile("^(GeneralizedTime|BOOLEAN|DATE|CHARACTER STRING|DATE_TIME|DURATION|EMBEDDED PDV|EXTERNAL|INTEGER|OID-IRI|NULL|OBJECT IDENTIFIER|REAL|RELATIVE-OID-IRI|RELATIVE-OID|BMPString|GeneralString|GraphicString|ISO646String|PrintableString|TeletexString|T61String|UniversalString|VideotexString|TIME|TIME-OF-DAY|CHARACTER STRING) ?(\\{(.+)\\})? ?(\\((.+)\\))?$");

    /** error message if an unknown ASN.1 built-in type is found */
    private static final String ERROR_UNKNOWN_BUILT_IN_TYPE =
            "Parser expected a constructed built-in type (SEQUENCE, SET, ENUMERATED, SEQUENCE OF, SET OF, CHOICE, CLASS) or a primitive built-in type (BIT STRING, GeneralizedTime, IA5String, INTEGER, NumericString, OCTET STRING, UTF8String, VisibleString, BOOLEAN, DATE, CHARACTER STRING, DATE_TIME, DURATION, EMBEDDED PDV, EXTERNAL, INTEGER, OID-IRI, NULL, OBJECT IDENTIFIER, REAL, RELATIVE-OID-IRI, RELATIVE-OID, BMPString, GeneralString, GraphicString, ISO646String, PrintableString, TeletexString, T61String, UniversalString, VideotexString, TIME, TIME-OF-DAY, CHARACTER STRING) but found: ";

    // -------------------------------------------------------------------------
    // CLASS VARIABLES
    // -------------------------------------------------------------------------

    /** class logger */
    private static final Logger log = Logger.getLogger(AsnSchemaTypeDefinitionParser.class.getName());

    // -------------------------------------------------------------------------
    // CONSTRUCTION
    // -------------------------------------------------------------------------

    /**
     * Private constructor. There should be no need to ever instantiate this
     * static class.
     */
    private AsnSchemaTypeDefinitionParser()
    {
        throw new AssertionError();
    }

    // -------------------------------------------------------------------------
    // PUBLIC METHODS
    // -------------------------------------------------------------------------

    /**
     * Parses a type definition from a module from an ASN.1 schema
     *
     * @param name
     *            the name of the defined type (i.e. the text on the left hand
     *            side of the {@code ::=})
     * @param value
     *            the value of the defined type (i.e. the text on the right hand
     *            side of the {@code ::=})
     *
     * @return an {@link AsnSchemaTypeDefinition} representing the parsed data
     *
     * @throws ParseException
     *             if any errors occur while parsing the type
     */
    public static AsnSchemaTypeDefinition parse(String name, String value) throws ParseException
    {
        log.log(Level.FINE, "Found type definition: {0} = {1}", new Object[] { name, value });

        // check if defining a SEQUENCE
        Matcher matcher = PATTERN_TYPE_DEFINITION_SEQUENCE.matcher(value);
        if (matcher.matches()) { return parseSequence(name, matcher); }

        // check if defining a SET
        matcher = PATTERN_TYPE_DEFINITION_SET.matcher(value);
        if (matcher.matches()) { return parseSet(name, matcher); }

        // check if defining a CHOICE
        matcher = PATTERN_TYPE_DEFINITION_CHOICE.matcher(value);
        if (matcher.matches()) { return parseChoice(name, matcher); }

        // check if defining an ENUMERATED
        matcher = PATTERN_TYPE_DEFINITION_ENUMERATED.matcher(value);
        if (matcher.matches()) { return parseEnumerated(name, matcher); }

        // check if defining an OCTET STRING
        matcher = PATTERN_TYPE_DEFINITION_OCTET_STRING.matcher(value);
        if (matcher.matches()) { return parseOctetString(name, matcher); }

        // check if defining a BIT STRING
        matcher = PATTERN_TYPE_DEFINITION_BIT_STRING.matcher(value);
        if (matcher.matches()) { return parseBitString(name, matcher); }

        // check if defining an IA5String
        matcher = PATTERN_TYPE_DEFINITION_IA5_STRING.matcher(value);
        if (matcher.matches()) { return parseIA5String(name, matcher); }

        // check if defining a UTF8String
        matcher = PATTERN_TYPE_DEFINITION_UTF8_STRING.matcher(value);
        if (matcher.matches()) { return parseUTF8String(name, matcher); }

        // check if defining a NumericString
        matcher = PATTERN_TYPE_DEFINITION_NUMERIC_STRING.matcher(value);
        if (matcher.matches()) { return parseNumericString(name, matcher); }

        // check if defining a VisibleString
        matcher = PATTERN_TYPE_DEFINITION_VISIBLE_STRING.matcher(value);
        if (matcher.matches()) { return parseVisibleString(name, matcher); }

        // check if defining a Integer
        matcher = PATTERN_TYPE_DEFINITION_INTEGER.matcher(value);
        if (matcher.matches()) { return parseInteger(name, matcher); }

        // check if defining a PRIMITIVE
        matcher = PATTERN_TYPE_DEFINITION_PRIMITIVE.matcher(value);
        if (matcher.matches())
        {
            /*
             * TODO ASN-25 - handle all primitive types. Currently this is just
             * a catch-all to log warnings
             */
            final String builtinType = matcher.group(1);
            final String error =
                    String.format("Cannot parse unsupported ASN.1 built-in type: %s for type: %s", builtinType, name);
            log.warning(error);
            return AsnSchemaTypeDefinition.NULL;
        }

        // check if defining a SEQUENCE OF
        matcher = PATTERN_TYPE_DEFINITION_SEQUENCE_OF.matcher(value);
        if (matcher.matches()) { return parseSequenceOf(name, matcher); }

        // check if defining a SET OF
        matcher = PATTERN_TYPE_DEFINITION_SET_OF.matcher(value);
        if (matcher.matches()) { return parseSetOf(name, matcher); }

        // check if defining a CLASS
        matcher = PATTERN_TYPE_DEFINITION_CLASS.matcher(value);
        if (matcher.matches())
        {
            // TODO ASN-39 - handle CLASS
            log.warning("Type Definitions for CLASS not yet supported");
            return AsnSchemaTypeDefinition.NULL;
        }

        // unknown definition
        final String error = ERROR_UNKNOWN_BUILT_IN_TYPE + name + " ::= " + value;
        throw new ParseException(error, -1);
    }

    // -------------------------------------------------------------------------
    // PRIVATE METHODS
    // -------------------------------------------------------------------------

    /**
     * Parses a SEQUENCE type definition
     *
     * @param name
     *            name of the defined type
     *
     * @param matcher
     *            matcher which matched on
     *            {@link #PATTERN_TYPE_DEFINITION_SEQUENCE}
     *
     * @return an {@link AsnSchemaTypeDefinitionSequence} representing the
     *         parsed data
     *
     * @throws ParseException
     *             if any errors occur while parsing the type
     */
    private static AsnSchemaTypeDefinitionSequence parseSequence(String name, Matcher matcher) throws ParseException
    {
        final String componentTypesText = matcher.group(1);
        final String constraintText = Strings.nullToEmpty(matcher.group(2));

        final ImmutableList<AsnSchemaComponentType> componentTypes =
                AsnSchemaComponentTypeParser.parse(componentTypesText);
        final AsnSchemaConstraint constraint = AsnSchemaConstraintParser.parse(constraintText);
        final AsnSchemaTypeDefinitionSequence typeDefinition =
                new AsnSchemaTypeDefinitionSequence(name, componentTypes, constraint);
        return typeDefinition;
    }

    /**
     * Parses a SET type definition
     *
     * @param name
     *            name of the defined type
     *
     * @param matcher
     *            matcher which matched on {@link #PATTERN_TYPE_DEFINITION_SET}
     *
     * @return an {@link AsnSchemaTypeDefinitionSet} representing the parsed
     *         data
     *
     * @throws ParseException
     *             if any errors occur while parsing the type
     */
    private static AsnSchemaTypeDefinitionSet parseSet(String name, Matcher matcher) throws ParseException
    {
        final String componentTypesText = matcher.group(1);
        final String constraintText = Strings.nullToEmpty(matcher.group(2));

        final ImmutableList<AsnSchemaComponentType> componentTypes =
                AsnSchemaComponentTypeParser.parse(componentTypesText);
        final AsnSchemaConstraint constraint = AsnSchemaConstraintParser.parse(constraintText);
        final AsnSchemaTypeDefinitionSet typeDefinition =
                new AsnSchemaTypeDefinitionSet(name, componentTypes, constraint);
        return typeDefinition;
    }

    /**
     * Parses a CHOICE type definition
     *
     * @param name
     *            name of the defined type
     *
     * @param matcher
     *            matcher which matched on
     *            {@link #PATTERN_TYPE_DEFINITION_CHOICE}
     *
     * @return an {@link AsnSchemaTypeDefinitionChoice} representing the parsed
     *         data
     *
     * @throws ParseException
     *             if any errors occur while parsing the type
     */
    private static AsnSchemaTypeDefinitionChoice parseChoice(String name, Matcher matcher) throws ParseException
    {
        final String componentTypesText = matcher.group(1);
        final String constraintText = Strings.nullToEmpty(matcher.group(2));

        final ImmutableList<AsnSchemaComponentType> componentTypes =
                AsnSchemaComponentTypeParser.parse(componentTypesText);
        final AsnSchemaConstraint constraint = AsnSchemaConstraintParser.parse(constraintText);
        final AsnSchemaTypeDefinitionChoice typeDefinition =
                new AsnSchemaTypeDefinitionChoice(name, componentTypes, constraint);
        return typeDefinition;
    }

    /**
     * Parses an ENUMERATED type definition
     *
     * @param name
     *            name of the defined type
     *
     * @param matcher
     *            matcher which matched on
     *            {@link #PATTERN_TYPE_DEFINITION_ENUMERATED}
     *
     * @return an {@link AsnSchemaTypeDefinitionEnumerated} representing the
     *         parsed data
     *
     * @throws ParseException
     *             if any errors occur while parsing the type
     */
    private static AsnSchemaTypeDefinitionEnumerated parseEnumerated(String name, Matcher matcher)
            throws ParseException
    {
        final String enumeratedOptionsText = matcher.group(1);

        final ImmutableList<AsnSchemaEnumeratedOption> enumeratedOptions =
                AsnSchemaEnumeratedParser.parse(enumeratedOptionsText);
        final AsnSchemaTypeDefinitionEnumerated typeDefinition =
                new AsnSchemaTypeDefinitionEnumerated(name, enumeratedOptions);
        return typeDefinition;
    }

    /**
     * Parses an OCTET STRING type definition
     *
     * @param name
     *            name of the defined type
     *
     * @param matcher
     *            matcher which matched on
     *            {@link #PATTERN_TYPE_DEFINITION_OCTET_STRING}
     *
     * @return an {@link AsnSchemaTypeDefinitionOctetString} representing the
     *         parsed data
     *
     * @throws ParseException
     *             if any errors occur while parsing the type
     */
    private static AsnSchemaTypeDefinitionOctetString parseOctetString(String name, Matcher matcher)
            throws ParseException
    {
        final String constraintText = Strings.nullToEmpty(matcher.group(2));
        return AsnSchemaTypeDefinitionPrimitiveParser.parseOctetString(name, constraintText);
    }

    /**
     * Parses a BIT STRING type definition
     *
     * @param name
     *            name of the defined type
     *
     * @param matcher
     *            matcher which matched on
     *            {@link #PATTERN_TYPE_DEFINITION_BIT_STRING}
     *
     * @return an {@link AsnSchemaTypeDefinitionBitString} representing the
     *         parsed data
     *
     * @throws ParseException
     *             if any errors occur while parsing the type
     */
    private static AsnSchemaTypeDefinitionBitString parseBitString(String name, Matcher matcher) throws ParseException
    {
        // TODO ASN-87 - parse list of named bits
        final String constraintText = Strings.nullToEmpty(matcher.group(6));
        return AsnSchemaTypeDefinitionPrimitiveParser.parseBitString(name, constraintText);
    }

    /**
     * Parses an IA5String type definition
     *
     * @param name
     *            name of the defined type
     *
     * @param matcher
     *            matcher which matched on
     *            {@link #PATTERN_TYPE_DEFINITION_IA5_STRING}
     *
     * @return an {@link AsnSchemaTypeDefinitionIA5String} representing the
     *         parsed data
     *
     * @throws ParseException
     *             if any errors occur while parsing the type
     */
    private static AsnSchemaTypeDefinitionIA5String parseIA5String(String name, Matcher matcher) throws ParseException
    {
        final String constraintText = Strings.nullToEmpty(matcher.group(2));
        return AsnSchemaTypeDefinitionPrimitiveParser.parseIA5String(name, constraintText);
    }

    /**
     * Parses a UTF8String type definition
     *
     * @param name
     *            name of the defined type
     *
     * @param matcher
     *            matcher which matched on
     *            {@link #PATTERN_TYPE_DEFINITION_UTF8_STRING}
     *
     * @return an {@link AsnSchemaTypeDefinitionUTF8String} representing the
     *         parsed data
     *
     * @throws ParseException
     *             if any errors occur while parsing the type
     */
    private static AsnSchemaTypeDefinitionUTF8String parseUTF8String(String name, Matcher matcher)
            throws ParseException
    {
        final String constraintText = Strings.nullToEmpty(matcher.group(2));
        return AsnSchemaTypeDefinitionPrimitiveParser.parseUTF8String(name, constraintText);
    }

    /**
     * Parses a NumericString type definition
     *
     * @param name
     *            name of the defined type
     *
     * @param matcher
     *            matcher which matched on
     *            {@link #PATTERN_TYPE_DEFINITION_NUMERIC_STRING}
     *
     * @return an {@link AsnSchemaTypeDefinitionNumericString} representing the
     *         parsed data
     *
     * @throws ParseException
     *             if any errors occur while parsing the type
     */
    private static AsnSchemaTypeDefinitionNumericString parseNumericString(String name, Matcher matcher)
            throws ParseException
    {
        final String constraintText = Strings.nullToEmpty(matcher.group(2));
        return AsnSchemaTypeDefinitionPrimitiveParser.parseNumericString(name, constraintText);
    }

    /**
     * Parses a VisibleString type definition
     *
     * @param name
     *            name of the defined type
     *
     * @param matcher
     *            matcher which matched on
     *            {@link #PATTERN_TYPE_DEFINITION_VISIBLE_STRING}
     *
     * @return an {@link AsnSchemaTypeDefinitionVisibleString} representing the
     *         parsed data
     *
     * @throws ParseException
     *             if any errors occur while parsing the type
     */
    private static AsnSchemaTypeDefinitionVisibleString parseVisibleString(String name, Matcher matcher)
            throws ParseException
    {
        final String constraintText = Strings.nullToEmpty(matcher.group(2));
        return AsnSchemaTypeDefinitionPrimitiveParser.parseVisibleString(name, constraintText);
    }

    /**
     * Parses an Integer type definition
     *
     * @param name
     *            name of the defined type
     *
     * @param matcher
     *            matcher which matched on
     *            {@link #PATTERN_TYPE_DEFINITION_INTEGER}
     *
     * @return an {@link AsnSchemaTypeDefinitionInteger} representing the parsed
     *         data
     *
     * @throws ParseException
     *             if any errors occur while parsing the type
     */
    private static AsnSchemaTypeDefinitionInteger parseInteger(String name, Matcher matcher) throws ParseException
    {
        // TODO ASN-95 - parse optional distinguished values
        final String constraintText = Strings.nullToEmpty(matcher.group(2));
        return AsnSchemaTypeDefinitionPrimitiveParser.parseInteger(name, constraintText);
    }

    /**
     * Parses a SEQUENCE OF type definition
     *
     * @param name
     *            name of the defined type
     *
     * @param matcher
     *            matcher which matched on
     *            {@link #PATTERN_TYPE_DEFINITION_SEQUENCE_OF}
     *
     * @return an {@link AsnSchemaTypeDefinitionSequenceOf} representing the
     *         parsed data
     *
     * @throws ParseException
     *             if any errors occur while parsing the type
     */
    private static AsnSchemaTypeDefinitionSequenceOf parseSequenceOf(String name, Matcher matcher)
            throws ParseException
    {
        final String constraintText = Strings.nullToEmpty(matcher.group(1));
        final String elementTypeName = matcher.group(2);

        final AsnSchemaConstraint constraint = AsnSchemaConstraintParser.parse(constraintText);
        final AsnSchemaTypeDefinitionSequenceOf typeDefinition =
                new AsnSchemaTypeDefinitionSequenceOf(name, elementTypeName, constraint);
        return typeDefinition;
    }

    /**
     * Parses a SET OF type definition
     *
     * @param name
     *            name of the defined type
     *
     * @param matcher
     *            matcher which matched on
     *            {@link #PATTERN_TYPE_DEFINITION_SET_OF}
     *
     * @return an {@link AsnSchemaTypeDefinitionSetOf} representing the parsed
     *         data
     *
     * @throws ParseException
     *             if any errors occur while parsing the type
     */
    private static AsnSchemaTypeDefinitionSetOf parseSetOf(String name, Matcher matcher) throws ParseException
    {
        final String constraintText = Strings.nullToEmpty(matcher.group(1));
        final String elementTypeName = matcher.group(2);

        final AsnSchemaConstraint constraint = AsnSchemaConstraintParser.parse(constraintText);
        final AsnSchemaTypeDefinitionSetOf typeDefinition =
                new AsnSchemaTypeDefinitionSetOf(name, elementTypeName, constraint);
        return typeDefinition;
    }
}
