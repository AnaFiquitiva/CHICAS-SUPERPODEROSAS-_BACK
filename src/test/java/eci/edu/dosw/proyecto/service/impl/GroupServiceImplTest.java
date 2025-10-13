package eci.edu.dosw.proyecto.service.impl;

import static org.junit.jupiter.api.Assertions.*;


import eci.edu.dosw.proyecto.dto.GroupCapacityResponseDTO;
import eci.edu.dosw.proyecto.exception.GroupNotFoundException;
import eci.edu.dosw.proyecto.model.Group;
import eci.edu.dosw.proyecto.repository.GroupRepository;
import eci.edu.dosw.proyecto.utils.GroupMapper;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.mockito.Mockito.*;

class GroupServiceImplTest {

    private final GroupRepository repo = mock(GroupRepository.class);
    private final GroupMapper mapper = new GroupMapper();
    private final GroupServiceImpl service = new GroupServiceImpl(repo, mapper);

    @Test
    void getCapacity_existingGroup_returnsDTO() {
        Group g = new Group();
        g.setId("G1");
        g.setCurrentEnrollment(20);
        g.setMaxCapacity(40);

        when(repo.findById("G1")).thenReturn(Optional.of(g));

        GroupCapacityResponseDTO dto = service.getCapacity("G1");

        assertEquals("G1", dto.getGroupId());
        assertEquals(20, dto.getEnrolledCount());
        assertEquals(40, dto.getMaxCapacity());
        assertEquals(50.0, dto.getOccupancyPercentage());
        verify(repo, times(1)).findById("G1");
    }

    @Test
    void getCapacity_groupNotFound_throwsException() {
        when(repo.findById("NOPE")).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> service.getCapacity("NOPE"));
        verify(repo, times(1)).findById("NOPE");
    }
}
