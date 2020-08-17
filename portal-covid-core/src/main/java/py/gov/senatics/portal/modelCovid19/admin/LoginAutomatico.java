package py.gov.senatics.portal.modelCovid19.admin;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "login_automatico", schema = "covid19admin")
public class LoginAutomatico {

    public static final String ESTADO_ACTIVO = "activo";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    private String token;

    private String estado;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "timestamp_creacion")
    private Date timestampCreacion;

    public LoginAutomatico() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getTimestampCreacion() {
        return timestampCreacion;
    }

    public void setTimestampCreacion(Date timestampCreacion) {
        this.timestampCreacion = timestampCreacion;
    }
}
