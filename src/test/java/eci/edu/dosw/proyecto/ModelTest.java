package eci.edu.dosw.proyecto;
import eci.edu.dosw.proyecto.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@DisplayName("Model Tests")
class ModelTest {

    @Nested
    @DisplayName("Student Tests")
    class StudentTests {

        @Test
        @DisplayName("Happy Path - Crear estudiante completo")
        void testCreateStudentHappyPath() {
            Student student = new Student();
            student.setId("1");
            student.setStudentCode("2021101234");
            student.setName("Juan Pérez");
            student.setEmail("juan@email.com");
            student.setInstitutionalEmail("juan.perez@escuelaing.edu.co");
            student.setType(StudentType.UNDERGRADUATE);
            student.setProgram("Ingeniería de Sistemas");
            student.setCurrentSemester(5);
            student.setStatus(AcademicStatus.GREEN);
            student.setAcademicPlans(new ArrayList<>());
            student.setCurrentEnrollments(new ArrayList<>());

            assertAll("student",
                    () -> assertEquals("1", student.getId()),
                    () -> assertEquals("2021101234", student.getStudentCode()),
                    () -> assertEquals("Juan Pérez", student.getName()),
                    () -> assertEquals("juan@email.com", student.getEmail()),
                    () -> assertEquals(StudentType.UNDERGRADUATE, student.getType()),
                    () -> assertEquals(5, student.getCurrentSemester()),
                    () -> assertEquals(AcademicStatus.GREEN, student.getStatus())
            );
        }

        @Test
        @DisplayName("No Happy - Estudiante con datos nulos")
        void testStudentWithNullValues() {
            Student student = new Student();

            assertAll("null student",
                    () -> assertNull(student.getId()),
                    () -> assertNull(student.getStudentCode()),
                    () -> assertNull(student.getName()),
                    () -> assertNull(student.getType()),
                    () -> assertNull(student.getCurrentSemester())
            );
        }

        @Test
        @DisplayName("No Happy - Estudiante con semestre inválido")
        void testStudentWithInvalidSemester() {
            Student student = new Student();
            student.setCurrentSemester(-1);

            assertTrue(student.getCurrentSemester() < 0, "Semestre no debe ser negativo");
        }
    }

    @Nested
    @DisplayName("AcademicPlan Tests")
    class AcademicPlanTests {

        @Test
        @DisplayName("Happy Path - Crear plan académico activo")
        void testCreateAcademicPlanHappyPath() {
            AcademicPlan plan = new AcademicPlan();
            plan.setId("plan1");
            plan.setName("Plan 2024");
            plan.setStudentType(StudentType.UNDERGRADUATE);
            plan.setProgram("Ingeniería de Sistemas");
            plan.setActive(true);

            List<Subject> subjects = new ArrayList<>();
            Subject subject = new Subject();
            subject.setCode("ISOFT101");
            subjects.add(subject);
            plan.setAvailableSubjects(subjects);

            assertAll("academic plan",
                    () -> assertEquals("plan1", plan.getId()),
                    () -> assertEquals("Plan 2024", plan.getName()),
                    () -> assertTrue(plan.getActive()),
                    () -> assertEquals(1, plan.getAvailableSubjects().size())
            );
        }

        @Test
        @DisplayName("No Happy - Plan académico sin materias")
        void testAcademicPlanWithoutSubjects() {
            AcademicPlan plan = new AcademicPlan();
            plan.setActive(true);

            assertNull(plan.getAvailableSubjects(), "El plan no tiene materias asignadas");
        }

        @Test
        @DisplayName("No Happy - Plan académico inactivo")
        void testInactiveAcademicPlan() {
            AcademicPlan plan = new AcademicPlan();
            plan.setActive(false);

            assertFalse(plan.getActive(), "El plan está inactivo");
        }
    }

    @Nested
    @DisplayName("Subject Tests")
    class SubjectTests {

