package eci.edu.dosw.proyecto.service.interfaces;


import eci.edu.dosw.proyecto.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;
public interface GroupService {
    GroupResponse createGroup(GroupRequest groupRequest);
    GroupResponse getGroupById(String id);
    GroupResponse getGroupByCodeAndSubject(String groupCode, String subjectId);
    List<GroupResponse> getGroupsBySubject(String subjectId);
    List<GroupResponse> getGroupsByFaculty(String facultyId);
    List<GroupResponse> getGroupsByProfessor(String professorId);
    List<GroupResponse> getAllActiveGroups();
    GroupResponse updateGroup(String groupId, GroupUpdateRequest groupRequest);
    GroupResponse updateGroupCapacity(String groupId, Integer newCapacity);
    void deactivateGroup(String groupId);

    // Capacity and occupancy
    GroupCapacityResponse getGroupCapacity(String groupId);
    List<GroupCapacityResponse> getGroupsCapacity(List<String> groupIds);
    List<GroupResponse> getGroupsWithHighOccupancy(Double threshold);
    boolean hasAvailableSpots(String groupId);
    Integer getAvailableSpots(String groupId);

    // Schedule management
    ScheduleResponse addScheduleToGroup(ScheduleRequest scheduleRequest);
    List<ScheduleResponse> getGroupSchedules(String groupId);
    ScheduleResponse updateSchedule(String scheduleId, ScheduleRequest scheduleRequest);
    void removeSchedule(String scheduleId);
    void removeAllGroupSchedules(String groupId);

    // Professor assignment
    GroupResponse assignProfessorToGroup(String groupId, String professorId);
    GroupResponse removeProfessorFromGroup(String groupId);

    // Student enrollment (for manual assignments)
    GroupResponse enrollStudentInGroup(String groupId, String studentId);
    GroupResponse unenrollStudentFromGroup(String groupId, String studentId);
    List<StudentBasicResponse> getGroupEnrolledStudents(String groupId);

    boolean hasScheduleConflict(String studentId, String groupId);
    List<GroupResponse> getGroupsAtHighOccupancy(Double threshold);

    List<GroupOccupancyResponse> getGroupsAtHighOccupancyWithDetails(Double threshold);
}
