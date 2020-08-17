package py.gov.senatics.portal.rest.covid19.reporte;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import py.gov.senatics.portal.annotation.RolAllowed;
import py.gov.senatics.portal.annotation.Secured;
import py.gov.senatics.portal.business.covid19.PacienteBC;
import py.gov.senatics.portal.dto.covid19.reportes.NoReportaronUbicacionDTO;
import py.gov.senatics.portal.modelCovid19.Paciente;
import py.gov.senatics.portal.modelCovid19.PacienteDatosPersonalesBasicos;
import py.gov.senatics.portal.modelCovid19.admin.Rol;
import py.gov.senatics.portal.persistence.covid19.PacienteDAO;
import py.gov.senatics.portal.rest.covid19.generic.GenericEndpoint;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Path("covid19/reporte/pacientes/estadosalud")
@RequestScoped
@Secured
public class ReportePacientesEstadoSaludEndpoint extends ReportePacientesEndpoint {

    @Override
    protected List<NoReportaronUbicacionDTO> listToDto(List<Paciente> lista) {
        return lista.stream().map(p -> {
            NoReportaronUbicacionDTO dto = new NoReportaronUbicacionDTO();
            dto.setCedula(p.getCedula());
            dto.setNombreCompleto(p.getNombreCompleto());
            dto.setFechaUltimoReporte(p.getFechaUltimoReporteEstadoSalud());
            dto.setHorasRetraso( p.getFechaUltimoReporteEstadoSalud() != null ?
                    ((long) Math.floor((double) (Calendar.getInstance().getTimeInMillis() - p.getFechaUltimoReporteEstadoSalud().getTime()) / (1000 * 60 * 60))) : null
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
}
