package com.psddev.cms.db;

import java.util.ArrayList;
//import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.psddev.dari.db.Grouping;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.ObjectField;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.StringUtils;
//import com.psddev.dari.db.Countable;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


public abstract class MetricsChart extends Record {

    static Logger LOGGER = LoggerFactory.getLogger(MetricsChart.class);

    @ToolUi.Hidden
    MetricsReport report;

    private transient String htmlId;

    public MetricsReport getReport() {
        return this.report;
    }

    public void setReport(MetricsReport report) {
        this.report = report;
    }

    String getHtmlId() {
        if (htmlId == null) {
            htmlId = "chart_" + StringUtils.hex(StringUtils.md5(Double.toString(Math.random())));
        }
        return htmlId;
    }

    ///**
    // * Filter field names (strings) against the available field names in the report's chosen contentType and modificationType
    // */
    //List<String> filterFieldNames(List<String> fieldNames, int limit) {
    //    List<String> filteredFieldNames = new ArrayList<String>();
    //    Set<String> available = getReport().getAvailableFieldNames(true);
    //    for (String fieldName : fieldNames) {
    //        if (available.contains(fieldName)) {
    //            filteredFieldNames.add(fieldName);
    //        } else {
    //            for (String availableFieldName : available) {
    //                if (fieldName.toLowerCase().equals(availableFieldName.toLowerCase())) {
    //                    filteredFieldNames.add(availableFieldName);
    //                    break;
    //                }
    //            }
    //        }
    //        if (limit > 0 && filteredFieldNames.size() == limit) {
    //            break;
    //        }
    //    }
    //    return filteredFieldNames;
    //}

    //String filterFieldNamesString(String fieldNames, int limit) {
    //    if (fieldNames == null) return null;
    //    return StringUtils.join(filterFieldNames(Arrays.asList(StringUtils.split(fieldNames.trim(), "\\s*,\\s*")), limit), ", ");
    //}

    public Integer getKeyIndex(String key) {
        for (int i = 0; i < getRowKeys().size(); i++) {
            if (key.equals(getRowKeys().get(i))) {
                return i;
            }
        }
        return null;
    }

    public Object getValueOf(Object value) {
        if (value == null) {
            return "empty";
        } else if (value instanceof Date) {
            // seconds
            return ((Date) value).getTime();
        } else {
            return value.toString();
        }
    }

    public String getHtml() {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<div id=\"");
        htmlBuilder.append(getHtmlId());
        htmlBuilder.append("\">");
        htmlBuilder.append("<svg></svg>");
        htmlBuilder.append("</div>");
        return htmlBuilder.toString();
    }

    public Set<ObjectField> getAvailableFields() {
        if (getReport() == null) return null;
        return getReport().getAvailableFields(false);
    }

    public String getStyle() { 
        StringBuilder cssBuilder = new StringBuilder();
        cssBuilder.append("#");
        cssBuilder.append(getHtmlId());
        cssBuilder.append(" svg {");
        cssBuilder.append(getSvgCss());
        cssBuilder.append("}");
        return cssBuilder.toString();
    }

    String getSvgCss() {
        return "height: 400px;";
    }

    public static String getDataScript(Object values) {
        StringBuilder data = new StringBuilder();
        data.append("return ");
        data.append(ObjectUtils.toJson(values));
        data.append(";");
        return data.toString();
    }

    abstract String getScript();
    abstract String getData();
    abstract List<String> getRowKeys();
    abstract String getNotReadyMessage();

}

///// XY CHARTS

abstract class StandardXYChart extends MetricsChart {

    @ToolUi.InputProcessorPath("metricsIndexableFieldsDropdown.jsp")
    String groupFieldName;

    @ToolUi.DisplayName("Cumulative Over Dates?")
    boolean cumulative;

    @Override
    public List<String> getRowKeys() {
        List<String> groupBy = new ArrayList<String>();

        if (groupFieldName != null)
            groupBy.add(groupFieldName);

        if (isDateOnXAxis())
            groupBy.add(getReport().getEventDateFieldName());

        return groupBy;
    }

    boolean isDateOnXAxis() {
        return true;
    }

    boolean isCumulative() {
        return cumulative;
    }

    @Override
    public String getNotReadyMessage() {
        if (getReport() == null) return "No report set.";
        return null;
    }

