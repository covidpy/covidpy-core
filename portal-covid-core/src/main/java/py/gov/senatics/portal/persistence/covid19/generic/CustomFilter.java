package py.gov.senatics.portal.persistence.covid19.generic;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;

public interface CustomFilter {

    Predicate filter(CriteriaBuilder builder, CriteriaQuery<?> query, ParameterExpression<String> paramExpr, String value);
}
