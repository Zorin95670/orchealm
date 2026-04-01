package com.orchealm.orchealmapi.persistence.converter;

import com.orchealm.orchealmapi.model.error.ApiException;
import com.orchealm.orchealmapi.model.error.ErrorReference;
import com.orchealm.orchealmapi.persistence.attribute.TstzRange;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.commons.lang3.StringUtils;
import org.postgresql.util.PGobject;

import java.time.OffsetDateTime;

@Converter(autoApply = false)
public class TstzRangeConverter implements AttributeConverter<TstzRange, Object> {
    @Override
    public Object convertToDatabaseColumn(final TstzRange range) {
        try {
            PGobject pgObject = new PGobject();
            pgObject.setType("tstzrange");

            if (range == null || (range.getStart() == null && range.getEnd() == null)) {
                pgObject.setValue(null);
                return pgObject;
            }

            String start = "";
            if (range.getStart() != null) {
                start = range.getStart().toString();
            }

            String end = "";
            if (range.getEnd() != null) {
                end = range.getEnd().toString();
            }

            pgObject.setValue("[" + start + "," + end + ")");
            return pgObject;

        } catch (Exception e) {
            throw new ApiException(ErrorReference.internalServerError("Error converting TstzRange", e));
        }
    }

    @Override
    public TstzRange convertToEntityAttribute(final Object dbData) {
        String value = dbData.toString();
        if (StringUtils.isBlank(value)) {
            return null;
        }

        String[] parts = value.replace("[", "")
            .replace("(", "")
            .replace("]", "")
            .replace(")", "")
            .split(",");

        String startStr = parts[0].trim();
        String endStr = parts[1].trim();

        OffsetDateTime start = null;
        OffsetDateTime end = null;

        if (!StringUtils.isBlank(startStr)) {
            start = OffsetDateTime.parse(startStr);
        }

        if (!StringUtils.isBlank(endStr)) {
            end = OffsetDateTime.parse(endStr);
        }

        return new TstzRange(start, end);
    }
}
