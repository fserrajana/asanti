package com.brightsparklabs.asanti.model.schema.type;

import com.brightsparklabs.asanti.model.schema.constraint.AsnSchemaConstraint;
import com.brightsparklabs.asanti.model.schema.primitive.AsnPrimitiveType;
import com.google.common.collect.ImmutableSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AsnSchemaTypePlaceholder}
 *
 * @author brightSPARK Labs
 */
public class AsnSchemaTypePlaceholderTest
{
    // -------------------------------------------------------------------------
    // FIXTURES
    // -------------------------------------------------------------------------

    /** the type the instance collection is wrapping */
    private AsnSchemaType indirectType;

    /** the instance that will be used for testing */
    private AsnSchemaTypePlaceholder instance;

    /** component "0" of the Sequence the placeholder will point to */
    private AsnSchemaType sequenceComponent;

    // -------------------------------------------------------------------------
    // SETUP/TEAR-DOWN
    // -------------------------------------------------------------------------

    @Before
    public void setUpBeforeTest() throws Exception
    {
        sequenceComponent = mock(AsnSchemaType.class);

        indirectType = mock(AsnSchemaType.class);
        // For the sake of testing that the Collection is delegating to the element type make it
        // return testable values

        AsnSchemaConstraint constraint1 = mock(AsnSchemaConstraint.class);
        AsnSchemaConstraint constraint2 = mock(AsnSchemaConstraint.class);

        when(indirectType.getPrimitiveType()).thenReturn(AsnPrimitiveType.SEQUENCE);
        when(indirectType.getChildType("0")).thenReturn(sequenceComponent);
        when(indirectType.getChildName("0")).thenReturn("foo");
        when(indirectType.getConstraints()).thenReturn(ImmutableSet.of(
                constraint1, constraint2));

        instance = new AsnSchemaTypePlaceholder("Module", "Type", AsnSchemaConstraint.NULL);

    }

    @After
    public void validate()
    {
        // forces Mockito to cause the failure (for verify) on the failing test, rather than the next one!
        validateMockitoUsage();
    }

    // -------------------------------------------------------------------------
    // TESTS
    // -------------------------------------------------------------------------

    @Test
    public void testAsnSchemaTypePlaceholderConstructorPreconditions() throws Exception
    {
        // null moduleName
        try
        {
            new AsnSchemaTypePlaceholder(null, "Type", AsnSchemaConstraint.NULL);
        }
        catch (final NullPointerException ex)
        {
            fail("Module name can be null");
        }
        // blank moduleName
        try
        {
            new AsnSchemaTypePlaceholder("", "Type", AsnSchemaConstraint.NULL);
        }
        catch (final NullPointerException ex)
        {
            fail("Module name can be null");
        }

        // null Type
        try
        {
            new AsnSchemaTypePlaceholder("Module", null, AsnSchemaConstraint.NULL);
            fail("NullPointerException not thrown");
        }
        catch (final NullPointerException ex)
        {
        }

        // blank type
        try
        {
            new AsnSchemaTypePlaceholder("Module", "", AsnSchemaConstraint.NULL);
            fail("IllegalArgumentException not thrown");
        }
        catch (final IllegalArgumentException ex)
        {
        }
    }

    @Test
    public void testGetModuleName() throws Exception
    {
        assertEquals("Module", instance.getModuleName());
        verifyZeroInteractions(indirectType);

        instance.setIndirectType(indirectType);
        assertEquals("Module", instance.getModuleName());
        verifyZeroInteractions(indirectType);
    }

    @Test
    public void testGetTypeName() throws Exception
    {
        assertEquals("Type", instance.getTypeName());
        verifyZeroInteractions(indirectType);

        instance.setIndirectType(indirectType);
        assertEquals("Type", instance.getTypeName());
        verifyZeroInteractions(indirectType);
    }

    @Test
    public void testSetIndirectType() throws Exception
    {
        // test that if the placeholder is not resolved it does not delegate
        assertEquals(AsnPrimitiveType.NULL, instance.getPrimitiveType());
        verify(indirectType, never()).getPrimitiveType();
        instance.setIndirectType(indirectType);

        // and that it does delegate after it is resolved
        assertEquals(AsnPrimitiveType.SEQUENCE, instance.getPrimitiveType());
        verify(indirectType).getPrimitiveType();
    }

    @Test
    public void testGetConstraints() throws Exception
    {
        // test that if the placeholder is not resolved it does not delegate
        assertEquals(1, instance.getConstraints().size());
        verify(indirectType, never()).getConstraints();
        instance.setIndirectType(indirectType);

        // and that it does delegate after it is resolved
        assertEquals(3, instance.getConstraints().size());
        verify(indirectType).getConstraints();
    }

    @Test
    public void testGetChildType() throws Exception
    {
        // test that if the placeholder is not resolved it does not delegate
        assertEquals(AsnSchemaType.NULL, instance.getChildType("0"));
        verify(indirectType, never()).getChildType("0");
        instance.setIndirectType(indirectType);

        // and that it does delegate after it is resolved
        assertEquals(sequenceComponent, instance.getChildType("0"));
        verify(indirectType).getChildType("0");
    }

    @Test
    public void testGetChildName() throws Exception
    {
        // test that if the placeholder is not resolved it does not delegate
        assertEquals("", instance.getChildName("0"));
        verify(indirectType, never()).getChildName("0");

        instance.setIndirectType(indirectType);
        // and that it does delegate after it is resolved
        assertEquals("foo", instance.getChildName("0"));
        verify(indirectType).getChildName("0");
    }
}