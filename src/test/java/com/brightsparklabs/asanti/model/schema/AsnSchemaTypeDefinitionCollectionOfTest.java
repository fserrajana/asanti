/*
 * Created by brightSPARK Labs
 * www.brightsparklabs.com
 */
package com.brightsparklabs.asanti.model.schema;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit tests for {@link AsnSchemaTypeDefinitionCollectionOf}
 *
 * @author brightSPARK Labs
 */
public class AsnSchemaTypeDefinitionCollectionOfTest
{
    // -------------------------------------------------------------------------
    // TESTS
    // -------------------------------------------------------------------------

    @Test
    public void testAsnSchemaConstructedTypeDefinition_Name() throws Exception
    {
        // test null
        try
        {
            new AsnSchemaTypeDefinitionCollectionOf(null, AsnBuiltinType.SetOf, "person", AsnSchemaConstraint.NULL);
            fail("NullPointerException not thrown");
        }
        catch (final NullPointerException ex)
        {
        }

        // test blank
        try
        {
            new AsnSchemaTypeDefinitionCollectionOf("", AsnBuiltinType.SetOf, "person", AsnSchemaConstraint.NULL);
            fail("IllegalArgumentException not thrown");
        }
        catch (final IllegalArgumentException ex)
        {
        }
        try
        {
            new AsnSchemaTypeDefinitionCollectionOf(" ", AsnBuiltinType.SetOf, "person", AsnSchemaConstraint.NULL);
            fail("IllegalArgumentException not thrown");
        }
        catch (final IllegalArgumentException ex)
        {
        }
    }

    @Test
    public void testAsnSchemaConstructedTypeDefinition_Type() throws Exception
    {
        // test null
        try
        {
            new AsnSchemaTypeDefinitionCollectionOf("NAME", null, "person", AsnSchemaConstraint.NULL);
            fail("NullPointerException not thrown");
        }
        catch (final NullPointerException ex)
        {
        }

        // test creation using all types
        for (final AsnBuiltinType type : AsnBuiltinType.values())
        {
            if (AsnSchemaTypeDefinitionCollectionOf.validTypes.contains(type))
            {
                try
                {
                    new AsnSchemaTypeDefinitionCollectionOf("NAME", type, "person", AsnSchemaConstraint.NULL);
                }
                catch (final IllegalArgumentException ex)
                {
                    fail("IllegalAccessException thrown");
                }
            }
            else
            {
                try
                {
                    new AsnSchemaTypeDefinitionCollectionOf("NAME", type, "person", AsnSchemaConstraint.NULL);
                    fail("IllegalAccessException not thrown");
                }
                catch (final IllegalArgumentException ex)
                {
                }
            }
        }
    }

    @Test
    public void testAsnSchemaConstructedTypeDefinition_ElementTypeName() throws Exception
    {
        // test null
        try
        {
            new AsnSchemaTypeDefinitionCollectionOf("NAME", AsnBuiltinType.SetOf, null, AsnSchemaConstraint.NULL);
            fail("NullPointerException not thrown");
        }
        catch (final NullPointerException ex)
        {
        }

        // test blank
        try
        {
            new AsnSchemaTypeDefinitionCollectionOf("", AsnBuiltinType.SetOf, "", AsnSchemaConstraint.NULL);
            fail("IllegalArgumentException not thrown");
        }
        catch (final IllegalArgumentException ex)
        {
        }
        try
        {
            new AsnSchemaTypeDefinitionCollectionOf(" ", AsnBuiltinType.SetOf, " ", AsnSchemaConstraint.NULL);
            fail("IllegalArgumentException not thrown");
        }
        catch (final IllegalArgumentException ex)
        {
        }
    }

    @Test
    public void testAsnSchemaConstructedTypeDefinition_Constraint() throws Exception
    {
        // test null
        try
        {
            new AsnSchemaTypeDefinitionCollectionOf("NAME", AsnBuiltinType.SetOf, "person", null);
        }
        catch (final NullPointerException ex)
        {
            fail("NullPointerException thrown");
        }
    }

    @Test
    public void testGetTagName() throws Exception
    {
        final AsnSchemaTypeDefinitionCollectionOf instance =
                new AsnSchemaTypeDefinitionCollectionOf("NAME",
                        AsnBuiltinType.SetOf,
                        "person",
                        AsnSchemaConstraint.NULL);
        // test explicit tags
        assertEquals("", instance.getTagName("1"));
        assertEquals("", instance.getTagName("2"));
        assertEquals("", instance.getTagName("99"));
        assertEquals("", instance.getTagName("anything"));
        // test null
        assertEquals("", instance.getTagName(null));
        // test empty
        assertEquals("", instance.getTagName(""));
        assertEquals("", instance.getTagName(" "));
    }

    @Test
    public void testGetTypeName() throws Exception
    {
        final AsnSchemaTypeDefinitionCollectionOf instance =
                new AsnSchemaTypeDefinitionCollectionOf("NAME",
                        AsnBuiltinType.SequenceOf,
                        "person",
                        AsnSchemaConstraint.NULL);
        // test explicit tags
        assertEquals("person", instance.getTypeName("1"));
        assertEquals("person", instance.getTypeName("2"));
        assertEquals("person", instance.getTypeName("99"));
        assertEquals("person", instance.getTypeName("anything"));
        // test null
        assertEquals("person", instance.getTypeName(null));
        // test empty
        assertEquals("person", instance.getTypeName(""));
        assertEquals("person", instance.getTypeName(" "));
    }
}