package py.gov.senatics.portal.dto.covid19.admin;

import py.gov.senatics.portal.validation.covid19.SamePassword;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@SamePassword
public class CambiarClaveDto {

    @NotNull(message = "Es requerido")
    @Size(min = 8, message = "Debe tener 8 caracteres como mínimo")
    private String password;

    @NotNull(message = "Es requerido")
    private String password2;

    public CambiarClaveDto() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }
}
