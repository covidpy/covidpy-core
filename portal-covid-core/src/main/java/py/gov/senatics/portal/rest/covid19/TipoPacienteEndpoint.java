package py.gov.senatics.portal.rest.covid19;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;

import py.gov.senatics.portal.annotation.Secured;
import py.gov.senatics.portal.dto.covid19.TipoPacienteDTO;
import py.gov.senatics.portal.modelCovid19.TipoPacienteDiagnostico;
import py.gov.senatics.portal.persistence.covid19.TipoPacienteDiagnosticoDAO;
import py.gov.senatics.portal.rest.covid19.generic.GenericEndpoint;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@SuppressWarnings("restriction")
@Path("covid19/tipopaciente")
@RequestScoped
@Secured
public class TipoPacienteEndpoint extends GenericEndpoint<TipoPacienteDiagnostico, TipoPacienteDiagnosticoDAO, TipoPacienteDTO> {

    @Override
    public Response getFormulario() throws JsonProcessingException {
        throw new NotImplementedException();
    }

    @Override
    protected List<TipoPacienteDTO> listToDto(List<TipoPacienteDiagnostico> lista) {
        return lista.stream().map(p -> {
            TipoPacienteDTO dto = new TipoPacienteDTO();
            dto.setId(p.getId());
            dto.setDescripcion(p.getDescripcion());
            return dto;
        }).collect(Collectors.toList());

    }

    @Override
    public Response list(Integer page, Integer pageSize, List<String> filters, String orderBy, Boolean orderDesc, String search) {
        return super.list(page, pageSize, filters, orderBy, orderDesc, search);
    }

}
