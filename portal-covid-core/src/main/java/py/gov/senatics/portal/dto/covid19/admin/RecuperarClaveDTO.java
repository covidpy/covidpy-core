package py.gov.senatics.portal.dto.covid19.admin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RecuperarClaveDTO {

    @NotNull(message = "Es requerido")
    @Size(min = 1, message = "Es requerido")
    private String nroDocumento;

    @NotNull(message = "Es requerido")
    @Size(min = 1, message = "Es requerido")
    private String celular;

    public RecuperarClaveDTO() {
    }

    public String getNroDocumento() {
        return nroDocumento;
    }

    public void setNroDocumento(String nroDocumento) {
        this.nroDocumento = nroDocumento;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }
}