    @Override
    public String getData() {
        String groupKey, xAxisKey;
        if (groupFieldName != null) {
            groupKey = groupFieldName;
        } else {
            groupKey = getReport().getCountField().getDisplayName();
        }
        if (isDateOnXAxis()) {
            xAxisKey = getReport().getEventDateFieldName();
        } else {
            xAxisKey = "All Dates";
        }
        return getDataScript(getDataGroupedXYMaps(xAxisKey, groupKey));
    }

    @SuppressWarnings("unchecked")
    public List<Map<String,Object>> getDataGroupedXYMaps(String xKey, String groupKey) {
        List<Grouping<?>> rows = getReport().getRows();

        Integer groupIndex = getKeyIndex(groupKey);
        Integer xAxisIndex = getKeyIndex(xKey);

        Map<Object, Map<String,Object>> values = new HashMap<Object, Map<String,Object>>();
        for (Grouping<?> row : rows) {
            Object groupValue, xAxisValue;
            if (groupIndex != null) {
                groupValue = getValueOf(row.getKeys().get(groupIndex));
            } else {
                groupValue = groupKey;
            }
            if (xAxisIndex != null) {
                xAxisValue = getValueOf(row.getKeys().get(xAxisIndex));
            } else {
                xAxisValue = xKey;
            }
            if (! values.containsKey(groupValue)) {
                List<Map<String, Object>> valuesList = new ArrayList<Map<String, Object>>();
                Map<String, Object> valuesMap = new HashMap<String, Object>();
                valuesMap.put("key", groupValue);
                valuesMap.put("values", valuesList);
                values.put(groupValue, valuesMap);
            }
            long count = row.getCount();
            Map<String, Object> pointValues = new HashMap<String, Object>();
            pointValues.put("x", xAxisValue);
            pointValues.put("y", count);
            ((List<Map<String, Object>>) values.get(groupValue).get("values")).add(pointValues);
        }

        List<Map<String,Object>> valuesList = new ArrayList<Map<String,Object>>(values.values());
        Collections.sort(valuesList, new KeySorterComparator());
        if (isCumulative()) {
            for (Map<String, Object> groupMap : valuesList) {
                List<Map<String, Long>> groupValues = (List<Map<String, Long>>) groupMap.get("values");
                //LOGGER.info("===== rowValues: " + groupValues);
                Long cumulativeValue = 0L;
                for (Map<String, Long> rowValues : groupValues) {
                    cumulativeValue += rowValues.get("y");
                    rowValues.put("y", cumulativeValue);
                }
            }
        }
        return valuesList;
    }

    class KeySorterComparator implements Comparator<Map<String,Object>> {
        public int compare(Map<String, Object> lhs, Map<String, Object> rhs) {
            return lhs.get("key").toString().compareTo(rhs.get("key").toString());
        }
    }

}

@ToolUi.DisplayName("Grouped/Stacked Bar Chart")
class GroupedStackedBarChart extends StandardXYChart {

    @ToolUi.DisplayName("Swap Date and Group?")
    boolean swapDateAndGroup;

    @ToolUi.DisplayName("Display Date?")
    boolean dateOnXAxis;

    @Override
    boolean isDateOnXAxis() {
        return dateOnXAxis;
    }

    @Override
    public String getScript() {
        StringBuilder scriptBuilder = new StringBuilder();
        scriptBuilder.append("nv.addGraph(function() {");
        scriptBuilder.append("var chart = nv.models.multiBarChart();");

        scriptBuilder.append("chart.xAxis");
        if (dateOnXAxis && !swapDateAndGroup) {
            scriptBuilder.append(".tickFormat(function(d) { return d3.time.format('%x %H:00')(new Date(d)) })");
        }
        scriptBuilder.append(";");

        if (dateOnXAxis && swapDateAndGroup) {
            scriptBuilder.append("chart.legend.key(function(d) {return d3.time.format('%x %H:00')(new Date(d.key)) } );");
        }

        scriptBuilder.append("chart.yAxis");
        scriptBuilder.append(".tickFormat(d3.format(',f'))");
        scriptBuilder.append(";");

        scriptBuilder.append("d3.select('#");
        scriptBuilder.append(getHtmlId());
        scriptBuilder.append(" svg')");
        scriptBuilder.append(".datum(data())");
        scriptBuilder.append(".transition().duration(500).call(chart);");

        scriptBuilder.append("nv.utils.windowResize(chart.update);");

        scriptBuilder.append("return chart;");
        scriptBuilder.append("});");
        scriptBuilder.append("\n");
        scriptBuilder.append("function data() {");
        scriptBuilder.append(getData());
        scriptBuilder.append("}");
        return scriptBuilder.toString();
    }

