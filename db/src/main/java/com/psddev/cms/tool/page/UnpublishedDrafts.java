package com.psddev.cms.tool.page;

import com.psddev.cms.db.Content;
import com.psddev.cms.db.Draft;
import com.psddev.cms.db.DraftStatus;
import com.psddev.cms.tool.PageServlet;
import com.psddev.cms.tool.PageWriter;
import com.psddev.cms.tool.ToolPageContext;

import com.psddev.dari.db.Query;
import com.psddev.dari.db.State;
import com.psddev.dari.util.PaginatedResult;
import com.psddev.dari.util.RoutingFilter;

import java.io.IOException;

import javax.servlet.ServletException;

@RoutingFilter.Path(application = "cms", value = "/misc/unpublishedDrafts.jsp")
@SuppressWarnings("serial")
public class UnpublishedDrafts extends PageServlet {

    @Override
    protected String getPermissionId() {
        return "area/dashboard";
    }

    @Override
    protected void doService(ToolPageContext page) throws IOException, ServletException {
        PageWriter writer = page.getWriter();
        PaginatedResult<Draft> drafts = Query.
                from(Draft.class).
                where(Content.UPDATE_DATE_FIELD + " != missing").
                sortDescending(Content.UPDATE_DATE_FIELD).
                select(page.param(long.class, "offset"), page.paramOrDefault(int.class, "limit", 20));

        writer.start("style", "type", "text/css");
            writer.write(".widget-unpublishedDrafts .status {");
                writer.write("background-color: #ee6;");
                writer.write("-moz-border-radius: 5px;");
                writer.write("-webkit-border-radius: 5px;");
                writer.write("border-radius: 5px;");
                writer.write("font-size: 80%;");
                writer.write("padding: 5px;");
                writer.write("text-transform: uppercase;");
            writer.write("}");
        writer.end();

        writer.start("div", "class", "widget widget-unpublishedDrafts");

            writer.start("h1", "class", "icon-edit").html("Drafts").end();

            if (drafts.hasPrevious() || drafts.hasNext()) {
                writer.start("ul", "class", "pagination");

                    if (drafts.hasPrevious()) {
                        writer.start("li", "class", "first");
                            writer.start("a", "href", page.url("", "offset", drafts.getFirstOffset()));
                                writer.html("Newest");
                            writer.end();
                        writer.end();

                        writer.start("li", "class", "previous");
                            writer.start("a", "href", page.url("", "offset", drafts.getPreviousOffset()));
                                writer.html("Newer ").html(drafts.getLimit());
                            writer.end();
                        writer.end();
                    }

                    if (drafts.hasNext()) {
                        writer.start("li", "class", "next");
                            writer.start("a", "href", page.url("", "offset", drafts.getNextOffset()));
                                writer.html("Older ").html(drafts.getLimit());
                            writer.end();
                        writer.end();
                    }

                writer.end();
            }

            writer.start("table", "class", "links table-striped").start("tbody");
                for (Draft draft : drafts.getItems()) {
                    Object object = draft.getObject();

                    if (object == null) {
                        continue;
                    }

                    State objectState = State.getInstance(object);

                    if (!objectState.isNew()) {
                        continue;
                    }

                    writer.start("tr");
                        writer.start("td");
                            DraftStatus status = draft.getStatus();
                            if (status != null) {
                                writer.start("span", "class", "status");
                                    writer.objectLabel(status);
                                writer.end();
                            }
                        writer.end();

                        writer.start("td");
                            writer.typeLabel(object);
                        writer.end();

                        writer.start("td", "class", "main");
                            writer.start("a", "href", page.objectUrl("/content/edit.jsp", draft), "target", "_top");
                                writer.objectLabel(object);
                            writer.end();
                        writer.end();

                        writer.start("td");
                            writer.objectLabel(draft.as(Content.ObjectModification.class).getPublishUser());
                        writer.end();
                    writer.end();
                }
            writer.end().end();

        writer.end();
    }
}