        @Test
        @DisplayName("Happy Path - Crear materia completa")
        void testCreateSubjectHappyPath() {
            Subject subject = new Subject();
            subject.setId("subj1");
            subject.setCode("ISOFT101");
            subject.setName("Programación I");
            subject.setCredits(3);
            subject.setMandatory(true);
            subject.setGroups(new ArrayList<>());
            subject.setAcademicPlanIds(Arrays.asList("plan1", "plan2"));

            assertAll("subject",
                    () -> assertEquals("subj1", subject.getId()),
                    () -> assertEquals("ISOFT101", subject.getCode()),
                    () -> assertEquals(3, subject.getCredits()),
                    () -> assertTrue(subject.getMandatory()),
                    () -> assertEquals(2, subject.getAcademicPlanIds().size())
            );
        }

        @Test
        @DisplayName("No Happy - Materia con créditos negativos")
        void testSubjectWithNegativeCredits() {
            Subject subject = new Subject();
            subject.setCredits(-5);

            assertTrue(subject.getCredits() < 0, "Los créditos no pueden ser negativos");
        }

        @Test
        @DisplayName("No Happy - Materia sin grupos")
        void testSubjectWithoutGroups() {
            Subject subject = new Subject();
            subject.setName("Materia sin grupos");

            assertNull(subject.getGroups(), "La materia no tiene grupos asignados");
        }
    }

    @Nested
    @DisplayName("Group Tests")
    class GroupTests {

        @Test
        @DisplayName("Happy Path - Crear grupo con capacidad")
        void testCreateGroupHappyPath() {
            Group group = new Group();
            group.setId("group1");
            group.setSubjectId("subj1");
            group.setGroupCode("G01");
            group.setProfessor("Dr. García");
            group.setMaxCapacity(30);
            group.setCurrentEnrollment(25);
            group.setWaitingListCount(3);
            group.setActive(true);

            assertAll("group",
                    () -> assertEquals("group1", group.getId()),
                    () -> assertEquals("G01", group.getGroupCode()),
                    () -> assertEquals(30, group.getMaxCapacity()),
                    () -> assertEquals(25, group.getCurrentEnrollment()),
                    () -> assertTrue(group.getActive())
            );
        }

        @Test
        @DisplayName("No Happy - Grupo con sobre-cupo")
        void testGroupOverCapacity() {
            Group group = new Group();
            group.setMaxCapacity(30);
            group.setCurrentEnrollment(35);

            assertTrue(group.getCurrentEnrollment() > group.getMaxCapacity(),
                    "El grupo está sobre su capacidad máxima");
        }

        @Test
        @DisplayName("No Happy - Grupo inactivo")
        void testInactiveGroup() {
            Group group = new Group();
            group.setActive(false);

            assertFalse(group.getActive(), "El grupo está inactivo");
        }
    }

    @Nested
    @DisplayName("Schedule Tests")
    class ScheduleTests {

        @Test
        @DisplayName("Happy Path - Horarios sin conflicto")
        void testScheduleNoConflict() {
            DaySchedule day1 = new DaySchedule();
            day1.setDay(DayOfWeek.MONDAY);
            day1.setStartTime("08:00");
            day1.setEndTime("10:00");

            DaySchedule day2 = new DaySchedule();
            day2.setDay(DayOfWeek.MONDAY);
            day2.setStartTime("10:00");
            day2.setEndTime("12:00");

            Schedule schedule1 = new Schedule();
            schedule1.setDaySchedules(List.of(day1));

            Schedule schedule2 = new Schedule();
            schedule2.setDaySchedules(List.of(day2));

            assertFalse(schedule1.hasConflict(schedule2),
                    "No debe haber conflicto entre horarios consecutivos");
        }

