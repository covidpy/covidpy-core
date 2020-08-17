package py.gov.senatics.portal.rest.covid19.generic;

import static org.apache.commons.beanutils.BeanUtils.getProperty;
import static py.gov.senatics.portal.annotation.formulario.FormularioUtils.meetsCondition;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.Transient;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.beanutils.BeanUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CaseFormat;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import py.gov.senatics.portal.annotation.formulario.ConditionalField;
import py.gov.senatics.portal.annotation.formulario.ConditionalFieldCollection;
import py.gov.senatics.portal.annotation.formulario.FieldOption;
import py.gov.senatics.portal.annotation.formulario.FieldOptions;
import py.gov.senatics.portal.annotation.formulario.FormField;
import py.gov.senatics.portal.annotation.formulario.FormInputLength;
import py.gov.senatics.portal.annotation.formulario.validation.ConditionalNotNull;
import py.gov.senatics.portal.dto.covid19.formulario.CampoFormularioRenderDTO;
import py.gov.senatics.portal.dto.covid19.formulario.RenderConditionDto;
import py.gov.senatics.portal.modelCovid19.ReporteSalud;
import py.gov.senatics.portal.persistence.covid19.generic.BaseDAO;

public abstract class GenericEndpoint<T, TDao extends BaseDAO<T>, TDto>  {

    @Inject
    private Logger logger;

    @Inject
    protected TDao dao;

    @Context
    protected org.jboss.resteasy.spi.HttpResponse response;

    @GET
    @Path("form")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getFormulario() throws JsonProcessingException {

        List<Field> fields = Arrays.stream(ReporteSalud.class.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(FormField.class))
                .sorted(Comparator.comparingInt(f -> f.getAnnotation(FormField.class).fila()))
                .collect(Collectors.toList());

        List<List<CampoFormularioRenderDTO>> dto = new ArrayList<>();
        // TODO (pendiente)Los que tengan el mismo número de orden, van a una fila
        List<CampoFormularioRenderDTO> fila = null;
        Integer ordenActual = null;
        for (Field campo : fields) {
            FormField infoCampo = campo.getAnnotation(FormField.class);
            // Envolvemos en un array para después poder implementar las filas
            fila = new ArrayList<>();
            dto.add(fila);
            CampoFormularioRenderDTO campoDto = new CampoFormularioRenderDTO();
            campoDto.setIcon(infoCampo.icon());
            // Campo máximo toma de la validación Size
            if (campo.isAnnotationPresent(Size.class)) {
                campoDto.setFieldMaxLength(campo.getAnnotation(Size.class).max());
            }
            campoDto.setFieldName(campo.getName());
            campoDto.setFieldType(
                    CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, infoCampo.fieldType().name())
            );
            campoDto.setHintText(infoCampo.hintText());
            campoDto.setLabel(infoCampo.label());
            if (campo.isAnnotationPresent(FormInputLength.class)) {
                campoDto.setInputLength(campo.getAnnotation(FormInputLength.class).value());
            }
            // Es requerido toma de la anotación Not Null o ConditionalNotNull
            campoDto.setIsRequired(
                    campo.isAnnotationPresent(NotNull.class)
                            || campo.isAnnotationPresent(ConditionalNotNull.class)
            );
            campoDto.setPage(infoCampo.page());
            campoDto.setOptionsSource(
                    campo.isAnnotationPresent(FieldOptions.class) || campo.isAnnotationPresent(FieldOption.class) ?
                            new ObjectMapper().writeValueAsString(
                                    Arrays.stream(campo.getAnnotationsByType(FieldOption.class))
                                            .map(fieldOption -> {
                                                Map<String, String> option = new HashMap<>();
                                                option.put("id", fieldOption.id());
                                                option.put("descripcion", fieldOption.descripcion());
                                                return option;
                                            }).toArray()
                            ) :
                            infoCampo.optionsSource()
            );
            campoDto.setOptionsIdProp(infoCampo.optionsIdProp());
            campoDto.setOptionsTextProp(infoCampo.optionsTextProp());

            // Conditions
            if (campo.isAnnotationPresent(ConditionalFieldCollection.class) || campo.isAnnotationPresent(ConditionalField.class)) {
                campoDto.setConditions(
                        Arrays.stream(campo.getAnnotationsByType(ConditionalField.class))
                                .map(cf -> new RenderConditionDto(cf.conditionField(), cf.conditionValue()))
                                .collect(Collectors.toList())
                );
            }

            fila.add(campoDto);
        }

