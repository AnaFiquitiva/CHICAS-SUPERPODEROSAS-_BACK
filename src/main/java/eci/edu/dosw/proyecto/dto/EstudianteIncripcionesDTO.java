@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
class EstudianteConInscripcionesDTO extends EstudianteResponseDTO {
    private List<InscripcionSummaryDTO> inscripcionesActivas;
    private List<SolicitudSummaryDTO> solicitudesPendientes;
}