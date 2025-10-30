# language: es
Característica: Gestión de empleados - Actualización de información laboral
  Como administrador
  Quiero modificar el cargo (rol), salario o datos de contacto de un empleado existente
  Para mantener la información del personal actualizada

  Escenario: Actualizar datos de empleado exitosamente por ID
    Dado que existe un empleado "Luz Gómez" con:
      | campo           | valor                                  |
      | id              | 550e8400-e29b-41d4-a716-446655440000  |
      | code            | EMP001                                 |
      | roles           | 2bed922b-bc39-4842-8297-4a3d9be34856  |
      | salary          | 1800000                                |
      | contactNumber   | 3001234567                             |
    Y el usuario tiene un token JWT de administrador con email "admin@hotel.com"
    Cuando el supervisor cambia los datos del empleado por ID:
      | campo           | valor                                  |
      | name            | Luz Gómez                              |
      | role            | a1541ebd-1323-43a7-8d16-30309bb6ade9  |
      | salary          | 2300000                                |
      | contactNumber   | 3009876543                             |
    Entonces el sistema debe guardar los cambios exitosamente
    Y el empleado debe tener el cargo actualizado a "a1541ebd-1323-43a7-8d16-30309bb6ade9"
    Y el empleado debe tener el salario actualizado a "2300000"
    Y el empleado debe tener el número de contacto actualizado a "3009876543"
    Y debe registrar la fecha de actualización
    Y debe registrar el responsable de la actualización como "admin@hotel.com"

  Escenario: Actualizar datos de empleado exitosamente por código
    Dado que existe un empleado "Luz Gómez" con:
      | campo           | valor                                  |
      | id              | 550e8400-e29b-41d4-a716-446655440001  |
      | code            | EMP002                                 |
      | roles           | 2bed922b-bc39-4842-8297-4a3d9be34856  |
      | salary          | 1800000                                |
      | contactNumber   | 3001234567                             |
    Y el usuario tiene un token JWT de administrador con email "supervisor@hotel.com"
    Cuando el supervisor cambia los datos del empleado por código "EMP002":
      | campo           | valor                                  |
      | name            | Luz Gómez                              |
      | role            | a1541ebd-1323-43a7-8d16-30309bb6ade9  |
      | salary          | 2300000                                |
      | contactNumber   | 3009876543                             |
    Entonces el sistema debe guardar los cambios exitosamente
    Y el empleado debe tener el cargo actualizado a "a1541ebd-1323-43a7-8d16-30309bb6ade9"
    Y el empleado debe tener el salario actualizado a "2300000"

  Escenario: Actualizar solo el cargo de un empleado
    Dado que existe un empleado con id "550e8400-e29b-41d4-a716-446655440002" y cargo "RECEPTIONIST"
    Y el usuario tiene un token JWT de administrador con email "admin@hotel.com"
    Cuando el supervisor cambia solo el cargo a "1f674d6a-7978-42e3-87fb-af0740c9e8d2" por ID
    Entonces el sistema debe guardar los cambios exitosamente
    Y el empleado debe tener el cargo actualizado a "1f674d6a-7978-42e3-87fb-af0740c9e8d2"
    Y debe mantener el salario anterior
    Y debe mantener el número de contacto anterior

  Escenario: Intentar actualizar un empleado inexistente por ID
    Dado que NO existe un empleado con id "999e8400-e29b-41d4-a716-446655440000"
    Y el usuario tiene un token JWT de administrador con email "admin@hotel.com"
    Cuando el supervisor intenta cambiar el cargo a "2bed922b-bc39-4842-8297-4a3d9be34856" por ID
    Entonces el sistema debe retornar un error NOT_FOUND
    Y el mensaje debe indicar "Empleado no encontrado"

  Escenario: Intentar actualizar un empleado inexistente por código
    Dado que NO existe un empleado con código "EMP999"
    Y el usuario tiene un token JWT de administrador con email "admin@hotel.com"
    Cuando el supervisor intenta cambiar el cargo por código "EMP999"
    Entonces el sistema debe retornar un error NOT_FOUND
    Y el mensaje debe indicar "Empleado no encontrado con código: EMP999"

  Escenario: Intentar actualizar empleado sin ser administrador
    Dado que existe un empleado con id "550e8400-e29b-41d4-a716-446655440003" y cargo "RECEPTIONIST"
    Y el usuario tiene un token JWT de STAFF con email "staff@hotel.com"
    Cuando el usuario intenta cambiar el salario a "2500000"
    Entonces el sistema debe retornar un error FORBIDDEN
    Y el mensaje debe indicar "Solo un ADMIN puede actualizar empleados"

  Escenario: Intentar actualizar empleado sin token de autorización
    Dado que existe un empleado con id "550e8400-e29b-41d4-a716-446655440004" y cargo "RECEPTIONIST"
    Y el usuario NO tiene un token de autorización
    Cuando el usuario intenta cambiar el salario a "2500000"
    Entonces el sistema debe retornar un error UNAUTHORIZED
    Y el mensaje debe indicar "Falta token Bearer"

  Escenario: Intentar actualizar empleado con token inválido
    Dado que existe un empleado con id "550e8400-e29b-41d4-a716-446655440005" y cargo "RECEPTIONIST"
    Y el usuario tiene un token JWT inválido
    Cuando el usuario intenta cambiar el salario a "2500000"
    Entonces el sistema debe retornar un error BAD_REQUEST
    Y el mensaje debe indicar "No se puede leer el JWT"

  Escenario: Intentar actualizar empleado con ID inválido
    Dado que el usuario tiene un token JWT de administrador con email "admin@hotel.com"
    Cuando el usuario intenta actualizar un empleado con ID inválido "no-es-uuid"
    Entonces el sistema debe retornar un error BAD_REQUEST
    Y el mensaje debe indicar "ID inválido"