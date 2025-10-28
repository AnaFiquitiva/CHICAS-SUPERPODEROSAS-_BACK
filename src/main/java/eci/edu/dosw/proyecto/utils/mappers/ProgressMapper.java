package eci.edu.dosw.proyecto.utils.mappers;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.model.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {StudentMapper.class, AcademicMapper.class})
public interface ProgressMapper {

    // StudyPlanProgress Mappings
    @Mapping(source = "student", target = "student")
    @Mapping(source = "program", target = "program")
    @Mapping(source = "subjectProgress", target = "subjectProgress")
    StudyPlanProgressResponse toStudyPlanProgressResponse(StudyPlanProgress progress);

    // SubjectProgress Mappings
    @Mapping(source = "subject", target = "subject")
    SubjectProgressResponse toSubjectProgressResponse(SubjectProgress subjectProgress);

    // StudentSchedule Mappings
    @Mapping(source = "student", target = "student")
    @Mapping(source = "enrolledGroups", target = "enrolledGroups")
    @Mapping(source = "schedules", target = "schedules")
    StudentScheduleResponse toStudentScheduleResponse(StudentSchedule studentSchedule);

    // Lists
    List<StudyPlanProgressResponse> toStudyPlanProgressResponseList(List<StudyPlanProgress> progresses);
    List<SubjectProgressResponse> toSubjectProgressResponseList(List<SubjectProgress> subjectProgresses);
    List<StudentScheduleResponse> toStudentScheduleResponseList(List<StudentSchedule> studentSchedules);
    default String getTrafficLightColor(SubjectProgress progress) {
        return progress.getTrafficLightColor();
    }

    default boolean getCanBeRetaken(SubjectProgress progress) {
        return progress.canBeRetaken();
    }
}