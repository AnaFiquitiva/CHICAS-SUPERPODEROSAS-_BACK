@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
class ProfesorConGruposDTO extends ProfesorResponseDTO {
    private List<GrupoSummaryDTO> gruposAsignados;
}