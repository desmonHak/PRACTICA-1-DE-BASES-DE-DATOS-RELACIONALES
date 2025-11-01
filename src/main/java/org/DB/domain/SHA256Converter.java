package org.DB.domain;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Como Paciente usa la Clase SHA256, esta clase de Convertirse
 * para poder ser almacenada, deberemos poner
 * "@Convert(converter = SHA256Converter.class)" en el atributo
 * que use la clase SHA256 y queramos que pueda ser mepada por hibernate
 */
@Converter(autoApply = true)
public class SHA256Converter implements AttributeConverter<SHA256, String> {

    @Override
    public String convertToDatabaseColumn(SHA256 attribute) {
        return (attribute != null) ? attribute.getHash() : null;
    }

    @Override
    public SHA256 convertToEntityAttribute(String dbData) {
        return (dbData != null) ? new SHA256(dbData, true) : null;
    }
}
