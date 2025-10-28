package eci.edu.dosw.proyecto.service.interfaces;


import eci.edu.dosw.proyecto.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;
public interface WaitingListService {
    WaitingListResponse addStudentToWaitingList(String groupId, String studentId);
    WaitingListResponse removeStudentFromWaitingList(String groupId, String studentId);
    WaitingListResponse getWaitingListByGroup(String groupId);
    List<WaitingListEntryResponse> getWaitingListEntriesByGroup(String groupId);
    List<WaitingListResponse> getWaitingListsByFaculty(String facultyId);
    List<WaitingListEntryResponse> getStudentWaitingListEntries(String studentId);
    Integer getStudentPositionInWaitingList(String groupId, String studentId);
    boolean isStudentInWaitingList(String groupId, String studentId);

    // Automatic processing
    void processWaitingList(String groupId);
    WaitingListEntryResponse getNextStudentInWaitingList(String groupId);
    boolean promoteNextStudent(String groupId);

    // Administration
    void clearWaitingList(String groupId);
    long getWaitingListSize(String groupId);
}