        @Test
        @DisplayName("No Happy - Horarios con conflicto")
        void testScheduleWithConflict() {
            DaySchedule day1 = new DaySchedule();
            day1.setDay(DayOfWeek.TUESDAY);
            day1.setStartTime("08:00");
            day1.setEndTime("10:00");

            DaySchedule day2 = new DaySchedule();
            day2.setDay(DayOfWeek.TUESDAY);
            day2.setStartTime("09:00");
            day2.setEndTime("11:00");

            Schedule schedule1 = new Schedule();
            schedule1.setDaySchedules(List.of(day1));

            Schedule schedule2 = new Schedule();
            schedule2.setDaySchedules(List.of(day2));

            assertTrue(schedule1.hasConflict(schedule2),
                    "Debe detectar conflicto de horarios solapados");
        }

        @Test
        @DisplayName("Happy Path - Horarios en días diferentes")
        void testScheduleDifferentDays() {
            DaySchedule monday = new DaySchedule();
            monday.setDay(DayOfWeek.MONDAY);
            monday.setStartTime("08:00");
            monday.setEndTime("10:00");

            DaySchedule tuesday = new DaySchedule();
            tuesday.setDay(DayOfWeek.TUESDAY);
            tuesday.setStartTime("08:00");
            tuesday.setEndTime("10:00");

            assertFalse(monday.hasTimeConflict(tuesday),
                    "Horarios en días diferentes no deben tener conflicto");
        }

        @Test
        @DisplayName("No Happy - Horarios mismo día sin solapamiento (antes)")
        void testSameDayNoOverlapBefore() {
            DaySchedule schedule1 = new DaySchedule();
            schedule1.setDay(DayOfWeek.WEDNESDAY);
            schedule1.setStartTime("08:00");
            schedule1.setEndTime("10:00");

            DaySchedule schedule2 = new DaySchedule();
            schedule2.setDay(DayOfWeek.WEDNESDAY);
            schedule2.setStartTime("10:00");
            schedule2.setEndTime("12:00");

            assertFalse(schedule1.hasTimeConflict(schedule2),
                    "Horarios consecutivos no deben tener conflicto");
        }

        @Test
        @DisplayName("No Happy - Horarios mismo día sin solapamiento (después)")
        void testSameDayNoOverlapAfter() {
            DaySchedule schedule1 = new DaySchedule();
            schedule1.setDay(DayOfWeek.THURSDAY);
            schedule1.setStartTime("14:00");
            schedule1.setEndTime("16:00");

            DaySchedule schedule2 = new DaySchedule();
            schedule2.setDay(DayOfWeek.THURSDAY);
            schedule2.setStartTime("10:00");
            schedule2.setEndTime("12:00");

            assertFalse(schedule1.hasTimeConflict(schedule2),
                    "Horarios no consecutivos sin solapamiento");
        }

        @Test
        @DisplayName("Happy Path - Conflicto total (horario contenido)")
        void testTotalOverlap() {
            DaySchedule schedule1 = new DaySchedule();
            schedule1.setDay(DayOfWeek.FRIDAY);
            schedule1.setStartTime("09:00");
            schedule1.setEndTime("12:00");

            DaySchedule schedule2 = new DaySchedule();
            schedule2.setDay(DayOfWeek.FRIDAY);
            schedule2.setStartTime("10:00");
            schedule2.setEndTime("11:00");

            assertTrue(schedule1.hasTimeConflict(schedule2),
                    "Debe detectar conflicto cuando un horario contiene a otro");
        }

        @Test
        @DisplayName("Happy Path - Conflicto parcial (inicio)")
        void testPartialOverlapStart() {
            DaySchedule schedule1 = new DaySchedule();
            schedule1.setDay(DayOfWeek.MONDAY);
            schedule1.setStartTime("08:00");
            schedule1.setEndTime("10:00");

            DaySchedule schedule2 = new DaySchedule();
            schedule2.setDay(DayOfWeek.MONDAY);
            schedule2.setStartTime("09:30");
            schedule2.setEndTime("11:00");

            assertTrue(schedule1.hasTimeConflict(schedule2),
                    "Debe detectar conflicto con solapamiento parcial al inicio");
        }

