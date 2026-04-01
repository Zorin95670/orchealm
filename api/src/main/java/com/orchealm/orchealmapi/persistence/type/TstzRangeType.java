package com.orchealm.orchealmapi.persistence.type;

import com.orchealm.orchealmapi.persistence.attribute.TstzRange;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.postgresql.util.PGobject;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Objects;

public class TstzRangeType implements UserType<TstzRange> {

    private static final DateTimeFormatter PG_TSTZ_FORMATTER = new DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd HH:mm:ss")
        .optionalStart()
        .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
        .optionalEnd()
        .appendOffset("+HH", "+00")
        .toFormatter();

    @Override
    public int getSqlType() {
        return Types.OTHER;  // ← the critical fix
    }

    @Override
    public Class<TstzRange> returnedClass() {
        return TstzRange.class;
    }

    @Override
    public void nullSafeSet(final PreparedStatement st,
                            final TstzRange value,
                            final int index,
                            final SharedSessionContractImplementor session) throws SQLException {
        PGobject pgObject = new PGobject();
        pgObject.setType("tstzrange");

        if (value == null || (value.getStart() == null && value.getEnd() == null)) {
            pgObject.setValue(null);
        } else {
            String start = value.getStart() != null ? value.getStart().toString() : "";
            String end = value.getEnd() != null ? value.getEnd().toString() : "";
            pgObject.setValue("[" + start + "," + end + ")");
        }

        st.setObject(index, pgObject, Types.OTHER);  // ← explicit type
    }

    @Override
    public TstzRange nullSafeGet(ResultSet rs, int position,
                                 SharedSessionContractImplementor session,
                                 Object owner) throws SQLException {
        Object obj = rs.getObject(position);
        if (obj == null) {
            return null;
        }

        String value = obj.toString();
        if (value.isBlank()) {
            return null;
        }

        String[] parts = value.replaceAll("[\\[\\]()\"]", "").split(",", 2);

        OffsetDateTime start = null;
        if (!StringUtils.isBlank(parts[0])) {
            start = OffsetDateTime.parse(parts[0].trim(), PG_TSTZ_FORMATTER);
        }

        OffsetDateTime end = null;
        if (!StringUtils.isBlank(parts[1])) {
            end = OffsetDateTime.parse(parts[1].trim(), PG_TSTZ_FORMATTER);
        }

        return new TstzRange(start, end);
    }

    @Override
    public boolean equals(TstzRange x, TstzRange y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(TstzRange x) {
        return Objects.hashCode(x);
    }

    @Override
    public TstzRange deepCopy(TstzRange value) {
        if (value == null) {
            return null;
        }
        return new TstzRange(value.getStart(), value.getEnd());
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(TstzRange value) {
        return (Serializable) value;
    }

    @Override
    public TstzRange assemble(Serializable cached, Object owner) {
        return (TstzRange) cached;
    }
}