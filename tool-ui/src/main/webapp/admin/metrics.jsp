<%@ page import="

com.psddev.cms.db.MetricsReport,
com.psddev.cms.db.PageFilter,

com.psddev.cms.tool.ToolPageContext,

com.psddev.dari.db.Query,
com.psddev.dari.db.State,

java.util.List
" %><%

// --- Logic ---

ToolPageContext wp = new ToolPageContext(pageContext);
if (wp.requirePermission("area/admin/adminMetrics")) {
    return;
}

Object selected = wp.findOrReserve(MetricsReport.class);
Class<?> selectedClass = selected.getClass();
State selectedState = State.getInstance(selected);

if (wp.include("/WEB-INF/updateObject.jsp", "object", selected)) {
    return;
}

List<MetricsReport> reports = Query.from(MetricsReport.class).sortAscending("name").select();

// --- Presentation ---

%><% wp.include("/WEB-INF/header.jsp"); %>

<div class="withLeftNav">
    <div class="leftNav">
        <div class="widget">

            <h1>Metrics</h1>

            <h2>Saved Reports</h2>
            <ul class="links">
                <li class="new<%= selectedState.isNew() ? " selected" : ""%>">
                    <a href="<%= wp.typeUrl(null, MetricsReport.class) %>">New Report</a>
                </li>
                <% for (MetricsReport report : reports) { %>
                    <li<%= report.equals(selected) ? " class=\"selected\"" : "" %>>
                        <a href="<%= wp.objectUrl(null, report) %>"><%= wp.objectLabel(report) %></a>
                    </li>
                <% } %>
            </ul>

        </div>
    </div>
    <div class="main">
    
        <div class="widget">
            <% wp.include("/WEB-INF/editObject.jsp", "object", selected); %>
        </div>

        <%if (! selectedState.isNew()) { %>
        <div class="widget">
            <h1>Preview</h1>
            <a class="action-share" href="<%= wp.url("/content/shareMetricsReport.jsp") %>?id=<%=selectedState.getId()%>">Share</a><br />

            <% wp.include("/WEB-INF/metricsRenderChart.jsp", "report", selected); %>

        </div>
        <% } %>

    </div>
</div>

<% wp.include("/WEB-INF/footer.jsp"); %>