        @Test
        @DisplayName("Happy Path - Conflicto parcial (final)")
        void testPartialOverlapEnd() {
            DaySchedule schedule1 = new DaySchedule();
            schedule1.setDay(DayOfWeek.TUESDAY);
            schedule1.setStartTime("10:00");
            schedule1.setEndTime("12:00");

            DaySchedule schedule2 = new DaySchedule();
            schedule2.setDay(DayOfWeek.TUESDAY);
            schedule2.setStartTime("08:00");
            schedule2.setEndTime("10:30");

            assertTrue(schedule1.hasTimeConflict(schedule2),
                    "Debe detectar conflicto con solapamiento parcial al final");
        }

        @Test
        @DisplayName("Happy Path - Horarios exactamente iguales")
        void testExactSameSchedule() {
            DaySchedule schedule1 = new DaySchedule();
            schedule1.setDay(DayOfWeek.WEDNESDAY);
            schedule1.setStartTime("14:00");
            schedule1.setEndTime("16:00");

            DaySchedule schedule2 = new DaySchedule();
            schedule2.setDay(DayOfWeek.WEDNESDAY);
            schedule2.setStartTime("14:00");
            schedule2.setEndTime("16:00");

            assertTrue(schedule1.hasTimeConflict(schedule2),
                    "Horarios idénticos deben tener conflicto");
        }

        @Test
        @DisplayName("No Happy - Días diferentes con misma hora")
        void testDifferentDaysSameTime() {
            DaySchedule schedule1 = new DaySchedule();
            schedule1.setDay(DayOfWeek.MONDAY);
            schedule1.setStartTime("10:00");
            schedule1.setEndTime("12:00");

            DaySchedule schedule2 = new DaySchedule();
            schedule2.setDay(DayOfWeek.FRIDAY);
            schedule2.setStartTime("10:00");
            schedule2.setEndTime("12:00");

            assertFalse(schedule1.hasTimeConflict(schedule2),
                    "Días diferentes nunca deben tener conflicto aunque las horas coincidan");
        }

        @Test
        @DisplayName("No Happy - Horario termina justo cuando empieza el otro")
        void testAdjacentSchedules() {
            DaySchedule schedule1 = new DaySchedule();
            schedule1.setDay(DayOfWeek.THURSDAY);
            schedule1.setStartTime("08:00");
            schedule1.setEndTime("10:00");

            DaySchedule schedule2 = new DaySchedule();
            schedule2.setDay(DayOfWeek.THURSDAY);
            schedule2.setStartTime("10:00");
            schedule2.setEndTime("12:00");

            assertFalse(schedule1.hasTimeConflict(schedule2),
                    "Horarios adyacentes no deben tener conflicto");
        }

        @Test
        @DisplayName("Multiple schedules in Schedule object")
        void testMultipleDaySchedules() {
            DaySchedule mon1 = new DaySchedule();
            mon1.setDay(DayOfWeek.MONDAY);
            mon1.setStartTime("08:00");
            mon1.setEndTime("10:00");

            DaySchedule wed1 = new DaySchedule();
            wed1.setDay(DayOfWeek.WEDNESDAY);
            wed1.setStartTime("14:00");
            wed1.setEndTime("16:00");

            DaySchedule mon2 = new DaySchedule();
            mon2.setDay(DayOfWeek.MONDAY);
            mon2.setStartTime("09:00");
            mon2.setEndTime("11:00");

            Schedule schedule1 = new Schedule();
            schedule1.setDaySchedules(List.of(mon1, wed1));

            Schedule schedule2 = new Schedule();
            schedule2.setDaySchedules(List.of(mon2));

            assertTrue(schedule1.hasConflict(schedule2),
                    "Debe detectar conflicto en horarios múltiples");
        }
    }

    @Nested
    @DisplayName("ChangeRequest Tests")
    class ChangeRequestTests {

