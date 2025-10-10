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
## Funcionalidad: Registro de Decanos (CRUD)

La funcionalidad de gestión de decanos permite al Administrador crear, consultar, actualizar y eliminar decanos, mientras que los decanos solo pueden consultar su propia información. La implementación cumple con todos los criterios de aceptación establecidos.

### Criterios de aceptación y cumplimiento

1. **Creación de decanos**
    - **Criterio:** Dado que un Administrador accede al módulo de decanos, cuando crea un registro con nombre, facultad y correo institucional, entonces el sistema guarda correctamente el decano.
    - **Cumplimiento:** ✅ El endpoint `POST /api/deans` permite crear decanos, validando duplicados y solo accesible para Admin.

2. **Creación de decano duplicado**
    - **Criterio:** Dado que se intenta crear un decano duplicado, entonces el sistema rechaza la creación.
    - **Cumplimiento:** ✅ Se valida existencia por `employeeCode` y `institutionalEmail`; si existe, se lanza `CustomException`.

3. **Consulta de decanos**
    - **Criterio:** Dado que un Administrador consulta el módulo, entonces el sistema muestra todos los decanos registrados.
    - **Cumplimiento:** ✅ El endpoint `GET /api/deans` retorna todos los decanos para Admin; los decanos solo ven su registro.

4. **Consulta de decano propio**
    - **Criterio:** Dado que un Decano consulta su información, entonces el sistema muestra solo los datos de su facultad.
    - **Cumplimiento:** ✅ Endpoints `GET /api/deans/{id}` y `GET /api/deans/employee-code/{employeeCode}` filtran información según rol.

5. **Actualización de decanos**
    - **Criterio:** Dado que un Administrador edita un registro, cuando guarda, entonces el sistema actualiza correctamente.
    - **Cumplimiento:** ✅ Endpoints `PUT /api/deans/{id}` y `PATCH /api/deans/{id}` permiten actualización completa o parcial, solo para Admin.

6. **Bloqueo de edición para decanos**
    - **Criterio:** Dado que un Decano intenta modificar o eliminar, entonces el sistema bloquea la acción.
    - **Cumplimiento:** ✅ Se lanza `CustomException` si un decano intenta editar o eliminar.

7. **Eliminación de decanos**
    - **Criterio:** Dado que un Administrador elimina un registro, cuando confirma, entonces el sistema borra el decano.
    - **Cumplimiento:** ✅ Endpoint `DELETE /api/deans/{id}` realiza delete lógico (`active = false`) solo para Admin.

8. **Consultas por facultad, programa y estado**
    - **Criterio:** Los endpoints permiten filtrar decanos por facultad, programa o estado activo.
    - **Cumplimiento:** ✅ Métodos `getDeansByFaculty`, `getDeansByProgram` y `getActiveDeans` implementan los filtros y respetan roles.

### Pruebas unitarias y casos de validación

- **Happy Path (funcionamiento esperado):**
    1. Crear decano correctamente.
    2. Consultar todos los decanos como Admin.
    3. Consultar decano propio como Decano.
    4. Actualizar decano completo como Admin.
    5. Actualizar decano parcialmente como Admin.
    6. Eliminar decano como Admin.

- **Casos de error (bloqueos y validaciones):**
    1. Intentar crear decano duplicado → error.
    2. Decano intenta editar registro → bloqueado.
    3. Decano intenta eliminar registro → bloqueado.
    4. Consultar decano inexistente → error `NotFoundException`.

**Total de pruebas:** 10 (6 Happy Path + 4 Casos de error)
