<%@ page import="

com.psddev.cms.tool.ToolPageContext,

com.psddev.cms.db.MetricsReport,
com.psddev.cms.db.MetricsChart,

com.psddev.dari.db.ObjectField,
com.psddev.dari.db.State,

java.util.Set
" %><%

// --- Logic ---

ToolPageContext wp = new ToolPageContext(pageContext);

//State state = State.getInstance(request.getAttribute("object"));

MetricsChart chart = (MetricsChart) request.getAttribute("object");
State state = chart.getState();

ObjectField field = (ObjectField) request.getAttribute("field");
String fieldName = field.getInternalName();

String inputName = (String) request.getAttribute("inputName");

if ((Boolean) request.getAttribute("isFormPost")) {
    state.putValue(fieldName, wp.param(inputName));
    return;
}

Set<ObjectField> availableFields = null;

if (chart != null) {
    availableFields = chart.getAvailableFields();
}

// --- Presentation ---

if (availableFields != null) {

    %><div class="smallInput">
        <select name="<%= wp.h(inputName) %>">
            <option value=""> -- </option>
            <%for (ObjectField availableField : availableFields) { %>
                <option <%=(availableField.getInternalName().equals(state.getValue(fieldName)) ? "SELECTED" : "")%> value="<%=availableField.getInternalName()%>"><%=availableField.getDisplayName()%></option>
            <%}%>
        </select>
    </div>

<%  } else {%>
    Save for the list of available fields.
<%  } %>
