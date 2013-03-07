package com.psddev.cms.tool.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import com.psddev.cms.db.Directory;
import com.psddev.cms.db.Template;
import com.psddev.cms.tool.PageServlet;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.dari.db.ObjectType;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.State;
import com.psddev.dari.util.RoutingFilter;

@RoutingFilter.Path(application = "cms", value = "/createNew")
@SuppressWarnings("serial")
public class CreateNew extends PageServlet {

    @Override
    protected String getPermissionId() {
        return "area/dashboard";
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doService(final ToolPageContext page) throws IOException, ServletException {
        page.getWriter();

        String redirect = page.param(String.class, "redirect");
        List<TypeTemplate> typeTemplates = new ArrayList<TypeTemplate>();
        Map<ObjectType, Integer> typeCounts = new HashMap<ObjectType, Integer>();

        for (Template template : Query.
                from(Template.class).
                where(page.siteItemsPredicate()).
                sortAscending("name").
                selectAll()) {

            for (ObjectType type : template.getContentTypes()) {
                TypeTemplate typeTemplate = new TypeTemplate(type, template);

                if (typeTemplate.getCollapsedId().equals(redirect)) {
                    page.redirect("/content/edit.jsp", "typeId", type.getId(), "templateId", template.getId());
                    return;
                }

                typeTemplates.add(typeTemplate);
                typeCounts.put(type, typeCounts.containsKey(type) ? typeCounts.get(type) + 1 : 1);
            }
        }

        Collections.sort(typeTemplates);

        List<TypeTemplate> favorites = new ArrayList<TypeTemplate>();
        List<TypeTemplate> collapsed = new ArrayList<TypeTemplate>();

        if (page.isFormPost()) {
            Collection<String> collapsedIds = new HashSet<String>();

            for (TypeTemplate typeTemplate : typeTemplates) {
                if (page.param(boolean.class, typeTemplate.getParameterName())) {
                    favorites.add(typeTemplate);
                } else {
                    collapsed.add(typeTemplate);
                    collapsedIds.add(typeTemplate.getCollapsedId());
                }
            }

            page.putPageSetting("collapsedIds", collapsedIds);

        } else {
            Collection<String> collapsedIds = (Collection<String>) page.getPageSetting("collapsedIds");

            if (collapsedIds == null) {
                favorites = typeTemplates;

            } else {
                for (TypeTemplate typeTemplate : typeTemplates) {
                    if (collapsedIds.contains(typeTemplate.getCollapsedId())) {
                        collapsed.add(typeTemplate);
                    } else {
                        favorites.add(typeTemplate);
                    }
                }
            }
        }

        String widgetId = page.createId();

        page.writeStart("style", "type", "text/css");
            page.writeCss("#" + widgetId, "position", "relative");
            page.writeCss("#" + widgetId + " .action-customize", "position", "absolute", "right", "32px", "top", "6px");
            page.writeCss("#" + widgetId + " .checkboxContainer", "text-align", "center"); 
        page.writeEnd();

        page.writeStart("div", "class", "widget", "id", widgetId);
            page.writeStart("h1", "class", "icon icon-plus-sign").writeHtml("Create New").writeEnd();

            if (page.param(boolean.class, "customize")) {
                page.writeStart("form",
                        "method", "post",
                        "action", page.url(null));

                    page.writeStart("table", "class", "table-striped");
                        page.writeStart("thead");
                            page.writeStart("tr");
                                page.writeStart("th").writeEnd();
                                page.writeStart("th").writeHtml("Favorite?").writeEnd();
                            page.writeEnd();
                        page.writeEnd();

                        page.writeStart("tbody");
                            for (TypeTemplate typeTemplate : typeTemplates) {
                                page.writeStart("tr");
                                    page.writeStart("td").writeHtml(page.getObjectLabel(typeTemplate.type)).writeEnd();

                                    page.writeStart("td", "class", "checkboxContainer");
                                        page.writeTag("input",
                                                "type", "checkbox",
                                                "id", page.getId(),
                                                "name", typeTemplate.getParameterName(),
                                                "value", "true",
                                                "checked", collapsed.contains(typeTemplate) ? null : "checked");
                                    page.writeEnd();
                                page.writeEnd();
                            }
                        page.writeEnd();
                    page.writeEnd();

                    page.writeStart("div", "class", "actions");
                        page.writeTag("input",
                                "class", "action",
                                "type", "submit",
                                "value", "Save");

                        page.writeStart("a",
                                "class", "action action-pullRight",
                                "href", page.url(null));
                            page.writeHtml("Cancel");
                        page.writeEnd();
                    page.writeEnd();

                page.writeEnd();

            } else {
                page.writeStart("a",
                        "class", "icon icon-cog action action-customize",
                        "href", page.url("", "customize", "true"));
                    page.writeHtml("Customize");
                page.writeEnd();

                page.writeStart("ul", "class", "links pageThumbnails");
                    for (TypeTemplate typeTemplate : favorites) {
                        ObjectType type = typeTemplate.type;
                        Template template = typeTemplate.template;
                        State state = State.getInstance(Query.fromType(type).where("cms.template.default = ?", template).first());
                        String permalink = null;

                        if (state != null) {
                            permalink = state.as(Directory.ObjectModification.class).getPermalink();
                        }

                        page.writeStart("li", "data-preview-url", permalink);
                            page.writeStart("a",
                                    "target", "_top",
                                    "href", page.url("/content/edit.jsp",
                                            "typeId", type.getId(),
                                            "templateId", template.getId()));
                                page.writeHtml(page.getObjectLabel(type));

                                if (typeCounts.get(type) > 1) {
                                    page.writeHtml(" - ");
                                    page.writeHtml(page.getObjectLabel(template));
                                }
                            page.writeEnd();
                        page.writeEnd();
                    }
                page.writeEnd();

                if (!collapsed.isEmpty()) {
                    page.writeStart("form",
                            "method", "get",
                            "action", page.url(null),
                            "target", "_top");
                        page.writeStart("select", "name", "redirect");
                            for (TypeTemplate typeTemplate : collapsed) {
                                page.writeStart("option", "value", typeTemplate.getCollapsedId());
                                    page.writeHtml(page.getObjectLabel(typeTemplate.type));
                                page.writeEnd();
                            }
                        page.writeEnd();

                        page.writeHtml(" ");

                        page.writeTag("input",
                                "type", "submit",
                                "value", "New");
                    page.writeEnd();
                }
            }
        page.writeEnd();
    }

    private static class TypeTemplate implements Comparable<TypeTemplate> {

        public final ObjectType type;
        public final Template template;

        public TypeTemplate(ObjectType type, Template template) {
            this.type = type;
            this.template = template;
        }

        public String getParameterName() {
            return "favorite." + type.getId() + "." + template.getId();
        }

        public String getCollapsedId() {
            return type.getId() + "," + template.getId();
        }

        @Override
        public int compareTo(TypeTemplate other) {
            int comparison = type.compareTo(other.type);
            return comparison == 0 ? template.compareTo(other.template) : comparison;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;

            } else if (other instanceof TypeTemplate) {
                TypeTemplate o = (TypeTemplate) other;
                return type.equals(o.type) && template.equals(o.template);

            } else {
                return false;
            }
        }
    }
}
