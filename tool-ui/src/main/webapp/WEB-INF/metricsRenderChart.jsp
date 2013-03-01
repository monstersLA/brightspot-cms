<%@ page import="
com.psddev.cms.tool.ToolPageContext,
com.psddev.cms.db.MetricsReport,
com.psddev.dari.db.Grouping
" %>
<%

ToolPageContext wp = new ToolPageContext(pageContext);

MetricsReport report = (MetricsReport) request.getAttribute("report");
//State state = State.getInstance(object);

%>

<!--${report.availableFieldNames}-->

<hr />

<%if (! report.isReady()){

    %>${report.notReadyMessage}<%

} else {%>

<link type="text/css" media="screen" href="<%=wp.url("/style/nvd3/nv.d3.css")%>" rel="stylesheet">
<script type="text/javascript" src="<%=wp.url("/script/nvd3/lib/d3.v2.js")%>"></script>
<script type="text/javascript" src="<%=wp.url("/script/nvd3/nv.d3.js")%>"></script>

    <style type="text/css">
        ${report.chartStyle}
    </style>

    <script type="text/javascript">
        ${report.chartScript}
    </script>

    ${report.chartHtml}

    <%
}%>

<hr />
<br /> <br /> <br /> <br /> <br /> <br />
<br /> <br /> <br /> <br /> <br /> <br />
