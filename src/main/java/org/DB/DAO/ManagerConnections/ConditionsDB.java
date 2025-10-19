package org.DB.DAO.ManagerConnections;

import org.DB.Exceptions.ExceptionsDB.TypeDataUnknown;

public class ConditionsDB {

    private ConditionsDBOperators condition;
    private String field;
    private Object value;

    public ConditionsDB(String field, ConditionsDBOperators condition, Object value) {
        this.condition = condition;
        this.field = field;

        // comprobamos si el tipo de dato es valido
        check_type_data(value);
        this.value = value;
    }

    public ConditionsDBOperators getCondition() {
        return condition;
    }

    public void setCondition(ConditionsDBOperators condition) {
        this.condition = condition;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }


    /**
     * Devuelve una representacion valida para SQL y comprueba que sea un numero,
     * una cadena o una fecha el tipo de dato
     * @param data_update dato que comprobar si es valido
     * @return cadena formateada con sintaxis valida para SQL
     * @throws TypeDataUnknown tipo de dato desconocido, o no encontrado en el rango especificado
     */
    public static String check_type_data(Object data_update) throws TypeDataUnknown {
        // obtener el tipo de objeto que es
        Class<?> type_data_class = data_update.getClass();

        // si es un numero, agregarlo sin mas:
        if (
                type_data_class == Integer.class){
            return (data_update).toString();
        } else if (type_data_class == String.class) {
            // Los string van entre comillas simples en SQL:
            return "'" + ((String)data_update) + "'";

        } else if (type_data_class == java.sql.Date.class) {
            // Convertir java.sql.Date a 'YYYY-MM-DD' en string con comillas simples
            return "'" + data_update.toString() + "'";
        }
        else {
            throw new TypeDataUnknown("Error, este tipo de dato no se puede usar: " + type_data_class);
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", this.field, this.condition, check_type_data(this.value));
    }
}
