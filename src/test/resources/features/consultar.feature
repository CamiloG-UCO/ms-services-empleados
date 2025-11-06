# language: es
Característica: Gestión de empleados

  Como administrador
  Quiero consultar los datos de un empleado por su código
  Para ver su ficha y descargar el archivo

  Escenario: Consultar información de un empleado
    Dado existe el empleado con código "EMP-1234"
    Cuando el usuario busque "EMP-1234"
    Entonces el sistema debe mostrar la ficha con identificacion "102030", tipo_identificacion "Cédula", rol "SUPERVISOR_RECEPCION", nombre "Luz Gómez", contacto "300 7654321", correo "luz@hotel.com", hotel_codigo "HOT-2025-010", salario "2.300.000 COP" y permitir descargar "ficha_EMP-1234.pdf"

