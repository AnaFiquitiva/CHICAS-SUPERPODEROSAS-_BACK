@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
class DecanoConSolicitudesDTO extends DecanoResponseDTO {
    private List<SolicitudSummaryDTO> solicitudesAsignadas;
    private List<MateriaSummaryDTO> materiasGestionadas;
}