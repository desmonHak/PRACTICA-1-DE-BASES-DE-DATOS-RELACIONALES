use centro_medico


db.Paciente.insertMany(
    [ 
        {
            email: "admin", 
            password: "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4",
            DNI: "48901940F",
            nombre: "Pedro",
            direccion: "C/ salvador",
            telefono: "645645373"
        },
        {
            email: "ana@gmail.com", 
            password: "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4",
            DNI: "67984567V",
            nombre: "Ana",
            direccion: "C/ mayor",
            telefono: "645645374"
        },
        {
            email: "user2", 
            password: "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4",
            DNI: "12345678Z",
            nombre: "Luis",
            direccion: "C/ menor",
            telefono: "645645375"
        },
    ]
)