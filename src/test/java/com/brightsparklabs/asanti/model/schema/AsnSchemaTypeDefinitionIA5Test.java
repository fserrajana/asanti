/*
 * Created by brightSPARK Labs
 * www.brightsparklabs.com
 */
package com.brightsparklabs.asanti.model.schema;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit tests for {@link AsnSchemaTypeDefinitionIA5String}
 *
 * @author brightSPARK Labs
 */
public class AsnSchemaTypeDefinitionIA5Test
{
    // -------------------------------------------------------------------------
    // TESTS
    // -------------------------------------------------------------------------

    @Test
    public void testAsnSchemaTypeDefinitionIA5String() throws Exception
    {
        // test valid
        new AsnSchemaTypeDefinitionIA5String("TEST_NAME", AsnSchemaConstraint.NULL);
        new AsnSchemaTypeDefinitionIA5String("TEST_NAME", AsnSchemaConstraint.NULL);

        // test null
        try
        {
            new AsnSchemaTypeDefinitionIA5String(null, AsnSchemaConstraint.NULL);
            fail("NullPointerException not thrown");
        }
        catch (final NullPointerException ex)
        {
        }
        try
        {
            new AsnSchemaTypeDefinitionIA5String("TEST_NAME", null);
        }
        catch (final NullPointerException ex)
        {
            fail("NullPointerException thrown");
        }

        // test empty
        try
        {
            new AsnSchemaTypeDefinitionIA5String("", AsnSchemaConstraint.NULL);
            fail("IllegalArgumentException not thrown");
        }
        catch (final IllegalArgumentException ex)
        {
        }
        try
        {
            new AsnSchemaTypeDefinitionIA5String(" ", AsnSchemaConstraint.NULL);
            fail("IllegalArgumentException not thrown");
        }
        catch (final IllegalArgumentException ex)
        {
        }
    }

    @Test
    public void testGetBuiltinType() throws Exception
    {
        final AsnSchemaTypeDefinitionIA5String instance =
                new AsnSchemaTypeDefinitionIA5String("TEST_NAME", AsnSchemaConstraint.NULL);
        assertEquals(AsnBuiltinType.IA5String, instance.getBuiltinType());
    }
}
