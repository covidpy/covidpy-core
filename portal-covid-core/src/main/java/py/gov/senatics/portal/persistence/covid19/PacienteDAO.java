package py.gov.senatics.portal.persistence.covid19;

import py.gov.senatics.portal.cache.ConfiguracionCache;
import py.gov.senatics.portal.modelCovid19.*;
import py.gov.senatics.portal.modelCovid19.admin.Usuario;
import py.gov.senatics.portal.modelCovid19.admin.Usuario_;
import py.gov.senatics.portal.persistence.covid19.generic.BaseDAO;
import py.gov.senatics.portal.persistence.covid19.generic.CustomFilter;
import py.gov.senatics.portal.persistence.covid19.generic.CustomOrder;
import py.gov.senatics.portal.session.UserManager;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Stateless
public class PacienteDAO  extends BaseDAO<Paciente> {

    @Inject
    private UserManager userManager;

    @Inject
    private ConfiguracionCache conf;

    public Paciente getPacienteAutenticado() {

        Usuario usuario = userManager.getRequestUser();

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Paciente> query = builder.createQuery(Paciente.class);
        Root<Paciente> root = query.from(Paciente.class);
        Join<Object, Object> userRoot =  root.join("usuario");
        query.where(builder.equal(userRoot.get("id"), usuario.getId()));

        return em.createQuery(query).getSingleResult();
    }
    
    public Boolean esPaciente(Usuario usuario) {
    	String queryString = "SELECT p FROM Paciente p WHERE p.usuario.id =:usuario ";
    	Paciente paciente = null;
    	try{
    		TypedQuery<Paciente> q = em.createQuery(queryString, Paciente.class);
    		q.setParameter("usuario", usuario.getId());
    		paciente = q.getSingleResult();
    	}catch(NoResultException nre) {
			//System.out.println("Nulo ");
		}
		
		if(paciente != null) {
			return true;
		}
		return false;
		
    }

    @Override
    public void save(Paciente paciente)
    {
    	em.persist(paciente);
    }

    public void update(Paciente paciente)
    {
    	em.merge(paciente);
    	em.flush();
    }

