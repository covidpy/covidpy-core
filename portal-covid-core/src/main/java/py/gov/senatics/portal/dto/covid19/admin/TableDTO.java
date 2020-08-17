package py.gov.senatics.portal.dto.covid19.admin;

import java.util.List;

public class TableDTO<T> {

	private List<T> lista;

	private int totalRecords;

	public List<T> getLista() {
		return lista;
	}

	public void setLista(List<T> lista) {
		this.lista = lista;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

}
