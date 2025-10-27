package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.WaitingListEntryResponse;
import eci.edu.dosw.proyecto.dto.WaitingListResponse;
import eci.edu.dosw.proyecto.exception.BusinessValidationException;
import eci.edu.dosw.proyecto.exception.ForbiddenException;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.*;
import eci.edu.dosw.proyecto.service.interfaces.GroupService;
import eci.edu.dosw.proyecto.service.interfaces.WaitingListService;
import eci.edu.dosw.proyecto.utils.mappers.WaitingListMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WaitingListServiceImpl implements WaitingListService {

    private final WaitingListRepository waitingListRepository;
    private final WaitingListEntryRepository waitingListEntryRepository;
    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final DeanRepository deanRepository;
    private final WaitingListMapper waitingListMapper;
    private GroupService groupService;

    @Lazy
    @Autowired
    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }

    @Override
    @Transactional
    public WaitingListResponse addStudentToWaitingList(String groupId, String studentId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Grupo", groupId));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Estudiante", studentId));

        if (group.hasAvailableSpots()) {
            throw new BusinessValidationException("No se puede agregar a lista de espera: el grupo tiene cupos disponibles");
        }

        if (isStudentInWaitingList(groupId, studentId)) {
            throw new BusinessValidationException("El estudiante ya está en la lista de espera");
        }

        WaitingList waitingList = waitingListRepository.findByGroupId(groupId)
                .orElseGet(() -> {
                    WaitingList newList = WaitingList.builder()
                            .group(group)
                            .entries(new ArrayList<>())
                            .createdAt(LocalDateTime.now())
                            .build();
                    return waitingListRepository.save(newList);
                });

        int position = (int) waitingListEntryRepository.countActiveEntriesByWaitingListId(waitingList.getId()) + 1;

        WaitingListEntry entry = WaitingListEntry.builder()
                .student(student)
                .waitingList(waitingList)
                .position(position)
                .joinedAt(LocalDateTime.now())
                .active(true)
                .build();

        WaitingListEntry savedEntry = waitingListEntryRepository.save(entry);

        if (waitingList.getEntries() == null) {
            waitingList.setEntries(new ArrayList<>());
        }
        waitingList.getEntries().add(savedEntry);

        WaitingList updatedList = waitingListRepository.save(waitingList);
        return waitingListMapper.toWaitingListResponse(updatedList);
    }

    @Override
    @Transactional
    public WaitingListResponse removeStudentFromWaitingList(String groupId, String studentId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Grupo", groupId));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Estudiante", studentId));

        WaitingList waitingList = waitingListRepository.findByGroupId(groupId)
                .orElseThrow(() -> new NotFoundException("Lista de espera", groupId));

        validateFacultyAccess(group.getSubject().getFaculty().getId());

        WaitingListEntry entry = waitingListEntryRepository.findByWaitingListAndStudentAndActiveTrue(waitingList, student)
                .orElseThrow(() -> new NotFoundException("Entrada en lista de espera", "estudiante " + studentId));

        entry.setActive(false);
        waitingListEntryRepository.save(entry);

        reorganizePositions(waitingList);

        return waitingListMapper.toWaitingListResponse(waitingList);
    }

    @Override
    public WaitingListResponse getWaitingListByGroup(String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Grupo", groupId));

        validateFacultyAccess(group.getSubject().getFaculty().getId());

        return waitingListRepository.findByGroupId(groupId)
                .map(waitingListMapper::toWaitingListResponse)
                .orElseGet(() -> {
                    WaitingListResponse response = new WaitingListResponse();
                    response.setId(null);
                    response.setGroup(waitingListMapper.toGroupBasicResponse(group));
                    response.setEntries(List.of());
                    response.setTotalEntries(0);
                    response.setCreatedAt(LocalDateTime.now());
                    return response;
                });
    }

    @Override
    public List<WaitingListEntryResponse> getWaitingListEntriesByGroup(String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Grupo", groupId));

        validateFacultyAccess(group.getSubject().getFaculty().getId());

        List<WaitingListEntry> entries = waitingListEntryRepository.findByWaitingListIdAndActiveTrueOrderByPositionAsc(groupId);
        return entries.stream()
                .map(waitingListMapper::toWaitingListEntryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<WaitingListResponse> getWaitingListsByFaculty(String facultyId) {
        validateFacultyAccess(facultyId);
        List<WaitingList> lists = waitingListRepository.findByFacultyId(facultyId);
        return lists.stream()
                .map(waitingListMapper::toWaitingListResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<WaitingListEntryResponse> getStudentWaitingListEntries(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Estudiante", studentId));

        User currentUser = getCurrentAuthenticatedUser();
        if (!"ADMIN".equals(currentUser.getRole().getName()) &&
                !student.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("No tienes permiso para ver la lista de espera de otro estudiante");
        }

        List<WaitingListEntry> entries = waitingListEntryRepository.findByStudentIdAndActiveTrue(studentId);
        return entries.stream()
                .map(waitingListMapper::toWaitingListEntryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Integer getStudentPositionInWaitingList(String groupId, String studentId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Grupo", groupId));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Estudiante", studentId));

        User currentUser = getCurrentAuthenticatedUser();
        if (!"ADMIN".equals(currentUser.getRole().getName()) &&
                !student.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("No tienes permiso para ver la posición en lista de espera");
        }

        return waitingListEntryRepository.findByWaitingListIdAndActiveTrueOrderByPositionAsc(groupId)
                .stream()
                .filter(entry -> entry.getStudent().getId().equals(studentId))
                .findFirst()
                .map(WaitingListEntry::getPosition)
                .orElse(null);
    }

    @Override
    public boolean isStudentInWaitingList(String groupId, String studentId) {
        return waitingListEntryRepository.findByStudentIdAndActiveTrue(studentId)
                .stream()
                .anyMatch(entry -> entry.getWaitingList().getGroup().getId().equals(groupId));
    }

    @Override
    @Transactional
    public void processWaitingList(String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Grupo", groupId));

        validateAdminAccess();

        if (group.hasAvailableSpots()) {
            promoteNextStudent(groupId);
        }
    }

    @Override
    public WaitingListEntryResponse getNextStudentInWaitingList(String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Grupo", groupId));

        validateFacultyAccess(group.getSubject().getFaculty().getId());

        return waitingListEntryRepository.findByWaitingListIdAndActiveTrueOrderByPositionAsc(groupId)
                .stream()
                .findFirst()
                .map(waitingListMapper::toWaitingListEntryResponse)
                .orElse(null);
    }

    @Override
    @Transactional
    public boolean promoteNextStudent(String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Grupo", groupId));

        if (!group.hasAvailableSpots()) {
            return false;
        }

        List<WaitingListEntry> entries = waitingListEntryRepository.findByWaitingListIdAndActiveTrueOrderByPositionAsc(groupId);
        if (entries.isEmpty()) {
            return false;
        }

        WaitingListEntry firstEntry = entries.get(0);

        try {
            groupService.enrollStudentInGroup(groupId, firstEntry.getStudent().getId());

            firstEntry.setActive(false);
            waitingListEntryRepository.save(firstEntry);

            WaitingList waitingList = waitingListRepository.findByGroupId(groupId)
                    .orElseThrow(() -> new NotFoundException("Lista de espera", groupId));
            reorganizePositions(waitingList);

            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error al promover estudiante: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void clearWaitingList(String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Grupo", groupId));

        validateAdminAccess();

        WaitingList waitingList = waitingListRepository.findByGroupId(groupId)
                .orElseThrow(() -> new NotFoundException("Lista de espera", groupId));

        waitingList.getEntries().forEach(entry -> entry.setActive(false));
        waitingListEntryRepository.saveAll(waitingList.getEntries());

        waitingList.setEntries(new ArrayList<>());
        waitingListRepository.save(waitingList);
    }

    @Override
    public long getWaitingListSize(String groupId) {
        return waitingListEntryRepository.countActiveEntriesByWaitingListId(groupId);
    }

    private void reorganizePositions(WaitingList waitingList) {
        List<WaitingListEntry> activeEntries = waitingListEntryRepository.findByWaitingListIdAndActiveTrueOrderByPositionAsc(waitingList.getGroup().getId());
        for (int i = 0; i < activeEntries.size(); i++) {
            activeEntries.get(i).setPosition(i + 1);
        }
        waitingListEntryRepository.saveAll(activeEntries);
    }

    private void validateAdminAccess() {
        User user = getCurrentAuthenticatedUser();
        if (!"ADMIN".equals(user.getRole().getName())) {
            throw new ForbiddenException("Solo los administradores pueden realizar esta acción");
        }
    }

    private void validateFacultyAccess(String facultyId) {
        User user = getCurrentAuthenticatedUser();
        String userRole = user.getRole().getName();

        if ("ADMIN".equals(userRole)) {
            return;
        }

        if ("DEAN".equals(userRole)) {
            Dean dean = deanRepository.findByUser(user)
                    .orElseThrow(() -> new NotFoundException("Decano", user.getId()));
            if (!dean.getFaculty().getId().equals(facultyId)) {
                throw new ForbiddenException("No tienes permiso para gestionar listas de espera de otra facultad");
            }
            return;
        }

        throw new ForbiddenException("No tienes permiso para gestionar listas de espera");
    }

    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ForbiddenException("Usuario no autenticado");
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Usuario autenticado", username));
    }
}