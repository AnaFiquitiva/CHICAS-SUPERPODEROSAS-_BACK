package eci.edu.dosw.proyecto.service.interfaces;


import eci.edu.dosw.proyecto.dto.StudentDTO;
import eci.edu.dosw.proyecto.dto.StudentCreateDTO;
import eci.edu.dosw.proyecto.dto.StudentPartialUpdateDTO;

import java.util.List;

/**
 * Interfaz que define las operaciones del servicio para gestionar estudiantes.
 * Incluye métodos para crear, consultar, actualizar y eliminar registros.
 */
public interface StudentService {

    /**
     * Registra un nuevo estudiante.
     * @param studentDTO datos del estudiante a crear.
     * @return el estudiante registrado.
     */
    StudentDTO createStudent(StudentCreateDTO studentDTO);

    /**
     * Obtiene la información de un estudiante por su código.
     * @param studentCode código del estudiante.
     * @return el estudiante correspondiente.
     */
    StudentDTO getStudentByCode(String studentCode);

    /**
     * Obtiene todos los estudiantes aplicando filtros opcionales.
     * @param name nombre parcial o completo del estudiante.
     * @param code código exacto del estudiante.
     * @param program programa académico.
     * @return lista filtrada de estudiantes.
     */
    List<StudentDTO> getAllStudents(String name, String code, String program);

    /**
     * Actualiza los datos de un estudiante existente.
     * @param studentCode código del estudiante a modificar.
     * @param updatedStudent datos actualizados.
     * @param role rol del usuario que realiza la operación (ADMIN, DECANO, ESTUDIANTE).
     * @return el estudiante actualizado.
     */
    StudentDTO updateStudent(String studentCode, StudentDTO updatedStudent, String role);

    /**
     * Elimina un estudiante según su código.
     * @param studentCode código del estudiante.
     * @param role rol del usuario que realiza la eliminación.
     */
    void deleteStudent(String studentCode, String role);

    /**
     * Actualización parcial para estudiantes (correo, dirección y teléfono).
     */
    StudentDTO updateStudentPartial(String studentCode, StudentPartialUpdateDTO updatedFields, String role);

}