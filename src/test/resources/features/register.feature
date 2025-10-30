# language: es
Característica: Registrar nuevo empleado

  Como administrador
  Quiero registrar nuevos empleados con su información laboral
  Para gestionar sus roles, horarios y permisos

  Escenario: Registrar nuevo empleado exitosamente
    Dado el hotel "Santa Marta Resort"
    Cuando el administrador ingresa identificación "102030000", tipo_identificacion "Cédula", rol "RECEPCIONISTA", nombre "Luz Gómez", contacto "3001234567", correo "luz@hotel.com", contraseña "abcd1234", salario "1800000"
    Entonces el sistema debe crear el registro con código "EMP-XXXX"
    Y mostrar el mensaje "Empleado registrado exitosamente"

  Escenario: No permite registrar con email duplicado (devuelve 400)
    Dado el hotel "Santa Marta Resort"
    Y existe un empleado previamente con email "dup@hotel.com" e identificación "102039999"
    Cuando intenta registrar identificación "102039999"  tipo_identificacion "Cédula"  rol "RECEPCIONISTA"  nombre "Nombre Duplicado"  contacto "3000000000"  correo "dup@hotel.com"  contraseña "abcd1234"  salario "1800000"
    Entonces la respuesta es 400
    Y el cuerpo contiene "El email ya existe"

  Escenario: No permite registrar con identificación duplicada (devuelve 400)
    Dado el hotel "Santa Marta Resort"
    Y existe un empleado previamente con email "iddup@hotel.com" e identificación "102039998"
    Cuando intenta registrar identificación "102039998"  tipo_identificacion "Cédula"  rol "RECEPCIONISTA"  nombre "Otro Nombre"  contacto "3002222222"  correo "nuevo@hotel.com"  contraseña "abcd1234"  salario "1800000"
    Entonces la respuesta es 400
    Y el cuerpo contiene "Número de identificación ya existente"

  Escenario: Falla por tipo de identificación inexistente (devuelve 400)
    Dado el hotel "Santa Marta Resort"
    Cuando intenta registrar con typeId "11111111-1111-1111-1111-111111111111" identificación "102040000"  rol "RECEPCIONISTA"  nombre "Tipo Inexistente"  contacto "3003333333"  correo "tipo404@hotel.com"  contraseña "abcd1234"  salario "1800000"
    Entonces la respuesta es 400
    Y el cuerpo contiene "Tipo de identificación no encontrado"
