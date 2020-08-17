package py.gov.senatics.portal.persistence.covid19.generic;

import javax.persistence.criteria.*;

public interface CustomOrder {

    Expression<?> order(CriteriaBuilder builder, CriteriaQuery<?> query, boolean desc);
}
