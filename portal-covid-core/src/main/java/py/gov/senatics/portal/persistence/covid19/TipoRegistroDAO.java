package py.gov.senatics.portal.persistence.covid19;

import py.gov.senatics.portal.modelCovid19.*;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

@Stateless
public class TipoRegistroDAO {

    @PersistenceContext(unitName = "covid19")
    protected EntityManager em;

    public TipoRegistro getMotivoIngreso(Paciente p) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<TipoRegistro> query = builder.createQuery(TipoRegistro.class);
        Root<FormSeccionDatosBasicos> formSeccionDatosBasicosRoot = query.from(FormSeccionDatosBasicos.class);
        Join<FormSeccionDatosBasicos, RegistroFormulario> registroFormularioJoin = formSeccionDatosBasicosRoot.join(FormSeccionDatosBasicos_.registroFormulario);
        Join<RegistroFormulario, Registro> registroJoin = registroFormularioJoin.join(RegistroFormulario_.registro);
        query.where(
                builder.equal(
                        formSeccionDatosBasicosRoot.get(FormSeccionDatosBasicos_.numeroDocumento),
                        p.getDatosPersonalesBasicos().getNumeroDocumento()
                )
        );
        query.select(registroJoin.get(Registro_.tipoRegistroFk));
        try {
            return em.createQuery(query).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
}
