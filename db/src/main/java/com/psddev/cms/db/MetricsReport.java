package com.psddev.cms.db;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.psddev.dari.db.Countable;
import com.psddev.dari.db.Grouping;
import com.psddev.dari.db.Modification;
import com.psddev.dari.db.ObjectField;
import com.psddev.dari.db.ObjectType;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.Record;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class MetricsReport extends Record {

    static Logger LOGGER = LoggerFactory.getLogger(MetricsReport.class);

    private transient Query<?> query;

    @Indexed
    private String name;

    private Date startDate;

    private Date endDate;

    //@ToolUi.InputProcessorPath("metricsObjectType.jsp")
    @ToolUi.Note("Select a type modified by the Countable Action modification")
    @Required
    private ObjectType contentType;

    //@ToolUi.InputProcessorPath("metricsObjectType.jsp")
    @ToolUi.Note("Select a modification that modifies Countable types")
    @DisplayName("Countable Action")
    @Required
    private ObjectType modificationType;

    @Embedded
    @Required
    private MetricsChart chart;

    //@ToolUi.Note("Select this to detail data for every date in the range. TO DO: replace this with date summary level")
    //private boolean dateDetail;

    private transient Class<? extends Modification<? extends Countable>> modificationClass;

    private transient ObjectField countField;

    private transient ObjectField eventDateField;

    private transient Set<ObjectField> availableFields;

    private transient Set<String> availableFieldNames;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public ObjectType getContentType() {
        return contentType;
    }

    public ObjectType getModificationType() {
        return modificationType;
    }

    public void setModificationType(ObjectType modificationType) {
        this.modificationType = modificationType;
    }

    public ObjectField getCountField() {
        if (countField == null)
            if (getModificationClass() != null)
                countField = Countable.CountAction.getCountField(getModificationClass());
        return countField;
    }

    public ObjectField getEventDateField() {
        if (eventDateField == null)
            if (getModificationClass() != null)
                eventDateField = Countable.CountAction.getEventDateField(getModificationClass());
        return eventDateField;
    }

    public String getActionSymbol() {
        if (getCountField() != null)
            return getCountField().getInternalName();
        return null;
    }

    public String getEventDateFieldName() {
        if (getEventDateField() != null)
            return getEventDateField().getInternalName();
        return null;
    }

    public MetricsChart getChart() {
        return chart;
    }

    public String getChartHtml() {
        if (!isReady())
            throw new RuntimeException("Check isReady() before you try to render the chart!");
        return getChart().getHtml();
    }

    public String getChartScript() {
        if (!isReady())
            throw new RuntimeException("Check isReady() before you try to render the chart!");
        return getChart().getScript();
    }

    public String getChartStyle() {
        if (!isReady())
            throw new RuntimeException("Check isReady() before you try to render the chart!");
        return getChart().getStyle();
    }

    //public boolean isDateDetail() {
    //    return dateDetail;
    //}

    private Class<? extends Modification<? extends Countable>> getModificationClass() {
        if (modificationClass == null) {
            if (modificationType != null) {
                @SuppressWarnings("unchecked")
                Class<? extends Modification<? extends Countable>> modClass = (Class<? extends Modification<? extends Countable>>) modificationType.getObjectClass();
                modificationClass = modClass;
            }
        }
        return modificationClass;
    }

    @Override
    public void beforeSave() {
        if (getModificationClass() != null) {
            try {
                getCountField();
                getEventDateField();
            } catch (RuntimeException ex) {
                ObjectField field = this.getState().getField("modificationType");
                this.getState().addError(field, "Countable Action must be a modification on types that extend Countable");
            }
        }
        if (getContentType() != null) {
            if (! contentType.getGroups().contains(Countable.class.getName())) {
                ObjectField field = this.getState().getField("contentType");
                this.getState().addError(field, "Content Type must extend Countable");
            }
            if (! contentType.getModificationClassNames().contains(modificationType.getObjectClass().getName())) {
                ObjectField field = this.getState().getField("contentType");
                this.getState().addError(field, "Content Type must have the modification " + modificationType.getObjectClass().getName());
            }
        }
        if (getChart() != null) {
            getChart().setReport(this);
        }
    }

    public String getNotReadyMessage() {
        String msg = null;
        if (getCountField() == null || getEventDateField() == null) {
            // beforeSave should prevent this from happening.
            msg = "This report contains an invalid Countable Action.";
        } else if (getChart() == null) {
            msg = "Choose a chart.";
        } else {
            msg = getChart().getNotReadyMessage();
        }
        return msg;
    }

    public boolean isReady() {
        if (getNotReadyMessage() != null) return false;
        return true;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<Grouping<?>> getRows() {
        // If I don't do this *sometimes* the chart doesn't know about the report. Just set it here to be sure, this will always be executed.
        if (this.getChart().getReport() != this) this.getChart().setReport(this);
        List<String> groupBy = this.getChart().getRowKeys();
        String[] groupByArray = groupBy.toArray(new String[groupBy.size()]);
        return (List) getQuery().groupBy(groupByArray);
    }

    public Query<?> getQuery() {
        if (query == null) {
            query = Query.fromType(contentType);
            query.counting(getActionSymbol());
            if (getStartDate() != null)
                query.and(getEventDateFieldName() + " > ?", getStartDate());
            if (getEndDate() != null)
                query.and(getEventDateFieldName() + " <= ?", getEndDate());
        }
        return query;
    }

    public Set<ObjectField> getAvailableFields(boolean includeEventDate) {
        if (availableFields == null) {
            availableFields = new HashSet<ObjectField>();
            if (getContentType() != null) {
                for (ObjectField field : contentType.getIndexedFields()) {
                    if (field.as(Countable.CountableFieldData.class).isDimension() ||
                            field.as(Countable.CountableFieldData.class).isEventDateField()) {
                        if (field.getJavaDeclaringClassName().equals(getModificationType().getObjectClassName())) {
                            if (includeEventDate || ! field.as(Countable.CountableFieldData.class).isEventDateField()) {
                                availableFields.add(field);
                            }
                        }
                    } else if (field.as(Countable.CountableFieldData.class).isCountField()) {
                        // not a dimension
                    } else {
                        availableFields.add(field);
                    }
                }
            }
        }
        return availableFields;
    }

    public Set<String> getAvailableFieldNames(boolean includeEventDate) {
        if (availableFieldNames == null) {
            availableFieldNames = new HashSet<String>();
            for (ObjectField availableField : getAvailableFields(includeEventDate)) {
                availableFieldNames.add(availableField.getInternalName());
            }
        }
        return availableFieldNames;
    }

    public Set<String> getAvailableFieldNames() {
        return getAvailableFieldNames(true);
    }

    public Set<ObjectField> getAvailableFields() {
        return getAvailableFields(true);
    }

}