        return Response.ok(dto).build();
    }

    protected T setEntityFromDto(T entity, T dto) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        // Debemos guardar los que tienen la anotación FormField, no sean de sólo lectura
        // y si son campos condicionales, se cumpla su condición o sean transient
        List<Field> fields = Arrays.stream(ReporteSalud.class.getDeclaredFields())
                .filter(f ->
                        f.isAnnotationPresent(FormField.class) &&
                                (f.isAnnotationPresent(Transient.class) ||
                                        (
                                                !f.getAnnotation(FormField.class).readonly() &&
                                                        (
                                                                (
                                                                        !f.isAnnotationPresent(ConditionalField.class) &&
                                                                                !f.isAnnotationPresent(ConditionalFieldCollection.class)
                                                                ) || (
                                                                        // Si es condicional, debe cumplirse todas sus condiciones
                                                                        meetsCondition(f, dto)
                                                                )
                                                        )
                                        )
                                )
                ).sorted(Comparator.comparingInt(f -> f.getAnnotation(FormField.class).fila()))
                .collect(Collectors.toList());

        List<Field> transientConditionalFields =
                Arrays.stream(ReporteSalud.class.getDeclaredFields())
                        .filter(f ->
                                f.isAnnotationPresent(FormField.class) &&
                                        f.isAnnotationPresent(Transient.class) &&
                                        (f.isAnnotationPresent(ConditionalField.class) ||
                                                !f.isAnnotationPresent(ConditionalFieldCollection.class))
                                        && meetsCondition(f, dto)

                        )
                        .collect(Collectors.toList());

        for (Field field: fields) {
            FormField fieldInfo = field.getAnnotation(FormField.class);
            // Seteamos según modelField o por el nombre del campo
            BeanUtils.setProperty(
                    entity,
                    fieldInfo.modelField().isEmpty() ?
                            field.getName() :
                            fieldInfo.modelField(),
                    getProperty(
                            dto,
                            field.getName()
                    )
            );
        }
        for (Field field: transientConditionalFields) {
            // Si es transient, también seteamos el mismo
            BeanUtils.setProperty(
                    entity,
                    field.getName(),
                    getProperty(
                            dto,
                            field.getName()
                    )
            );
        }

        return entity;
    }

    protected List<TDto> listToDto(List<T> lista) {
        return (List<TDto>) lista;
    }

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(@QueryParam("page") Integer page,
                         @QueryParam("pageSize") Integer pageSize,
                         @QueryParam("filters") List<String> filters,
                         @QueryParam("orderBy") String orderBy,
                         @QueryParam("orderDesc") Boolean orderDesc,
                         @QueryParam("search") String search) {
        if (page == null) page = 0;
        if (pageSize == null) pageSize = Integer.MAX_VALUE;
        if (orderDesc == null) orderDesc = false;
        Long count = dao.getCount(filters, search);
        this.response.getOutputHeaders().putSingle("X-Total-Count", count);
        this.response.getOutputHeaders().putSingle("X-Total-Pages", (long)Math.ceil(((double)count)/pageSize));
        return Response.ok(
                listToDto(
                    dao.getList(page, pageSize, filters, orderBy, orderDesc, search)
                )
        ).build();
    }

    @Path("/csv")
    @GET
    public void csvList(@Context HttpServletResponse response,
                         @Context HttpServletRequest request,
                         @QueryParam("filters") List<String> filters,
                         @QueryParam("orderBy") String orderBy,
                         @QueryParam("orderDesc") boolean orderDesc,
                         @QueryParam("search") String search) {
        try {
            List<TDto> lista = listToDto(
                    dao.getList(0, Integer.MAX_VALUE, filters, orderBy, orderDesc, search)
            );

            ServletOutputStream os = response.getOutputStream();
            OutputStream buffOs = new BufferedOutputStream(os);
            /**
             * Se agrega encode, para poder abrir con los caracteres correctamente.
             */
            buffOs.write(0xef);
            buffOs.write(0xbb);
            buffOs.write(0xbf);
            OutputStreamWriter osWriter = new OutputStreamWriter(buffOs,"UTF-8");
            StatefulBeanToCsv<TDto> csvWriter = new StatefulBeanToCsvBuilder<TDto>(osWriter)
                    .withSeparator(';')
                    .build();
            csvWriter.write(lista);
            osWriter.flush();

        } catch (Exception e) {
            logger.warning("No se pudo generar archivo CSV " + e.getMessage());
        }
    }
}
