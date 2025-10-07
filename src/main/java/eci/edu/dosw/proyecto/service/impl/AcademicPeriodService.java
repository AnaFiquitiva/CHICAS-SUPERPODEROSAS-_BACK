package eci.edu.dosw.proyecto.service.impl;

import org.springframework.stereotype.Service;

/**
 * Servicio para validar periodos académicos habilitados para cambios
 */
@Service
public class AcademicPeriodService {

    public Boolean isWithinChangePeriod() {

        return true;
    }
}
