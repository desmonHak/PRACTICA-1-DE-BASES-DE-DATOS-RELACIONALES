use centro_medico


db.Citas.insertMany(
    [ 
        { 
            dni: "48901940F", 
            numero_cita: 1, 
            fecha_cita: ISODate("2025-10-20T00:00:00Z"), 
            especialidad: "Cirugia"
        },
        { 
            dni: "67984567V", 
            numero_cita: 2, 
            fecha_cita: ISODate("2025-12-12T00:00:00Z"), 
            especialidad: "Cardiologia"
        },
    ]
)