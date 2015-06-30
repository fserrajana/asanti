/*
 * Created by brightSPARK Labs
 * www.brightsparklabs.com
 */
package com.brightsparklabs.asanti.mocks.model.schema;

import com.brightsparklabs.asanti.model.schema.primitive.AsnPrimitiveType;
import com.brightsparklabs.asanti.model.schema.type.AsnSchemaType;
import com.brightsparklabs.asanti.model.schema.typedefinition.AsnSchemaComponentType;
import com.google.common.collect.ImmutableList;

import static org.mockito.Mockito.*;

/**
 * Utility class for obtaining mocked instances of {@link AsnSchemaComponentType} which conform to
 * the test ASN.1 schema defined in the {@linkplain README.md} file
 *
 * @author brightSPARK Labs
 */
public class MockAsnSchemaComponentType
{
    // -------------------------------------------------------------------------
    // PUBLIC METHODS
    // -------------------------------------------------------------------------

    /**
     * Default constructor. This is hidden, use one of the factory methods instead.
     */
    private MockAsnSchemaComponentType()
    {
        // private constructor
    }

    /**
     * Creates a mock {@link AsnSchemaComponentType} instance
     *
     * @param tagName
     *         value to return for {@link AsnSchemaComponentType#getTagName()}
     * @param tag
     *         value to return for {@link AsnSchemaComponentType#getTag()}
     * @param typeName
     *         value to return for {@link AsnSchemaComponentType#getTypeName()}
     * @param isOptional
     *         value to return for {@link AsnSchemaComponentType#isOptional()}
     * @param type
     *          value to return for {@link AsnSchemaComponentType#getType()}
     *
     * @return mock instance which returns the supplied values
     */
    public static AsnSchemaComponentType createMockedComponentType(String tagName, String tag,
            String typeName, boolean isOptional, AsnSchemaType type)
    {
        final AsnSchemaComponentType mockedInstance = mock(AsnSchemaComponentType.class);
        when(mockedInstance.getTagName()).thenReturn(tagName);
        when(mockedInstance.getTag()).thenReturn(tag);
        when(mockedInstance.getTypeName()).thenReturn(typeName);
        when(mockedInstance.isOptional()).thenReturn(isOptional);
        when(mockedInstance.getType()).thenReturn(type);
        return mockedInstance;
    }

    /**
     * Creates mock {@link AsnSchemaComponentType} instances conforming to the {@code Document} type
     * definition in the test ASN.1 schema defined in the {@linkplain README.md} file
     *
     * @return mock instances conforming to schema
     */
    public static ImmutableList<AsnSchemaComponentType> createMockedAsnSchemaComponentTypesForDocument()
    {
        final ImmutableList.Builder<AsnSchemaComponentType> listBuilder = ImmutableList.builder();

        listBuilder.add(createMockedComponentType("header", "1", "Header", false, MockAsnSchemaType.getDocumentHeader()));
        listBuilder.add(createMockedComponentType("body", "2", "Body", false, MockAsnSchemaType.getDocumentBody()));
        listBuilder.add(createMockedComponentType("footer", "3", "Footer", false, MockAsnSchemaType.getDocumentFooter()));
        listBuilder.add(createMockedComponentType("dueDate", "4", "Date-Due", false, MockAsnSchemaType.getDocumentDueDate()));
        listBuilder.add(createMockedComponentType("version", "5", "SEQUENCE", false, MockAsnSchemaType.getDocumentVersion()));
        listBuilder.add(createMockedComponentType("description", "6", "SET", true, MockAsnSchemaType.getDocumentDescription()));

        return listBuilder.build();
    }

