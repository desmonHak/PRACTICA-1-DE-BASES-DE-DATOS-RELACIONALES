package org.DB.domain;

import org.DB.domain.DNI;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * autoApply = true para que Hibernate lo aplique automáticamente
 * al tipo DNI sin necesidad de anotarlo en cada clase
 */
@Converter(autoApply = false)
public class DNIConverter implements AttributeConverter<DNI, String> {
    @Override
    public String convertToDatabaseColumn(DNI dni) {
        System.out.println("1 >_>"+ dni);
        return (dni == null) ? null : dni.toString();
    }

    @Override
    public DNI convertToEntityAttribute(String dbData) {
        System.out.println("1 >_>"+ dbData);
        return (dbData == null || dbData.isEmpty()) ? null : new DNI(dbData);
    }
}