        @Test
        @DisplayName("Happy Path - Solicitud de cambio de grupo")
        void testGroupChangeRequestHappyPath() {
            ChangeRequest request = new ChangeRequest();
            request.setId("req1");
            request.setRequestNumber("REQ-2024-001");
            request.setStudentId("student1");
            request.setCreationDate(LocalDateTime.now());
            request.setType(RequestType.GROUP_CHANGE);
            request.setStatus(RequestStatus.PENDING);
            request.setPriority(1);
            request.setCurrentSubjectId("subj1");
            request.setCurrentGroupId("group1");
            request.setTargetGroupId("group2");

            assertAll("change request",
                    () -> assertEquals("REQ-2024-001", request.getRequestNumber()),
                    () -> assertEquals(RequestType.GROUP_CHANGE, request.getType()),
                    () -> assertEquals(RequestStatus.PENDING, request.getStatus()),
                    () -> assertEquals(1, request.getPriority()),
                    () -> assertNotNull(request.getCreationDate())
            );
        }

        @Test
        @DisplayName("Happy Path - Solicitud de cambio de plan")
        void testPlanChangeRequestHappyPath() {
            ChangeRequest request = new ChangeRequest();
            request.setType(RequestType.PLAN_CHANGE);

            PlanChangeDetail detail = new PlanChangeDetail();
            detail.setAction("ADD");
            detail.setSubjectId("newSubject");
            detail.setReason("Electiva requerida");

            request.setPlanChanges(List.of(detail));

            assertAll("plan change request",
                    () -> assertEquals(RequestType.PLAN_CHANGE, request.getType()),
                    () -> assertEquals(1, request.getPlanChanges().size()),
                    () -> assertEquals("ADD", request.getPlanChanges().get(0).getAction())
            );
        }

        @Test
        @DisplayName("No Happy - Solicitud rechazada")
        void testRejectedRequest() {
            ChangeRequest request = new ChangeRequest();
            request.setStatus(RequestStatus.REJECTED);
            request.setObservations("No cumple con los requisitos");

            assertEquals(RequestStatus.REJECTED, request.getStatus(),
                    "La solicitud debe estar en estado REJECTED");
        }

        @Test
        @DisplayName("No Happy - Solicitud sin información completa")
        void testIncompleteRequest() {
            ChangeRequest request = new ChangeRequest();
            request.setType(RequestType.GROUP_CHANGE);
            // Falta información requerida

            assertAll("incomplete request",
                    () -> assertNull(request.getCurrentGroupId()),
                    () -> assertNull(request.getTargetGroupId()),
                    () -> assertNull(request.getStudentId())
            );
        }
    }

    @Nested
    @DisplayName("AcademicPeriod Tests")
    class AcademicPeriodTests {

        @Test
        @DisplayName("Happy Path - Periodo académico activo")
        void testActiveAcademicPeriod() {
            AcademicPeriod period = new AcademicPeriod();
            period.setId("per1");
            period.setPeriodName("2024-1");
            period.setStartDate(LocalDateTime.of(2024, 1, 15, 0, 0));
            period.setEndDate(LocalDateTime.of(2024, 6, 30, 23, 59));
            period.setIsActive(true);
            period.setAllowGroupChanges(true);
            period.setAllowSubjectChanges(true);
            period.setMaxRequestsPerStudent(3);
            period.setCreatedBy("admin");

            assertAll("academic period",
                    () -> assertEquals("2024-1", period.getPeriodName()),
                    () -> assertTrue(period.getIsActive()),
                    () -> assertTrue(period.getAllowGroupChanges()),
                    () -> assertEquals(3, period.getMaxRequestsPerStudent())
            );
        }

