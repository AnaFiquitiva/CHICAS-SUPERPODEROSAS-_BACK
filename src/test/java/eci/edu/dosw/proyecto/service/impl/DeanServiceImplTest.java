// java
package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.DeanRequest;
import eci.edu.dosw.proyecto.dto.DeanResponse;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.Dean;
import eci.edu.dosw.proyecto.model.Faculty;
import eci.edu.dosw.proyecto.model.User;
import eci.edu.dosw.proyecto.repository.DeanRepository;
import eci.edu.dosw.proyecto.repository.FacultyRepository;
import eci.edu.dosw.proyecto.repository.UserRepository;
import eci.edu.dosw.proyecto.utils.mappers.DeanProfessorMapper;
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
class DeanServiceImplTest {

    @Mock
    private DeanRepository deanRepository;

    @Mock
    private FacultyRepository facultyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DeanProfessorMapper deanMapper;

    @InjectMocks
    private DeanServiceImpl deanService;

    @Test
    void createDean_success() {
        DeanRequest req = new DeanRequest();
        req.setInstitutionalEmail("decano@uni.edu");
        req.setFacultyId("f1");
        req.setFirstName("Juan");
        req.setLastName("Perez");

        when(deanRepository.existsByInstitutionalEmail("decano@uni.edu")).thenReturn(false);

        Faculty faculty = new Faculty();
        faculty.setId("f1");
        when(facultyRepository.findById("f1")).thenReturn(Optional.of(faculty));

        Dean mapped = new Dean();
        when(deanMapper.toDean(req)).thenReturn(mapped);

        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId("u1");
            return u;
        });

        when(deanRepository.save(any(Dean.class))).thenAnswer(inv -> {
            Dean d = inv.getArgument(0);
            d.setId("d1");
            return d;
        });

        DeanResponse expectedResp = new DeanResponse();
        expectedResp.setId("d1");
        when(deanMapper.toDeanResponse(any(Dean.class))).thenReturn(expectedResp);

        DeanResponse resp = deanService.createDean(req);

        assertNotNull(resp);
        assertEquals("d1", resp.getId());
        ArgumentCaptor<Dean> deanCaptor = ArgumentCaptor.forClass(Dean.class);
        verify(deanRepository).save(deanCaptor.capture());
        assertEquals(faculty, deanCaptor.getValue().getFaculty());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createDean_emailExists_throws() {
        DeanRequest req = new DeanRequest();
        req.setInstitutionalEmail("decano@uni.edu");
        when(deanRepository.existsByInstitutionalEmail("decano@uni.edu")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> deanService.createDean(req));
        assertTrue(ex.getMessage().contains("Ya existe un decano"));
    }

    @Test
    void createDean_facultyNotFound_throws() {
        DeanRequest req = new DeanRequest();
        req.setInstitutionalEmail("decano@uni.edu");
        req.setFacultyId("missing");
        when(deanRepository.existsByInstitutionalEmail("decano@uni.edu")).thenReturn(false);
        when(facultyRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> deanService.createDean(req));
    }

    @Test
    void getDeanById_success() {
        Dean dean = new Dean();
        dean.setId("d2");
        when(deanRepository.findById("d2")).thenReturn(Optional.of(dean));

        DeanResponse dr = new DeanResponse();
        dr.setId("d2");
        when(deanMapper.toDeanResponse(dean)).thenReturn(dr);

        DeanResponse resp = deanService.getDeanById("d2");
        assertEquals("d2", resp.getId());
    }

    @Test
    void getDeanById_notFound_throws() {
        when(deanRepository.findById("no")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> deanService.getDeanById("no"));
    }

    @Test
    void getDeanByEmail_success_and_notFound() {
        Dean dean = new Dean();
        dean.setId("d3");
        when(deanRepository.findByInstitutionalEmail("a@u.edu")).thenReturn(Optional.of(dean));
        DeanResponse resp = new DeanResponse();
        resp.setId("d3");
        when(deanMapper.toDeanResponse(dean)).thenReturn(resp);

        DeanResponse r = deanService.getDeanByEmail("a@u.edu");
        assertEquals("d3", r.getId());

        when(deanRepository.findByInstitutionalEmail("missing@u.edu")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> deanService.getDeanByEmail("missing@u.edu"));
    }

    @Test
    void updateDean_success() {
        Dean existing = new Dean();
        existing.setId("d4");
        existing.setInstitutionalEmail("old@u.edu");
        when(deanRepository.findById("d4")).thenReturn(Optional.of(existing));

        DeanRequest req = new DeanRequest();
        req.setInstitutionalEmail("new@u.edu");
        req.setFacultyId("f2");
        req.setFirstName("Ana");
        req.setLastName("Lopez");

        when(deanRepository.existsByInstitutionalEmail("new@u.edu")).thenReturn(false);

        Faculty faculty = new Faculty();
        faculty.setId("f2");
        when(facultyRepository.findById("f2")).thenReturn(Optional.of(faculty));

        when(deanRepository.save(existing)).thenAnswer(inv -> inv.getArgument(0));

        DeanResponse resp = new DeanResponse();
        resp.setId("d4");
        when(deanMapper.toDeanResponse(existing)).thenReturn(resp);

        DeanResponse updated = deanService.updateDean("d4", req);
        assertEquals("d4", updated.getId());
        verify(deanRepository).save(existing);
        assertEquals("new@u.edu", existing.getInstitutionalEmail());
    }

    @Test
    void updateDean_emailConflict_throws() {
        Dean existing = new Dean();
        existing.setId("d5");
        existing.setInstitutionalEmail("old@u.edu");
        when(deanRepository.findById("d5")).thenReturn(Optional.of(existing));

        DeanRequest req = new DeanRequest();
        req.setInstitutionalEmail("conflict@u.edu");

        when(deanRepository.existsByInstitutionalEmail("conflict@u.edu")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> deanService.updateDean("d5", req));
        assertTrue(ex.getMessage().contains("Ya existe un decano"));
    }

    @Test
    void updateDean_deanNotFound_throws() {
        when(deanRepository.findById("no")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> deanService.updateDean("no", new DeanRequest()));
    }

    @Test
    void deactivateDean_success() {
        Dean dean = new Dean();
        dean.setId("d6");
        User user = new User();
        user.setId("u6");
        user.setActive(true);
        dean.setUser(user);
        dean.setActive(true);

        when(deanRepository.findById("d6")).thenReturn(Optional.of(dean));
        when(deanRepository.save(any(Dean.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        deanService.deactivateDean("d6");

        ArgumentCaptor<Dean> deanCaptor = ArgumentCaptor.forClass(Dean.class);
        verify(deanRepository).save(deanCaptor.capture());
        assertFalse(deanCaptor.getValue().isActive());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertFalse(userCaptor.getValue().isActive());
    }

    @Test
    void getDeansByFaculty_and_getAllActiveDeans() {
        Faculty faculty = new Faculty();
        faculty.setId("f3");
        when(facultyRepository.findById("f3")).thenReturn(Optional.of(faculty));

        Dean d1 = new Dean();
        Dean d2 = new Dean();
        List<Dean> list = List.of(d1, d2);
        when(deanRepository.findByFacultyAndActiveTrue(faculty)).thenReturn(list);

        List<DeanResponse> mapped = List.of(new DeanResponse(), new DeanResponse());
        when(deanMapper.toDeanResponseList(list)).thenReturn(mapped);

        List<DeanResponse> res = deanService.getDeansByFaculty("f3");
        assertSame(mapped, res);

        List<Dean> all = List.of(new Dean());
        when(deanRepository.findByActiveTrue()).thenReturn(all);
        List<DeanResponse> mappedAll = List.of(new DeanResponse());
        when(deanMapper.toDeanResponseList(all)).thenReturn(mappedAll);

        List<DeanResponse> resAll = deanService.getAllActiveDeans();
        assertSame(mappedAll, resAll);
    }

    @Test
    void isDeanOfFaculty_true_and_notFound() {
        Dean dean = new Dean();
        Faculty faculty = new Faculty();
        faculty.setId("f4");
        dean.setFaculty(faculty);
        when(deanRepository.findById("d7")).thenReturn(Optional.of(dean));

        assertTrue(deanService.isDeanOfFaculty("d7", "f4"));

        when(deanRepository.findById("no")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> deanService.isDeanOfFaculty("no", "f4"));
    }
}