    @Override
    public String getData() {
        String groupKey, xAxisKey;
        if (groupFieldName != null) {
            groupKey = groupFieldName;
        } else {
            groupKey = getReport().getCountField().getDisplayName();
        }
        if (dateOnXAxis) {
            xAxisKey = getReport().getEventDateFieldName();
        } else {
            xAxisKey = "All Dates";
        }
        if (!swapDateAndGroup) {
            return getDataScript(getDataGroupedXYMaps(xAxisKey, groupKey));
        } else {
            return getDataScript(getDataGroupedXYMaps(groupKey, xAxisKey));
        }
    }
}

class StandardLineChart extends StandardXYChart {

    @Override
    public String getScript() { 
        StringBuilder scriptBuilder = new StringBuilder();
        scriptBuilder.append("nv.addGraph(function() {");
        scriptBuilder.append("    var chart = nv.models.lineChart();");

        scriptBuilder.append("    chart.xAxis");
        scriptBuilder.append("        .tickFormat(function(d) { return d3.time.format('%x %H:00')(new Date(d)) })");
        scriptBuilder.append(";");

        scriptBuilder.append("    chart.yAxis");
        scriptBuilder.append("        .tickFormat(d3.format(',f'));");

        scriptBuilder.append("    d3.select('#");
        scriptBuilder.append(getHtmlId());
        scriptBuilder.append(" svg')");
        scriptBuilder.append("        .datum(data())");
        scriptBuilder.append("      .transition().duration(500).call(chart);");

        scriptBuilder.append("    nv.utils.windowResize(chart.update);");

        scriptBuilder.append("    return chart;");
        scriptBuilder.append("});");
        scriptBuilder.append("\n");
        scriptBuilder.append("function data() {");
        scriptBuilder.append(getData());
        scriptBuilder.append("}");
        return scriptBuilder.toString();
    }

}

///// PIE CHARTS

class PieChart extends MetricsChart {

    @ToolUi.InputProcessorPath("metricsIndexableFieldsDropdown.jsp")
    String groupFieldName;

    public List<Map<String,Object>> getDataLabelValueMaps(String labelKey) {
        List<Grouping<?>> rows = getReport().getRows();

        Integer labelIndex = getKeyIndex(labelKey);

        List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();

        for (Grouping<?> row : rows) {
            Map<String, Object> rowMap = new LinkedHashMap<String, Object>();
            Object labelValue;
            if (labelIndex != null) {
                labelValue = getValueOf(row.getKeys().get(labelIndex));
            } else {
                labelValue = labelKey;
            }
            rowMap.put("label", labelValue);
            long count = row.getCount();
            rowMap.put("value", count);
            values.add(rowMap);
        }

        List<Map<String,Object>> valuesList = new ArrayList<Map<String,Object>>();
        Map<String,Object> valuesMap = new LinkedHashMap<String, Object>();
        valuesMap.put("key", "Values");
        valuesMap.put("values", values);
        valuesList.add(valuesMap);
        return valuesList;
    }

    @Override
    public List<String> getRowKeys() {
        List<String> groupBy = new ArrayList<String>();

        if (groupFieldName != null)
            groupBy.add(groupFieldName);

        return groupBy;
    }

    @Override
    public String getScript() {
        StringBuilder scriptBuilder = new StringBuilder();

        scriptBuilder.append("nv.addGraph(function() {");
        scriptBuilder.append("  var chart = nv.models.pieChart()");
        scriptBuilder.append("      .x(function(d) { return d.label })");
        scriptBuilder.append("      .y(function(d) { return d.value })");
        scriptBuilder.append("      .showLabels(true);");

        scriptBuilder.append("    d3.select('#");
        scriptBuilder.append(getHtmlId());
        scriptBuilder.append(" svg')");
        scriptBuilder.append("        .datum(data())");
        scriptBuilder.append("      .transition().duration(1200)");
        scriptBuilder.append("        .call(chart);");

        scriptBuilder.append("  return chart;");
        scriptBuilder.append("});");
        scriptBuilder.append("\n");
        scriptBuilder.append("function data() {");
        scriptBuilder.append(getData());
        scriptBuilder.append("}");
        return scriptBuilder.toString();
    }

    @Override
    public String getData() {
        return getDataScript(getDataLabelValueMaps(groupFieldName));
    }

    @Override
    public String getNotReadyMessage() {
        if (getRowKeys().size() == 0) return "Choose a Group Field";
        return null;
    }

}