    @Override
    protected Map<String, CustomFilter> getCustomFilters() {
        Map<String, CustomFilter> filters = new HashMap<>();
        filters.put("ubicacionNoReportada", (builder, query, paramExpr, value) -> {
            Object[] roots = query.getRoots().toArray();
            Root<Paciente> root = (Root<Paciente>) roots[0];
            Join<Paciente, TipoPacienteDiagnostico> pacienteDiagnosticoRoot = root.join(Paciente_.tipoPaciente);
            Join<Paciente, PacienteDatosPersonalesBasicos> rootDatosPersonales = root.join(Paciente_.datosPersonalesBasicos);

            Subquery<Paciente> subqueryUbicaciones = query.subquery(Paciente.class);
            Root<RegistroUbicacion> rootUbicacion = subqueryUbicaciones.from(RegistroUbicacion.class);
            Join<RegistroUbicacion, Paciente> rootPaciente = rootUbicacion.join(RegistroUbicacion_.paciente);
            Join<Paciente, TipoPacienteDiagnostico> tipoPacienteRoot = rootPaciente.join(Paciente_.tipoPaciente);
            subqueryUbicaciones.select(rootUbicacion.get(RegistroUbicacion_.paciente));
            subqueryUbicaciones.groupBy(rootUbicacion.get(RegistroUbicacion_.paciente), tipoPacienteRoot.get(TipoPacienteDiagnostico_.frecuenciaReporteUbicacionHoras));

            Expression<Long> horasReportePath = tipoPacienteRoot.get(TipoPacienteDiagnostico_.frecuenciaReporteUbicacionHoras).as(Long.class);
            subqueryUbicaciones.where(
                    builder.isTrue(tipoPacienteRoot.get(TipoPacienteDiagnostico_.debeReportarUbicacion))
            );
            // timestampCreacion - now > frecuenciaReporteUbicacion
            subqueryUbicaciones.having(
                    builder.ge(
                            builder.least(rootUbicacion.get(RegistroUbicacion_.horasRetraso)),
                            horasReportePath
                    )
            );

            return builder.or(
                    builder.notEqual(paramExpr, paramExpr), // Se debe incluir la expresion para que no dé NullPointerException
                    builder.and(
                            builder.equal(rootDatosPersonales.get(PacienteDatosPersonalesBasicos_.numeroCelularVerificado), PacienteDatosPersonalesBasicos.ESTADO_CELULAR_VERIFICADO),
                            builder.or(
                                    builder.in(root).value(subqueryUbicaciones),
                                    builder.and(
                                            builder.isEmpty(root.get(Paciente_.ubicaciones)),
                                            builder.isTrue(pacienteDiagnosticoRoot.get(TipoPacienteDiagnostico_.debeReportarUbicacion))
                                    )
                            )
                    )
            );
        });
        filters.put("motivoIngreso", (builder, query, paramExpr, value) -> {
            Join<RegistroFormulario, Registro> registroJoin = getMotivoIngresoJoin(query);
            return builder.equal(registroJoin.get(Registro_.tipoRegistro), paramExpr);
        });
        filters.put("estadoSaludNoReportado", (builder, query, paramExpr, value) -> {
            Object[] roots = query.getRoots().toArray();
            Root<Paciente> root = (Root<Paciente>) roots[0];
            Join<Paciente, TipoPacienteDiagnostico> pacienteDiagnosticoRoot = root.join(Paciente_.tipoPaciente);
            Join<Paciente, PacienteDatosPersonalesBasicos> rootDatosPersonales = root.join(Paciente_.datosPersonalesBasicos);

            Subquery<Paciente> subqueryReporte = query.subquery(Paciente.class);
            Root<ReporteSalud> rootReporteSalud = subqueryReporte.from(ReporteSalud.class);
            Join<ReporteSalud, RegistroFormulario> rootRegistroFormulario = rootReporteSalud.join(ReporteSalud_.registroFormulario);
            Join<RegistroFormulario, Paciente> rootPaciente = rootRegistroFormulario.join(RegistroFormulario_.paciente);

            Join<Paciente, TipoPacienteDiagnostico> tipoPacienteRoot = rootPaciente.join(Paciente_.tipoPaciente);

            subqueryReporte.select(rootRegistroFormulario.get(RegistroFormulario_.paciente));
            subqueryReporte.groupBy(rootRegistroFormulario.get(RegistroFormulario_.paciente), tipoPacienteRoot.get(TipoPacienteDiagnostico_.frecuenciaReporteSaludHoras));

            Expression<Long> horasReportePath = tipoPacienteRoot.get(TipoPacienteDiagnostico_.frecuenciaReporteSaludHoras).as(Long.class);
            subqueryReporte.where(
                    builder.isTrue(tipoPacienteRoot.get(TipoPacienteDiagnostico_.debeReportarEstadoSalud))
            );
            // timestampCreacion - now > frecuenciaReporteEstadoSalud
            subqueryReporte.having(
                    builder.ge(
                            builder.least(rootReporteSalud.get(ReporteSalud_.horasRetraso)),
                            horasReportePath
                    )
            );

            return builder.or(
                    builder.notEqual(paramExpr, paramExpr), // Se debe incluir la expresion para que no dé NullPointerException
                    builder.and(
                            builder.equal(rootDatosPersonales.get(PacienteDatosPersonalesBasicos_.numeroCelularVerificado), PacienteDatosPersonalesBasicos.ESTADO_CELULAR_VERIFICADO),
                            builder.or(
                                    builder.in(root).value(subqueryReporte),
                                    builder.and(
                                            builder.isEmpty(root.get(Paciente_.formularios)),
                                            builder.isTrue(pacienteDiagnosticoRoot.get(TipoPacienteDiagnostico_.debeReportarEstadoSalud)
                                    )
                                )
                            )
                    )
            );
        });
        filters.put("cedula", (builder, query, paramExpr, value) -> {
            Object[] roots = query.getRoots().toArray();
            Root<Paciente> root = (Root<Paciente>) roots[0];
            Join<Paciente, PacienteDatosPersonalesBasicos> datosPersonalesBasicosJoin = root.join(Paciente_.datosPersonalesBasicos, JoinType.LEFT);
            return builder.equal(datosPersonalesBasicosJoin.get(PacienteDatosPersonalesBasicos_.numeroDocumento), paramExpr);
        });
        try {
            filters.put("horaRetrasoMinimo", filtroHorasRetrasoUbicacion("ge"));
            filters.put("horaRetrasoMaximo", filtroHorasRetrasoUbicacion("le"));
            filters.put("horaRetrasoMinimoEstadoSalud", this.filtroHorasRetrasoEstadoSalud("ge"));
            filters.put("horaRetrasoMaximoEstadoSalud", this.filtroHorasRetrasoEstadoSalud("le"));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return filters;
    }


    private CustomFilter filtroHorasRetrasoUbicacion(String comparador) throws NoSuchMethodException {
        Method comparadorMethod = CriteriaBuilder.class.getDeclaredMethod(comparador, Expression.class, Expression.class);
        return ((builder, query, paramExpr, value) -> {
            Object[] roots = query.getRoots().toArray();
            Root<Paciente> root = (Root<Paciente>) roots[0];

            Subquery<Paciente> subqueryUbicaciones = query.subquery(Paciente.class);
            Root<RegistroUbicacion> rootUbicacion = subqueryUbicaciones.from(RegistroUbicacion.class);
            subqueryUbicaciones.select(rootUbicacion.get(RegistroUbicacion_.paciente));
            subqueryUbicaciones.groupBy(rootUbicacion.get(RegistroUbicacion_.paciente));

            // timestampCreacion - now > frecuenciaReporteUbicacion
            try {
                subqueryUbicaciones.having(
                        (Predicate) comparadorMethod.invoke(builder,
                                builder.min(
                                        rootUbicacion.get(RegistroUbicacion_.horasRetraso)
                                ),
                                paramExpr.as(Integer.class)
                        )
                );
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            return builder.in(root).value(subqueryUbicaciones);
        });
    }

    private CustomFilter filtroHorasRetrasoEstadoSalud(String comparador) throws NoSuchMethodException {
        Method comparadorMethod = CriteriaBuilder.class.getDeclaredMethod(comparador, Expression.class, Expression.class);
        return (builder, query, paramExpr, value) -> {
            Object[] roots = query.getRoots().toArray();
            Root<Paciente> root = (Root<Paciente>) roots[0];

            Subquery<Paciente> subqueryReporte = query.subquery(Paciente.class);
            Root<ReporteSalud> rootReporteSalud = subqueryReporte.from(ReporteSalud.class);
            Join<ReporteSalud, RegistroFormulario> rootRegistroFormulario = rootReporteSalud.join(ReporteSalud_.registroFormulario);

            subqueryReporte.select(rootRegistroFormulario.get(RegistroFormulario_.paciente));
            subqueryReporte.groupBy(rootRegistroFormulario.get(RegistroFormulario_.paciente));

            // timestampCreacion - now > frecuenciaReporteEstadoSalud
            try {
                subqueryReporte.having(
                        (Predicate) comparadorMethod.invoke(builder,
                                builder.min(
                                        rootReporteSalud.get(ReporteSalud_.horasRetraso)
                                ),
                                paramExpr.as(Integer.class)
                        )
                );
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            return  builder.in(root).value(subqueryReporte);
        };
    }

    @Override
    protected List<String> getJoinProperties() {
        List<String> list = new ArrayList<>();
        list.add(Paciente_.usuario.getName());
        list.add(Paciente_.tipoPaciente.getName());
        list.add(
                Paciente_.datosPersonalesBasicos.getName() + "." +
                    PacienteDatosPersonalesBasicos_.formSeccionDatosBasicos.getName() + "." +
                    FormSeccionDatosBasicos_.registroFormulario.getName() + "." +
                    RegistroFormulario_.registro.getName() + "." +
                    Registro_.tipoRegistroFk.getName()
        );
        return list;
    }

    @Override
    protected Predicate basicSearchWhere(CriteriaBuilder builder, Root<Paciente> root, ParameterExpression<String> searchTerm) {
        Join<Object, Object> usuarioRoot = root.join("usuario");
        return builder.or(
                builder.like(
                        builder.lower(builder.concat(usuarioRoot.get("nombre"),
                                builder.concat(" ", usuarioRoot.get("apellido"))
                        )),
                        builder.concat("%", builder.concat(builder.lower(searchTerm), "%"))
                ),
                builder.like(usuarioRoot.get("cedula"), searchTerm)
        );
    }

    @Override
    protected Map<String, CustomOrder> getCustomOrder() {
        HashMap<String, CustomOrder> map = new HashMap<>();
        map.put("nombreCompleto", (builder, query, desc) -> {
            Root<Paciente> root = (Root<Paciente>) query.getRoots().toArray()[0];
            Join<Paciente, Usuario> usuarioRoot = root.join(Paciente_.usuario);
            return builder.concat(usuarioRoot.get(Usuario_.nombre),
                    builder.concat(" ", usuarioRoot.get(Usuario_.apellido))
            );
        });
        map.put("cedula", (builder, query, desc) -> {
            Root<Paciente> root = (Root<Paciente>) query.getRoots().toArray()[0];
            Join<Paciente, Usuario> usuarioRoot = root.join(Paciente_.usuario);
            return usuarioRoot.get(Usuario_.cedula);
        });
        map.put("telefono", (builder, query, desc) -> {
            Root<Paciente> root = (Root<Paciente>) query.getRoots().toArray()[0];
            Join<Paciente, PacienteDatosPersonalesBasicos> join = root.join(Paciente_.datosPersonalesBasicos);
            return join.get(PacienteDatosPersonalesBasicos_.numeroCelular);
        });
        map.put("tipoIngreso", (builder, query, desc) -> {
            Join<RegistroFormulario, Registro> registroJoin = getMotivoIngresoJoin(query);
            Join<Registro, TipoRegistro> join = registroJoin.join(Registro_.tipoRegistroFk, JoinType.LEFT);
            return join.get(TipoRegistro_.descripcion);
        });
        map.put("tipoPaciente", (builder, query, desc) -> {
            Object[] roots = query.getRoots().toArray();
            Root<Paciente> root = (Root<Paciente>) roots[0];
            Join<Paciente, TipoPacienteDiagnostico> join = root.join(Paciente_.tipoPaciente);
            return join.get(TipoPacienteDiagnostico_.descripcion);
        });
        CustomOrder fechaOrden = (builder, query, desc) -> {
            Object[] roots = query.getRoots().toArray();
            Root<Paciente> root = (Root<Paciente>) roots[0];
            return root.get(Paciente_.fechaUltimoReporteUbicacion);
        };
        CustomOrder fechaOrdenEstadoSalud = (builder, query, desc) -> {
            Object[] roots = query.getRoots().toArray();
            Root<Paciente> root = (Root<Paciente>) roots[0];
            return root.get(Paciente_.fechaUltimoReporteEstadoSalud);
        };
        map.put("fechaUltimoReporteUbicacion", fechaOrden);
        map.put("horasRetraso", fechaOrden);
        map.put("fechaUltimoReporteEstadoSalud", fechaOrdenEstadoSalud);
        map.put("horasRetrasoEstadoSalud", fechaOrdenEstadoSalud);
        return map;
    }

    private Join<RegistroFormulario, Registro> getMotivoIngresoJoin(CriteriaQuery<?> query) {
        Object[] roots = query.getRoots().toArray();
        Root<Paciente> root = (Root<Paciente>) roots[0];
        Join<Paciente, PacienteDatosPersonalesBasicos> datosPersonalesBasicosJoin = root.join(Paciente_.datosPersonalesBasicos, JoinType.LEFT);
        Join<PacienteDatosPersonalesBasicos, FormSeccionDatosBasicos> formSeccionDatosBasicosJoin = datosPersonalesBasicosJoin.join(PacienteDatosPersonalesBasicos_.formSeccionDatosBasicos, JoinType.LEFT);
        Join<FormSeccionDatosBasicos, RegistroFormulario> registroFormularioJoin = formSeccionDatosBasicosJoin.join(FormSeccionDatosBasicos_.registroFormulario, JoinType.LEFT);
        return registroFormularioJoin.join(RegistroFormulario_.registro, JoinType.LEFT);
    }
}
