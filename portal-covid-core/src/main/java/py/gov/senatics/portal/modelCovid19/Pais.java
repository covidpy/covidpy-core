package py.gov.senatics.portal.modelCovid19;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Dataset obtenido de https://raw.githubusercontent.com/umpirsky/country-list/master/data/es_PY/country.postgresql.sql
 */
@Entity
@Table(name = "pais", schema = "covid19")
public class Pais {

    @Id
    private String id;

    private String value;

    public Pais() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
