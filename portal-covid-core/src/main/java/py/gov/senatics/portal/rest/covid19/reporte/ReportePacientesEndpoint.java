package py.gov.senatics.portal.rest.covid19.reporte;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;

import py.gov.senatics.portal.annotation.RolAllowed;
import py.gov.senatics.portal.annotation.Secured;
import py.gov.senatics.portal.dto.covid19.reportes.NoReportaronUbicacionDTO;
import py.gov.senatics.portal.modelCovid19.Paciente;
import py.gov.senatics.portal.modelCovid19.PacienteDatosPersonalesBasicos;
import py.gov.senatics.portal.modelCovid19.admin.Rol;
import py.gov.senatics.portal.persistence.covid19.PacienteDAO;
import py.gov.senatics.portal.rest.covid19.generic.GenericEndpoint;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Path("covid19/reporte/pacientes")
@RequestScoped
@Secured
public class ReportePacientesEndpoint extends GenericEndpoint<Paciente, PacienteDAO, NoReportaronUbicacionDTO> {

    @Override
    public Response getFormulario() throws JsonProcessingException {
        throw new NotImplementedException();
    }

    @Override
    protected List<NoReportaronUbicacionDTO> listToDto(List<Paciente> lista) {
        return lista.stream().map(p -> {
            NoReportaronUbicacionDTO dto = new NoReportaronUbicacionDTO();
            dto.setCedula(p.getCedula());
            dto.setNombreCompleto(p.getNombreCompleto());
            dto.setFechaUltimoReporte(p.getFechaUltimoReporteUbicacion());
            dto.setHorasRetraso(
                    p.getFechaUltimoReporteUbicacion() != null ?
                            ((long) Math.floor((double) (Calendar.getInstance().getTimeInMillis() - p.getFechaUltimoReporteUbicacion().getTime()) / (1000 * 60 * 60))) : null
            );
            dto.setTipoPaciente(p.getTipoPaciente().getDescripcion());
            PacienteDatosPersonalesBasicos datosPersonales = p.getDatosPersonalesBasicos();
            if (datosPersonales != null) {
                if(
                    datosPersonales.getFormSeccionDatosBasicos() != null &&
                        datosPersonales.getFormSeccionDatosBasicos().getRegistroFormulario() != null &&
                        datosPersonales.getFormSeccionDatosBasicos().getRegistroFormulario().getRegistro() != null &&
                        datosPersonales.getFormSeccionDatosBasicos().getRegistroFormulario().getRegistro().getTipoRegistroFk() != null
                ) {
                    dto.setTipoIngreso(datosPersonales.getFormSeccionDatosBasicos().getRegistroFormulario().getRegistro().getTipoRegistroFk().getDescripcion());
                }
                dto.setTelefono("+" + p.getDatosPersonalesBasicos().getNumeroCelular());
            }
            return dto;
        }).collect(Collectors.toList());

    }

    @Override
    @RolAllowed(Rol.REPORTES)
    public Response list(Integer page, Integer pageSize, List<String> filters, String orderBy, Boolean orderDesc, String search) {
        return super.list(page, pageSize, filters, orderBy, orderDesc, search);
    }
}
