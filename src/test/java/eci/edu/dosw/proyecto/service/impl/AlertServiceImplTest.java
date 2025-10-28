// java
package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.Alert;
import eci.edu.dosw.proyecto.model.AlertType;
import eci.edu.dosw.proyecto.model.Group;
import eci.edu.dosw.proyecto.model.Subject;
import eci.edu.dosw.proyecto.model.User;
import eci.edu.dosw.proyecto.repository.AlertRepository;
import eci.edu.dosw.proyecto.repository.GroupRepository;
import eci.edu.dosw.proyecto.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceImplTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AlertServiceImpl alertService;

    @Test
    void checkAndCreateGroupCapacityAlert_createsAlert_whenTriggeredAndNotExists() {
        Group group = mock(Group.class);
        when(groupRepository.findById("g1")).thenReturn(Optional.of(group));
        when(group.shouldTrigger90PercentAlert()).thenReturn(true);
        when(group.getId()).thenReturn("g1");
        when(group.getGroupCode()).thenReturn("G01");
        Subject subject = new Subject();
        subject.setName("Matemáticas");
        when(group.getSubject()).thenReturn(subject);

        when(alertRepository.findUnresolvedAlertByTypeAndGroup(AlertType.GROUP_CAPACITY_90, "g1"))
                .thenReturn(Optional.empty());

        alertService.checkAndCreateGroupCapacityAlert("g1");

        ArgumentCaptor<Alert> captor = ArgumentCaptor.forClass(Alert.class);
        verify(alertRepository, times(1)).save(captor.capture());
        Alert saved = captor.getValue();

        assertEquals(AlertType.GROUP_CAPACITY_90, saved.getType());
        assertTrue(saved.getTitle().contains("90%"));
        assertEquals("HIGH", saved.getSeverity());
        assertFalse(saved.isResolved());
        assertNotNull(saved.getCreatedAt());
        assertEquals(group, saved.getGroup());
    }

    @Test
    void checkAndCreateGroupCapacityAlert_noCreate_whenAlertAlreadyExists() {
        Group group = mock(Group.class);
        when(groupRepository.findById("g2")).thenReturn(Optional.of(group));
        when(group.shouldTrigger90PercentAlert()).thenReturn(true);
        when(group.getId()).thenReturn("g2");

        Alert existing = new Alert();
        when(alertRepository.findUnresolvedAlertByTypeAndGroup(AlertType.GROUP_CAPACITY_90, "g2"))
                .thenReturn(Optional.of(existing));

        alertService.checkAndCreateGroupCapacityAlert("g2");

        verify(alertRepository, never()).save(any());
    }

    @Test
    void checkAndCreateGroupCapacityAlert_groupNotFound_throws() {
        when(groupRepository.findById("missing")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> alertService.checkAndCreateGroupCapacityAlert("missing"));
    }

    @Test
    void resolveGroupCapacityAlert_resolves_whenExistingAndAdminPresent() {
        Alert alert = new Alert();
        alert.setResolved(false);
        alert.setId("a1");

        when(alertRepository.findUnresolvedAlertByTypeAndGroup(AlertType.GROUP_CAPACITY_90, "g1"))
                .thenReturn(Optional.of(alert));

        User admin = new User();
        admin.setId("uadmin");
        when(userRepository.findByRoleName("ADMIN")).thenReturn(List.of(admin));

        alertService.resolveGroupCapacityAlert("g1");

        ArgumentCaptor<Alert> captor = ArgumentCaptor.forClass(Alert.class);
        verify(alertRepository, times(1)).save(captor.capture());
        Alert saved = captor.getValue();

        assertTrue(saved.isResolved());
        assertNotNull(saved.getResolvedAt());
        assertEquals(admin, saved.getResolvedBy());
    }

    @Test
    void resolveGroupCapacityAlert_noExisting_doesNothing() {
        when(alertRepository.findUnresolvedAlertByTypeAndGroup(AlertType.GROUP_CAPACITY_90, "g3"))
                .thenReturn(Optional.empty());

        alertService.resolveGroupCapacityAlert("g3");

        verify(alertRepository, never()).save(any());
    }

    @Test
    void getActiveAlerts_delegatesToRepository() {
        List<Alert> list = List.of(new Alert(), new Alert());
        when(alertRepository.findByResolvedFalse()).thenReturn(list);

        List<Alert> result = alertService.getActiveAlerts();

        assertSame(list, result);
        verify(alertRepository).findByResolvedFalse();
    }

    @Test
    void getAlertsByType_delegatesToRepository() {
        List<Alert> list = List.of(new Alert());
        when(alertRepository.findByTypeAndResolvedFalse(AlertType.GROUP_CAPACITY_90)).thenReturn(list);

        List<Alert> result = alertService.getAlertsByType(AlertType.GROUP_CAPACITY_90);

        assertSame(list, result);
        verify(alertRepository).findByTypeAndResolvedFalse(AlertType.GROUP_CAPACITY_90);
    }

    @Test
    void resolveAlert_success() {
        Alert alert = new Alert();
        alert.setId("a10");
        when(alertRepository.findById("a10")).thenReturn(Optional.of(alert));

        User resolver = new User();
        resolver.setId("u10");
        when(userRepository.findById("u10")).thenReturn(Optional.of(resolver));

        when(alertRepository.save(any(Alert.class))).thenAnswer(inv -> inv.getArgument(0));

        Alert saved = alertService.resolveAlert("a10", "u10");

        assertTrue(saved.isResolved());
        assertNotNull(saved.getResolvedAt());
        assertEquals(resolver, saved.getResolvedBy());
        verify(alertRepository).save(saved);
    }

    @Test
    void resolveAlert_alertNotFound_throws() {
        when(alertRepository.findById("no")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> alertService.resolveAlert("no", "u"));
    }

    @Test
    void resolveAlert_userNotFound_throws() {
        Alert alert = new Alert();
        when(alertRepository.findById("a11")).thenReturn(Optional.of(alert));
        when(userRepository.findById("missingUser")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> alertService.resolveAlert("a11", "missingUser"));
    }
}