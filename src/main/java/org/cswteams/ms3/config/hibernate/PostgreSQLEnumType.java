package org.cswteams.ms3.config.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.DynamicParameterizedType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

/**
 * Hibernate UserType for PostgreSQL enum columns.
 * Ensures JDBC binds enum values as OTHER so PG enum columns accept inserts/updates.
 */
public class PostgreSQLEnumType implements UserType, DynamicParameterizedType {

    private Class<? extends Enum> enumClass;

    @Override
    public void setParameterValues(Properties parameters) {
        Object parameterType = parameters.get(PARAMETER_TYPE);
        if (parameterType instanceof ParameterType) {
            @SuppressWarnings("unchecked")
            Class<? extends Enum> enumType = (Class<? extends Enum>) ((ParameterType) parameterType).getReturnedClass();
            this.enumClass = enumType;
        }
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.OTHER};
    }

    @Override
    public Class<?> returnedClass() {
        return enumClass == null ? Enum.class : enumClass;
    }

    @Override
    public boolean equals(Object x, Object y) {
        return x == y || (x != null && x.equals(y));
    }

    @Override
    public int hashCode(Object x) {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs,
                              String[] names,
                              SharedSessionContractImplementor session,
                              Object owner) throws SQLException {
        String value = rs.getString(names[0]);
        if (value == null || enumClass == null) {
            return null;
        }
        return Enum.valueOf(enumClass, value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st,
                            Object value,
                            int index,
                            SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            st.setObject(index, value.toString(), Types.OTHER);
        }
    }

    @Override
    public Object deepCopy(Object value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) {
        return original;
    }
}
