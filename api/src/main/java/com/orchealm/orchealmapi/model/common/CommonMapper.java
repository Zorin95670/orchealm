package com.orchealm.orchealmapi.model.common;

import com.orchealm.orchealmapi.persistence.attribute.TstzRange;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class CommonMapper {
    @Named("toRange")
    public TstzRange toRange(final PeriodRecord period) {
        if (period == null) {
            return null;
        }

        return new TstzRange(period.start(), period.end());
    }

    @Named("toPeriodDTO")
    public PeriodDTO toPeriodDTO(final TstzRange period) {
        if (period == null) {
            return new PeriodDTO();
        }

        return new PeriodDTO(period.getStart(), period.getEnd());
    }
}
