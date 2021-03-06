package com.psddev.cms.tool.page;

import java.io.IOException;

import javax.servlet.ServletException;

import com.psddev.cms.db.ToolUser;
import com.psddev.cms.db.ToolUserAction;
import com.psddev.cms.db.ToolUserDevice;
import com.psddev.cms.tool.PageServlet;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.dari.db.Query;
import com.psddev.dari.util.JspUtils;
import com.psddev.dari.util.RoutingFilter;

@RoutingFilter.Path(application = "cms", value = "toolUserHistory")
@SuppressWarnings("serial")
public class ToolUserHistory extends PageServlet {

    @Override
    protected String getPermissionId() {
        return null;
    }

    @Override
    protected void doService(ToolPageContext page) throws IOException, ServletException {
        ToolUser user = page.getUser();

        page.writeHeader();
            page.writeStart("div", "class", "widget", "style", "overflow: hidden;");
                page.writeStart("h1", "class", "icon icon-object-history");
                    page.writeHtml("History");
                page.writeEnd();

                page.writeStart("ul");
                    for (ToolUserDevice device : Query.
                            from(ToolUserDevice.class).
                            where("user = ?", user).
                            selectAll()) {
                        String lookingGlassUrl = page.cmsUrl("/lookingGlass", "id", device.getOrCreateLookingGlassId());

                        page.writeStart("li", "style", "clear: right;");
                            page.writeHtml(device.getUserAgentDisplay());

                            page.writeStart("div", "style", page.cssString(
                                    "float", "right",
                                    "text-align", "center"));
                                page.writeStart("a",
                                        "class", "icon icon-facetime-video",
                                        "target", "_blank",
                                        "href", lookingGlassUrl);
                                    page.writeHtml("Looking Glass");
                                page.writeEnd();

                                page.writeTag("br");

                                page.writeTag("img",
                                        "width", 150,
                                        "height", 150,
                                        "src", page.cmsUrl("qrCode",
                                                "data", JspUtils.getAbsoluteUrl(page.getRequest(), lookingGlassUrl),
                                                "size", 150));
                            page.writeEnd();

                            page.writeStart("ul",
                                    "class", "links",
                                    "style", page.cssString("margin-right", "150px"));
                                for (ToolUserAction action : Query.
                                        from(ToolUserAction.class).
                                        where("device = ?", device).
                                        sortDescending("time").
                                        selectAll()) {
                                    Object actionContent = action.getContent();

                                    if (actionContent == null) {
                                        continue;
                                    }

                                    page.writeStart("li");
                                        page.writeStart("a",
                                                "target", "_top",
                                                "href", page.objectUrl("/content/edit.jsp", actionContent));
                                            page.writeTypeObjectLabel(actionContent);
                                        page.writeEnd();
                                    page.writeEnd();
                                }
                            page.writeEnd();
                        page.writeEnd();
                    }
                page.writeEnd();
            page.writeEnd();
        page.writeFooter();
    }
}
