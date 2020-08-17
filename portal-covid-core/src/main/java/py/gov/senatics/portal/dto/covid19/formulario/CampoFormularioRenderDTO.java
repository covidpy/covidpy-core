package py.gov.senatics.portal.dto.covid19.formulario;

import java.util.List;

public class CampoFormularioRenderDTO {
    private String hintText;
    private String fieldName;
    private String fieldType;
    private Integer fieldMaxLength;
    private Integer inputLength;
    private Boolean isRequired;
    private String optionsSource;
    private String optionsTextProp;
    private String optionsIdProp;
    private String label;
    private String icon;
    private Integer page;
    private List<OpcionCampoRenderDTO> options;
    private List<RenderConditionDto> conditions;

    public CampoFormularioRenderDTO() {
    }

    public String getHintText() {
        return hintText;
    }

    public void setHintText(String hintText) {
        this.hintText = hintText;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public Integer getFieldMaxLength() {
        return fieldMaxLength;
    }

    public void setFieldMaxLength(Integer fieldMaxLength) {
        this.fieldMaxLength = fieldMaxLength;
    }

    public Integer getInputLength() {
        return inputLength;
    }

    public void setInputLength(Integer inputLength) {
        this.inputLength = inputLength;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean required) {
        isRequired = required;
    }

    public String getOptionsSource() {
        return optionsSource;
    }

    public void setOptionsSource(String optionsSource) {
        this.optionsSource = optionsSource;
    }

    public String getOptionsTextProp() {
        return optionsTextProp;
    }

    public void setOptionsTextProp(String optionsTextProp) {
        this.optionsTextProp = optionsTextProp;
    }

    public String getOptionsIdProp() {
        return optionsIdProp;
    }

    public void setOptionsIdProp(String optionsIdProp) {
        this.optionsIdProp = optionsIdProp;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<OpcionCampoRenderDTO> getOptions() {
        return options;
    }

    public void setOptions(List<OpcionCampoRenderDTO> options) {
        this.options = options;
    }

    public List<RenderConditionDto> getConditions() {
        return conditions;
    }

    public void setConditions(List<RenderConditionDto> conditions) {
        this.conditions = conditions;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
}