    /**
     * Creates mock {@link AsnSchemaComponentType} instances conforming to the {@code Body} type
     * definition in the test ASN.1 schema defined in the {@linkplain README.md} file
     *
     * @return mock instances conforming to schema
     */
    public static ImmutableList<AsnSchemaComponentType> createMockedAsnSchemaComponentTypesForBody()
    {
        final ImmutableList.Builder<AsnSchemaComponentType> listBuilder = ImmutableList.builder();
        listBuilder.add(createMockedComponentType("lastModified",
                "0",
                "ModificationMetadata",
                false,
                MockAsnSchemaType.getDocumentModificationMetadataLinked()));
        listBuilder.add(createMockedComponentType("prefix",
                "1",
                "Section-Note",
                true,
                MockAsnSchemaType.getDocumentSectionNote()));
        listBuilder.add(createMockedComponentType("content",
                "2",
                "Section-Main",
                false,
                MockAsnSchemaType.getDocumentSectionMain()));
        listBuilder.add(createMockedComponentType("suffix",
                "3",
                "Section-Note",
                true,
                MockAsnSchemaType.getDocumentSectionNote()));
        return listBuilder.build();
    }

    /**
     * Creates mock {@link AsnSchemaComponentType} instances conforming to the {@code Section-Main}
     * type definition in the test ASN.1 schema defined in the {@linkplain README.md} file
     *
     * @return mock instances conforming to schema
     */
    public static ImmutableList<AsnSchemaComponentType> createMockedAsnSchemaComponentTypesForSectionMain()
    {
        final ImmutableList.Builder<AsnSchemaComponentType> listBuilder = ImmutableList.builder();

        listBuilder.add(createMockedComponentType("text",
                "1",
                "OCTET STRING",
                true,
                MockAsnSchemaType.createMockedAsnSchemaType(AsnPrimitiveType.OCTET_STRING)));
        listBuilder.add(createMockedComponentType("paragraphs",
                "2",
                "Paragraph",
                false,
                MockAsnSchemaType.builder(AsnPrimitiveType.SEQUENCE_OF)
                    .setCollectionType(MockAsnSchemaType.getDocumentParagraph())
                .build()));
        listBuilder.add(createMockedComponentType("sections",
                "3",
                "SET",
                false,
                MockAsnSchemaType.builder(AsnPrimitiveType.SET_OF)
                    .setCollectionType(MockAsnSchemaType.
                            builder(AsnPrimitiveType.SET)
                            .addComponent("1",
                                    "number",
                                    false,
                                    MockAsnSchemaType.createMockedAsnSchemaType(AsnPrimitiveType.INTEGER))
                            .addComponent("2",
                                    "text",
                                    false,
                                    MockAsnSchemaType.createMockedAsnSchemaType(AsnPrimitiveType.OCTET_STRING))
                            .build()).build()));

         return listBuilder.build();
    }

    /**
     * Creates mock {@link AsnSchemaComponentType} instances conforming to the {@code Person} type
     * definition in the test ASN.1 schema defined in the {@linkplain README.md} file
     *
     * @return mock instances conforming to schema
     */
    public static ImmutableList<AsnSchemaComponentType> createMockedAsnSchemaComponentTypesForPerson()
    {
        final ImmutableList.Builder<AsnSchemaComponentType> listBuilder = ImmutableList.builder();
/* TODO ASN-138 - this was broken as part of ASN-126
        AsnSchemaComponentType componentType = createMockedComponentType("firstName",
                "1",
                "OCTET STRING",
                false);
        listBuilder.add(componentType);
        componentType = createMockedComponentType("lastName", "2", "OCTET STRING", false);
        listBuilder.add(componentType);
        AsnSchemaComponentTypeGenerated componentTypeGenerated = createMockedInstanceForGenerated(
                "title",
                "3",
                //"generated.Person.title",
                "ENUMERATED",
                "ENUMERATED { mr, mrs, ms, dr, rev }",
                true);
        listBuilder.add(componentTypeGenerated);
        componentType = createMockedComponentType("gender", "", "Gender", true);
        listBuilder.add(componentType);
        componentTypeGenerated = createMockedInstanceForGenerated("maritalStatus",
                "",
                //"generated.Person.maritalStatus",
                "CHOICE",
                "CHOICE { Married [0], Single [1] }",
                false);
        listBuilder.add(componentTypeGenerated);
*/
        return listBuilder.build();
    }
}
