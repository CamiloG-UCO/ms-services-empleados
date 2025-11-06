# language: es
Característica: Eliminar empleado

  Como usuario de RRHH
  Quiero eliminar empleados que no tengan dependencias activas
  Para mantener actualizada la base de datos del hotel

  Escenario: Eliminar empleado sin dependencias activas
    Dado el empleado "EMP-1234" sin tareas ni turnos activos
    Cuando el usuario de RRHH confirme la acción con "SI, ELIMINAR"
    Entonces el sistema debe eliminar el registro "EMP-1234" y mostrar el mensaje "Empleado eliminado exitosamente"
