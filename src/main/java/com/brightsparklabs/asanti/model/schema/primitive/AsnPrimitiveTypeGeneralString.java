package com.brightsparklabs.asanti.model.schema.primitive;

import com.brightsparklabs.assam.schema.AsnBuiltinType;
import com.brightsparklabs.assam.schema.AsnPrimitiveType;
import com.brightsparklabs.assam.schema.AsnPrimitiveTypeVisitor;

/**
 * A type used to represent the primitive builtin type General String within ASN.1 This class is
 * used as a 'key' for the Visitor.
 *
 * @author brightSPARK Labs
 */
public class AsnPrimitiveTypeGeneralString implements AsnPrimitiveType.GeneralString
{
    // -------------------------------------------------------------------------
    // IMPLEMENTATION: AsnPrimitiveType
    // -------------------------------------------------------------------------

    @Override
    public AsnBuiltinType getBuiltinType()
    {
        return AsnBuiltinType.GeneralString;
    }

    @Override
    public Object accept(AsnPrimitiveTypeVisitor<?> visitor)
    {
        return visitor.visit(this);
    }
}