        @Test
        @DisplayName("No Happy - Periodo con fechas inválidas")
        void testPeriodWithInvalidDates() {
            AcademicPeriod period = new AcademicPeriod();
            period.setStartDate(LocalDateTime.of(2024, 6, 30, 0, 0));
            period.setEndDate(LocalDateTime.of(2024, 1, 15, 0, 0));

            assertTrue(period.getStartDate().isAfter(period.getEndDate()),
                    "La fecha de inicio no puede ser después de la fecha de fin");
        }

        @Test
        @DisplayName("No Happy - Periodo inactivo con cambios deshabilitados")
        void testInactivePeriod() {
            AcademicPeriod period = new AcademicPeriod();
            period.setIsActive(false);
            period.setAllowGroupChanges(false);
            period.setAllowSubjectChanges(false);

            assertAll("inactive period",
                    () -> assertFalse(period.getIsActive()),
                    () -> assertFalse(period.getAllowGroupChanges()),
                    () -> assertFalse(period.getAllowSubjectChanges())
            );
        }
    }

    @Nested
    @DisplayName("Enrollment Tests")
    class EnrollmentTests {

        @Test
        @DisplayName("Happy Path - Inscripción activa")
        void testActiveEnrollment() {
            Enrollment enrollment = new Enrollment();
            enrollment.setId("enr1");
            enrollment.setStudentId("student1");
            enrollment.setSubjectId("subj1");
            enrollment.setGroupId("group1");
            enrollment.setEnrollmentDate(LocalDateTime.now());
            enrollment.setStatus(EnrollmentStatus.ACTIVE);

            assertAll("enrollment",
                    () -> assertEquals("enr1", enrollment.getId()),
                    () -> assertEquals("student1", enrollment.getStudentId()),
                    () -> assertEquals(EnrollmentStatus.ACTIVE, enrollment.getStatus()),
                    () -> assertNotNull(enrollment.getEnrollmentDate())
            );
        }

        @Test
        @DisplayName("No Happy - Inscripción cancelada")
        void testCancelledEnrollment() {
            Enrollment enrollment = new Enrollment();
            enrollment.setStatus(EnrollmentStatus.CANCELLED);

            assertEquals(EnrollmentStatus.CANCELLED, enrollment.getStatus(),
                    "La inscripción debe estar cancelada");
        }

        @Test
        @DisplayName("Happy Path - Inscripción completada")
        void testCompletedEnrollment() {
            Enrollment enrollment = new Enrollment();
            enrollment.setStatus(EnrollmentStatus.COMPLETED);

            assertEquals(EnrollmentStatus.COMPLETED, enrollment.getStatus(),
                    "La inscripción debe estar completada");
        }
    }

    @Nested
    @DisplayName("RequestHistory Tests")
    class RequestHistoryTests {

        @Test
        @DisplayName("Happy Path - Registro de historial completo")
        void testRequestHistoryHappyPath() {
            RequestHistory history = new RequestHistory();
            history.setTimestamp(LocalDateTime.now());
            history.setAction("APROBADO");
            history.setDescription("Solicitud aprobada por coordinador");
            history.setUserId("coord1");
            history.setUserRole("COORDINATOR");

            assertAll("request history",
                    () -> assertNotNull(history.getTimestamp()),
                    () -> assertEquals("APROBADO", history.getAction()),
                    () -> assertEquals("COORDINATOR", history.getUserRole())
            );
        }

        @Test
        @DisplayName("No Happy - Historial sin información de usuario")
        void testHistoryWithoutUserInfo() {
            RequestHistory history = new RequestHistory();
            history.setAction("RECHAZADO");

            assertAll("incomplete history",
                    () -> assertNull(history.getUserId()),
                    () -> assertNull(history.getUserRole())
            );
        }
    }

    @Nested
    @DisplayName("PlanChangeDetail Tests")
    class PlanChangeDetailTests {

