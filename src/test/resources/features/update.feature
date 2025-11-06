Feature: Gestión de empleados

  Scenario: Actualizar datos de empleado
    Given el empleado "Luz Gómez" con cargo actual "RECEPCIONISTA"
    When el supervisor cambie el cargo a "Supervisor de recepción" y el salario a "2300000"
    Then el sistema debe guardar los cambios
    And registrar fecha y responsable de la actualización
