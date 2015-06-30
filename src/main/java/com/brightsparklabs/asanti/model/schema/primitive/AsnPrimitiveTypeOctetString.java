package com.brightsparklabs.asanti.model.schema.primitive;

import com.brightsparklabs.asanti.model.schema.AsnBuiltinType;
import com.brightsparklabs.asanti.model.schema.typedefinition.AsnPrimitiveTypeVisitor;

/**
 * A type used to represent the primitive builtin type Octet String within ASN.1 This class is used
 * as a 'key' for the Visitor.
 *
 * @author brightSPARK Labs
 */
public class AsnPrimitiveTypeOctetString implements AsnPrimitiveType
{
    // -------------------------------------------------------------------------
    // IMPLEMENTATION: AsnPrimitiveType
    // -------------------------------------------------------------------------

    @Override
    public AsnBuiltinType getBuiltinType()
    {
        return AsnBuiltinType.OctetString;
    }

    @Override
    public Object visit(AsnPrimitiveTypeVisitor<?> visitor)
    {
        return visitor.visit(this);
    }
}