        @Test
        @DisplayName("Happy Path - Agregar materia al plan")
        void testAddSubjectToPlan() {
            PlanChangeDetail detail = new PlanChangeDetail();
            detail.setAction("ADD");
            detail.setSubjectId("newSubj");
            detail.setGroupId("newGroup");
            detail.setReason("Materia electiva aprobada");

            assertAll("add subject",
                    () -> assertEquals("ADD", detail.getAction()),
                    () -> assertEquals("newSubj", detail.getSubjectId()),
                    () -> assertNotNull(detail.getReason())
            );
        }

        @Test
        @DisplayName("Happy Path - Reemplazar materia en el plan")
        void testReplaceSubjectInPlan() {
            PlanChangeDetail detail = new PlanChangeDetail();
            detail.setAction("REPLACE");
            detail.setSubjectId("oldSubj");
            detail.setReplacementSubjectId("newSubj");
            detail.setReplacementGroupId("newGroup");
            detail.setReason("Homologación aprobada");

            assertAll("replace subject",
                    () -> assertEquals("REPLACE", detail.getAction()),
                    () -> assertNotNull(detail.getReplacementSubjectId()),
                    () -> assertNotNull(detail.getReplacementGroupId())
            );
        }

        @Test
        @DisplayName("No Happy - Acción sin justificación")
        void testActionWithoutReason() {
            PlanChangeDetail detail = new PlanChangeDetail();
            detail.setAction("REMOVE");
            detail.setSubjectId("subj1");

            assertNull(detail.getReason(), "No se proporcionó justificación");
        }
    }

    @Nested
    @DisplayName("Enum Tests")
    class EnumTests {

        @Test
        @DisplayName("StudentType enum - Valores correctos")
        void testStudentTypeEnum() {
            assertEquals(3, StudentType.values().length);
            assertNotNull(StudentType.valueOf("UNDERGRADUATE"));
            assertNotNull(StudentType.valueOf("POSTGRADUATE"));
            assertNotNull(StudentType.valueOf("MASTER"));
        }

        @Test
        @DisplayName("RequestType enum - Valores correctos")
        void testRequestTypeEnum() {
            assertEquals(4, RequestType.values().length);
            assertNotNull(RequestType.valueOf("GROUP_CHANGE"));
            assertNotNull(RequestType.valueOf("SUBJECT_CHANGE"));
            assertNotNull(RequestType.valueOf("PLAN_CHANGE"));
            assertNotNull(RequestType.valueOf("NEW_ENROLLMENT"));
        }

        @Test
        @DisplayName("RequestStatus enum - Valores correctos")
        void testRequestStatusEnum() {
            assertEquals(5, RequestStatus.values().length);
            assertNotNull(RequestStatus.valueOf("PENDING"));
            assertNotNull(RequestStatus.valueOf("APPROVED"));
            assertNotNull(RequestStatus.valueOf("REJECTED"));
        }

        @Test
        @DisplayName("AcademicStatus enum - Valores correctos")
        void testAcademicStatusEnum() {
            assertEquals(3, AcademicStatus.values().length);
            assertNotNull(AcademicStatus.valueOf("GREEN"));
            assertNotNull(AcademicStatus.valueOf("BLUE"));
            assertNotNull(AcademicStatus.valueOf("RED"));
        }

        @Test
        @DisplayName("EnrollmentStatus enum - Valores correctos")
        void testEnrollmentStatusEnum() {
            assertEquals(3, EnrollmentStatus.values().length);
            assertNotNull(EnrollmentStatus.valueOf("ACTIVE"));
            assertNotNull(EnrollmentStatus.valueOf("CANCELLED"));
            assertNotNull(EnrollmentStatus.valueOf("COMPLETED"));
        }

        @Test
        @DisplayName("DayOfWeek enum - Valores correctos")
        void testDayOfWeekEnum() {
            assertEquals(6, DayOfWeek.values().length);
            assertNotNull(DayOfWeek.valueOf("MONDAY"));
            assertNotNull(DayOfWeek.valueOf("FRIDAY"));
            assertNotNull(DayOfWeek.valueOf("SATURDAY"));
        }
    }
}