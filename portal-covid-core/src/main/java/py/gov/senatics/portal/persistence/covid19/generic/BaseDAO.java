package py.gov.senatics.portal.persistence.covid19.generic;

import org.apache.commons.lang3.ClassUtils;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * @param <T> Clase de entidad
 */
public abstract class BaseDAO<T> {


    @PersistenceContext(unitName = "covid19")
    protected EntityManager em;

    protected Class<T> entityType;

    protected Map<String, CustomFilter> customFilters;


    public BaseDAO() {
        Type genericSuperClass = getClass().getGenericSuperclass();

        ParameterizedType parametrizedType = null;
        while (parametrizedType == null) {
            if ((genericSuperClass instanceof ParameterizedType)) {
                parametrizedType = (ParameterizedType) genericSuperClass;
            } else {
                genericSuperClass = ((Class<?>) genericSuperClass).getGenericSuperclass();
            }
        }

        this.entityType = (Class<T>) parametrizedType.getActualTypeArguments()[0];
        this.customFilters = this.getCustomFilters();
    }

    public List<T> getList(int page, int pageSize, List<String> filters, String orderField, boolean orderDesc, String basicSearch) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(this.entityType);
        Root<T> root = query.from(this.entityType);

        this.executeListJoins(root);
        List<FiltersParameters> parameters = this.filterQuery(builder, query, filters, basicSearch);

        if (orderField != null && !orderField.trim().isEmpty()) {
            Map<String, CustomOrder> customOrder = getCustomOrder();
            Expression<Object> orderRootField;
            if (customOrder.containsKey(orderField)) {
                orderRootField = (Expression<Object>) customOrder.get(orderField).order(builder, query, orderDesc);
            } else {
                orderRootField = root.get(orderField);
            }
            query.orderBy(
                    orderDesc ?
                            builder.desc(orderRootField) :
                            builder.asc(orderRootField)
            );
        }

        TypedQuery<T> q = createListQuery(query, parameters);

        return q.setMaxResults(pageSize)
                .setFirstResult(page * pageSize).getResultList();
    }

    protected void executeListJoins(Root<T> root) {
        List<String> joinProperties = this.getJoinProperties();
        if (this.getJoinProperties() != null) {
            for (String prop: joinProperties) {
                String[] propSplit = prop.split("\\.");
                FetchParent<Object, Object> currentRoot = (FetchParent<Object, Object>) root;
                for (int i = 0; i < propSplit.length; i++) {
                    if (i < propSplit.length - 1) {
                        currentRoot = currentRoot.fetch(propSplit[i], JoinType.LEFT);
                    } else {
                        currentRoot.fetch(propSplit[i], JoinType.LEFT);
                    }
                }

            }
        }

    }

    protected List<FiltersParameters> filterQuery(CriteriaBuilder builder, CriteriaQuery<?> query, List<String> filters, String basicSearch) {
        Object[] roots = query.getRoots().toArray();
        Root<T> root = (Root<T>) roots[0];
        List<FiltersParameters> parameters = new ArrayList<>();
        Predicate advancedFilters = filters.stream().map(f -> f.split(":"))
                .filter(splitted -> splitted.length > 1)
                .map(splitted -> {
                    String fieldName = splitted[0];
                    String filterValue = splitted[1];
                    String comparator = splitted.length > 2 ? splitted[2] : "equal";
                    ParameterExpression<String> parameterExpr = builder.parameter(String.class);
                    parameters.add(new FiltersParameters(parameterExpr, filterValue));

                    if (this.customFilters != null && this.customFilters.containsKey(fieldName)) {
                        return this.customFilters.get(fieldName).filter(builder, query, parameterExpr, filterValue);
                    } else {
                        try {
                            Method comparatorMethod = CriteriaBuilder.class
                                    .getDeclaredMethod(comparator, Expression.class, Expression.class);
                            return (Predicate) comparatorMethod.invoke(builder, root.get(fieldName), parameterExpr);
                        } catch (NoSuchMethodException |
                                IllegalAccessException |
                                InvocationTargetException |
                                IllegalArgumentException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                }).filter(Objects::nonNull)
                .reduce(builder.conjunction(), builder::and);
        Predicate basicSearchPredicate = builder.conjunction();
        if (basicSearch != null && !basicSearch.trim().isEmpty()) {
            ParameterExpression<String> parameterExpr = builder.parameter(String.class);
            parameters.add(new FiltersParameters(parameterExpr, basicSearch));
            basicSearchPredicate = this.basicSearchWhere(builder, root, parameterExpr);
        }
        query.where(builder.and(basicSearchPredicate, advancedFilters));
        return parameters;
    }

    public void save(T entity) {
        em.persist(entity);
        em.flush();
    }

    /**
     * La búsqueda básica por defecto hace like de todos los campos
     * @param builder Query Builder
     * @param root Root
     * @param searchTerm String buscado
     * @return El Predicate a usar para la búsqueda básica
     */
    protected Predicate basicSearchWhere(
            CriteriaBuilder builder,
            Root<T> root,
            ParameterExpression<String> searchTerm) {
        return Arrays.stream(this.entityType.getDeclaredFields())
                .filter(f -> !f.isAnnotationPresent(Transient.class)
                        && !Modifier.isStatic(f.getModifiers())
                        && (ClassUtils.isPrimitiveOrWrapper(f.getType()) || f.getType().equals(String.class))
                )
                .map(f -> builder.like(root.get(f.getName()).as(String.class), searchTerm))
        .reduce(builder.disjunction(), builder::or);

    }

    public Long getCount(List<String> filters, String basicSearch) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<T> root = query.from(this.entityType);
        List<FiltersParameters> parameters = this.filterQuery(builder, query, filters, basicSearch);

        query.select(builder.count(root));
        TypedQuery<Long> q = createListQuery(query, parameters);

        try {
            return q.getSingleResult();
        } catch (NoResultException ex) {
            return 0L;
        }
    }

    protected Map<String, CustomFilter> getCustomFilters() {
        return null;
    }

    protected List<String> getJoinProperties () {
        return null;
    }

    protected Map<String, CustomOrder> getCustomOrder() {
        return new HashMap<>();
    }

    private <X> TypedQuery<X>createListQuery(CriteriaQuery<X> query, List<FiltersParameters> parameters) {
        TypedQuery<X> q = em.createQuery(query);
        for (FiltersParameters param : parameters) {
            q.setParameter(param.parameterExpr, param.value);
        }
        return q;
    }

    protected class FiltersParameters {
        private ParameterExpression<String> parameterExpr;
        private String value;

        public FiltersParameters(ParameterExpression<String> parameterExpr, String value) {
            this.parameterExpr = parameterExpr;
            this.value = value;
        }

        public ParameterExpression<String> getParameterExpr() {
            return parameterExpr;
        }

        public String getValue() {
            return value;
        }
    }
}
