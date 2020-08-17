package py.gov.senatics.portal.business.covid19;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import py.gov.senatics.portal.dto.covid19.admin.TableDTO;
import py.gov.senatics.portal.modelCovid19.CensoContacto;
import py.gov.senatics.portal.persistence.covid19.CensoContactoDAO;

@RequestScoped
public class CensoContactoBC {
	
	@Inject
	private CensoContactoDAO censoContactoDAO;
	
	public Response getContactosPaciente(Long idPaciente, int pageSize, int first, String sortField, boolean sortAsc, String filter) {

		TableDTO<CensoContacto> lista = new TableDTO<>();

		lista.setLista(this.censoContactoDAO.getContactosPaciente(idPaciente, pageSize, first, sortField, sortAsc, filter));
		lista.setTotalRecords(this.censoContactoDAO.obtenerCantidadDeFilas(idPaciente, filter));

		return Response.status(Status.OK).entity(lista).build();
	}
}