# CHICAS-SUPERPODEROSAS-_BACK
## Intregrantes:
- Valeria Bermúdez Aguilar
- Juan Andrés Suárez Fonseca
- Samuel Leonardo Albarracín Vergara
- Carlos David Astudillo Castiblanco
- Ana Gabriela Fiqutiva Poveda
## Repositorio:
- BackEnd : https://github.com/AnaFiquitiva/CHICAS-SUPERPODEROSAS-_BACK.git
- Tablero Jira: https://lc5.atlassian.net/jira/software/projects/SCRUM/boards/1/backlog?epics=visible&issueParent=0&selectedIssue=SCRUM-15&atlOrigin=eyJpIjoiNmJlZWYzMDVhZjhhNGRmOTk0ODExNjQ5NjVkOTRlOGYiLCJwIjoiaiJ9
- Documento de Requerimientos: https://docs.google.com/document/d/1QQTtvrpe3ckFsEVrEOXLoOOKtfOTOchyWGx3rIJgM5g/edit?usp=sharing
- Documento de de Arquitectura Back: https://docs.google.com/document/d/1qRB4X-avn5bgg7-FGpukmTu5wqMt_NLhz4XO49avYmM/edit?usp=sharing
---
# Proyecto Sirha – Backend

Este proyecto es el backend del sistema **Sirha**, desarrollado con **Spring Boot**, **MongoDB** y pruebas unitarias con **JUnit 5 y Mockito**.  
Se implementan los endpoints para gestionar **estudiantes**, incluyendo creación, consulta, actualización parcial/completa y eliminación, con control de acceso según el **rol del usuario**.

---

## ✅ Criterios de aceptación

Todos los criterios de aceptación definidos han sido completados y probados.

### 1. Registro de estudiantes
- **Descripción:** Solo usuarios con rol `ADMIN` o `DECANO` pueden registrar nuevos estudiantes.
- **Implementación:** Endpoint `POST /api/students`.
- **Validaciones:**
    - Código y correo institucional únicos.
    - Respuesta `403 FORBIDDEN` si el rol no es válido.
- **Pruebas:**
    - `createStudent_validRole_createsSuccessfully()`.
    - `createStudent_invalidRole_returnsForbidden()`.

### 2. Consulta de estudiante por código
- **Descripción:** Permite obtener la información de cualquier estudiante mediante su código.
- **Implementación:** Endpoint `GET /api/students/{studentCode}`.
- **Pruebas:**
    - `getStudentByCode_existingStudent_returnsStudentDTO()`.
    - `getStudentByCode_nonExistingStudent_returnsNotFound()`.

### 3. Listado de estudiantes
- **Descripción:** Listar todos los estudiantes con filtros opcionales por nombre, código o programa.
- **Implementación:** Endpoint `GET /api/students`.
- **Pruebas:**
    - `getAllStudents_noFilters_returnsAllStudents()`.
    - `getAllStudents_withFilters_returnsFilteredList()`.

### 4. Actualización completa de estudiante
- **Descripción:**
    - `ADMIN` y `DECANO` pueden actualizar todos los campos de un estudiante.
    - `ESTUDIANTE` solo puede actualizar su correo.
- **Implementación:** Endpoint `PUT /api/students/{studentCode}`.
- **Pruebas:**
    - `updateStudent_adminRole_updatesAllFields()`.
    - `updateStudent_studentRole_updatesEmailOnly()`.
    - `updateStudent_invalidRole_returnsBadRequest()`.

### 5. Eliminación de estudiante
- **Descripción:** Solo `ADMIN` puede eliminar estudiantes.
- **Implementación:** Endpoint `DELETE /api/students/{studentCode}`.
- **Pruebas:**
    - `deleteStudent_adminRole_deletesSuccessfully()`.
    - `deleteStudent_nonAdminRole_returnsForbidden()`.

### 6. Actualización parcial de estudiante
- **Descripción:** Solo `ESTUDIANTE` puede actualizar correo, dirección y teléfono.
- **Implementación:** Endpoint `PATCH /api/students/{studentCode}`.
- **Pruebas:**
    - `updateStudentPartial_studentRole_updatesFields()`.
    - `updateStudentPartial_nonStudentRole_returnsBadRequest()`.

---

##  Pruebas Unitarias

- Todas las funcionalidades fueron testeadas con **JUnit 5** y **Mockito**, cubriendo escenarios positivos y negativos.
- Se validó que los roles correctos tengan acceso y que los roles incorrectos devuelvan los códigos HTTP esperados (`403` o `400`).
- Mockeos realizados sobre `StudentService` para pruebas de controlador (`StudentControllerTest`).

---

## Endpoints

| Método | URL | Rol permitido | Descripción |
|--------|-----|---------------|------------|
| POST   | /api/students | ADMIN, DECANO | Registrar un estudiante |
| GET    | /api/students/{studentCode} | Todos | Consultar estudiante por código |
| GET    | /api/students | Todos | Listar estudiantes con filtros |
| PUT    | /api/students/{studentCode} | ADMIN, DECANO, ESTUDIANTE | Actualizar estudiante (parcial o completo según rol) |
| PATCH  | /api/students/{studentCode} | ESTUDIANTE | Actualizar datos personales |
| DELETE | /api/students/{studentCode} | ADMIN | Eliminar estudiante |

---

## Conclusión

Todos los criterios de aceptación están implementados y validados con pruebas unitarias.  
El sistema respeta la lógica de roles y maneja errores de forma consistente (`400` y `403`) según la operación y el rol del usuario.
