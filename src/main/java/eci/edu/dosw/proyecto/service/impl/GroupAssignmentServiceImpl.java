/**
 * Service implementation for group assignment operations
 * Handles business logic for assigning students to groups with validation
 */
package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.GroupAssignmentRequest;
import eci.edu.dosw.proyecto.dto.GroupAssignmentResponse;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.*;
import eci.edu.dosw.proyecto.service.interfaces.GroupAssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupAssignmentServiceImpl implements GroupAssignmentService {

    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final WaitingListRepository waitingListRepository;

    @Override
    @Transactional
    public GroupAssignmentResponse assignStudentToGroup(GroupAssignmentRequest request) {
        log.info("Assigning student {} to group {}", request.getStudentId(), request.getGroupId());

        // Validate student and group existence
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found: " + request.getStudentId()));

        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found: " + request.getGroupId()));

        // Validate if student is already enrolled in this group
        if (isAlreadyEnrolled(student.getId(), group.getId())) {
            throw new RuntimeException("Student is already enrolled in this group");
        }

        // Validate schedule conflicts
        if (hasScheduleConflict(student, group)) {
            log.warn("Schedule conflict detected for student {} in group {}", student.getId(), group.getId());
            throw new RuntimeException("Student has schedule conflict with this group");
        }

        // Check group capacity
        if (group.getCurrentEnrollment() >= group.getMaxCapacity() && !request.getForceAssignment()) {
            log.info("Group {} at capacity. Adding student {} to waitlist", group.getId(), student.getId());
            return handleWaitlistAssignment(student, group);
        }

        // Perform assignment
        Enrollment enrollment = createEnrollment(student, group);
        enrollmentRepository.save(enrollment);

        // Update group enrollment count
        group.setCurrentEnrollment(group.getCurrentEnrollment() + 1);
        groupRepository.save(group);

        log.info("Successfully assigned student {} to group {}", student.getId(), group.getId());

        return buildAssignmentResponse(student, group, "ASSIGNED", "Assignment successful");
    }

    @Override
    @Transactional
    public void bulkAssignStudents(List<GroupAssignmentRequest> requests) {
        log.info("Processing bulk assignment for {} students", requests.size());

        int successCount = 0;
        int failureCount = 0;

        for (GroupAssignmentRequest request : requests) {
            try {
                GroupAssignmentResponse response = assignStudentToGroup(request);
                successCount++;
                log.debug("Successfully assigned student {} to group {}", request.getStudentId(), request.getGroupId());
            } catch (Exception e) {
                failureCount++;
                log.error("Failed to assign student {} to group {}: {}", request.getStudentId(), request.getGroupId(), e.getMessage());
            }
        }

        log.info("Bulk assignment completed. Successful: {}, Failed: {}", successCount, failureCount);
    }

    @Override
    @Transactional
    public void removeStudentFromGroup(String enrollmentId) {
        log.info("Removing student from enrollment: {}", enrollmentId);

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found: " + enrollmentId));

        // Update group enrollment count
        Group group = groupRepository.findById(enrollment.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found: " + enrollment.getGroupId()));

        if (group.getCurrentEnrollment() > 0) {
            group.setCurrentEnrollment(group.getCurrentEnrollment() - 1);
            groupRepository.save(group);
        }

        // Cancel the enrollment
        enrollment.setStatus(EnrollmentStatus.CANCELLED);
        enrollmentRepository.save(enrollment);

        // Process waitlist if available
        processWaitlist(group.getId());

        log.info("Student successfully removed from group. Enrollment: {}", enrollmentId);
    }

    /**
     * Checks if student is already enrolled in the group
     */
    private boolean isAlreadyEnrolled(String studentId, String groupId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentIdAndStatus(studentId, EnrollmentStatus.ACTIVE);
        return enrollments.stream()
                .anyMatch(enrollment -> enrollment.getGroupId().equals(groupId));
    }

    /**
     * Checks if student has schedule conflict with the new group
     */
    private boolean hasScheduleConflict(Student student, Group newGroup) {
        if (newGroup.getSchedule() == null) {
            return false; // No schedule defined, no conflict
        }

        List<Enrollment> currentEnrollments = enrollmentRepository.findByStudentIdAndStatus(
                student.getId(), EnrollmentStatus.ACTIVE);

        return currentEnrollments.stream()
                .anyMatch(enrollment -> {
                    Group currentGroup = groupRepository.findById(enrollment.getGroupId()).orElse(null);
                    return currentGroup != null &&
                            currentGroup.getSchedule() != null &&
                            currentGroup.getSchedule().hasConflict(newGroup.getSchedule());
                });
    }

    /**
     * Handles assignment when group is at capacity by adding to waitlist
     */
    private GroupAssignmentResponse handleWaitlistAssignment(Student student, Group group) {
        // Check if student is already in waitlist for this group
        List<WaitingListEntry> existingWaitlist = waitingListRepository.findByGroupId(group.getId());
        boolean alreadyInWaitlist = existingWaitlist.stream()
                .anyMatch(entry -> entry.getStudentId().equals(student.getId()));

        if (alreadyInWaitlist) {
            throw new RuntimeException("Student is already in waitlist for this group");
        }

        WaitingListEntry waitingEntry = new WaitingListEntry();
        waitingEntry.setGroupId(group.getId());
        waitingEntry.setStudentId(student.getId());
        waitingEntry.setRequestDate(LocalDateTime.now());
        waitingListRepository.save(waitingEntry);

        // Update waitlist count
        int waitlistPosition = existingWaitlist.size() + 1;
        group.setWaitingListCount(waitlistPosition);
        groupRepository.save(group);

        GroupAssignmentResponse response = buildAssignmentResponse(student, group,
                "WAITLISTED", "Student added to waitlist");
        response.setWaitlistPosition(waitlistPosition);

        log.info("Student {} added to waitlist for group {} at position {}",
                student.getId(), group.getId(), waitlistPosition);

        return response;
    }

    /**
     * Process waitlist when a spot becomes available
     */
    private void processWaitlist(String groupId) {
        List<WaitingListEntry> waitlist = waitingListRepository.findByGroupId(groupId);

        if (!waitlist.isEmpty()) {
            // Ordenar por fecha de solicitud (más antigua primero)
            waitlist.sort((w1, w2) -> w1.getRequestDate().compareTo(w2.getRequestDate()));
            WaitingListEntry nextStudent = waitlist.get(0);

            try {
                // Create assignment request for the waitlisted student
                GroupAssignmentRequest request = new GroupAssignmentRequest();
                request.setStudentId(nextStudent.getStudentId());
                request.setGroupId(groupId);
                request.setReason("Automatic assignment from waitlist");
                request.setForceAssignment(true);

                // Assign the student
                assignStudentToGroup(request);

                // Remove from waitlist after successful assignment
                waitingListRepository.delete(nextStudent);

                log.info("Automatically assigned waitlisted student {} to group {}",
                        nextStudent.getStudentId(), groupId);

            } catch (Exception e) {
                log.error("Failed to assign waitlisted student {} to group {}: {}",
                        nextStudent.getStudentId(), groupId, e.getMessage());
            }
        }
    }

    /**
     * Creates a new enrollment record
     */
    private Enrollment createEnrollment(Student student, Group group) {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(student.getId());
        enrollment.setGroupId(group.getId());
        enrollment.setSubjectId(group.getSubjectId());
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        return enrollment;
    }

    /**
     * Builds assignment response DTO
     */
    private GroupAssignmentResponse buildAssignmentResponse(Student student, Group group,
                                                            String status, String message) {
        GroupAssignmentResponse response = new GroupAssignmentResponse();
        response.setStudentId(student.getId());
        response.setStudentName(student.getName());
        response.setGroupId(group.getId());
        response.setGroupCode(group.getGroupCode());
        response.setSubjectName(getSubjectName(group.getSubjectId()));
        response.setStatus(status);
        response.setMessage(message);
        response.setAssignmentDate(LocalDateTime.now());
        return response;
    }

    /**
     * Helper method to get subject name
     */
    private String getSubjectName(String subjectId) {
        // Placeholder - deberías inyectar SubjectRepository y buscar el nombre real
        return "Subject-" + subjectId;
    }
}