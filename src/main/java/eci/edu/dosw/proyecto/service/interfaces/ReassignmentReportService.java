/**
 * Service interface for reassignment reporting
 */
package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.dto.ReassignmentReportRequest;
import eci.edu.dosw.proyecto.dto.ReassignmentReportResponse;

public interface ReassignmentReportService {

    /**
     * Generates comprehensive reassignment statistics report
     * @param request Report configuration with filters
     * @return Reassignment statistics report
     */
    ReassignmentReportResponse generateReassignmentReport(ReassignmentReportRequest request);
    ReassignmentReportResponse generateCurrentPeriodReport();
}


