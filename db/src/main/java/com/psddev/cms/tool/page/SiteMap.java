package com.psddev.cms.tool.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import javax.servlet.ServletException;

import com.psddev.cms.db.Directory;
import com.psddev.cms.db.Template;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.tool.PageServlet;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.dari.db.AggregateQueryResult;
import com.psddev.dari.db.Database;
import com.psddev.dari.db.ObjectField;
import com.psddev.dari.db.ObjectType;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.State;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.PaginatedResult;
import com.psddev.dari.util.RoutingFilter;
import com.psddev.dari.util.StringUtils;

@RoutingFilter.Path(application = "cms", value = "/misc/siteMap.jsp")
@SuppressWarnings("serial")
public class SiteMap extends PageServlet {

    private static final int[] LIMITS = { 10, 20, 50 };
    private static final String URL_TYPE = "url";

    @Override
    protected String getPermissionId() {
        return "area/dashboard";
    }

    @Override
    protected void doService(final ToolPageContext page) throws IOException, ServletException {
        if (!Query.from(Directory.class).hasMoreThan(0)) {
            return;
        }

        String type = page.pageParam(String.class, "type", null);
        final ObjectType itemType = Query.findById(ObjectType.class, page.pageParam(UUID.class, "itemType", null));
        long offset = page.param(long.class, "offset");
        int limit = page.pageParam(Integer.class, "limit", 20);

        List<ObjectType> types = new ArrayList<ObjectType>();
        boolean typeFound = false;

        for (ObjectType t : Database.Static.getDefault().getEnvironment().getTypes()) {
            if (t.as(ToolUi.class).isGlobalFilter()) {
                types.add(t);
                if (type != null && type.equals(t.getId().toString())) {
                    typeFound = true;
                }
            }
        }

        Collections.sort(types);

        if (!typeFound) {
            type = URL_TYPE;
        }

        String valueParameter = type + ".value";
        final String value = page.pageParam(String.class, valueParameter, null);
        Object valueObject = Query.from(Object.class).where("_id = ?", value).first();
        PaginatedResult<?> result = null;
        Long count = null;

        if (type.equals(URL_TYPE)) {
            if (!(valueObject instanceof Directory)) {
                valueObject = Query.from(Directory.class).where("path = /").first();
            }

            if (valueObject != null) {
                result = new AggregateQueryResult<Object>(offset, limit, new DirectoryQueryIterator(Query.
                        from(Directory.class).
                        where("path startsWith ?", ((Directory) valueObject).getPath()).
                        sortAscending("path")) {

                    @Override
                    protected Query<?> createQuery(Directory directory) {
                        return (itemType != null ? Query.fromType(itemType) : Query.fromAll()).
                                and(page.siteItemsSearchPredicate()).
                                and(directory.itemsPredicate(page.getSite())).
                                sortAscending(Directory.PATHS_FIELD);
                    }
                });

                count = null;
            }

        } else if (valueObject != null) {
            Query<?> query = (itemType != null ? Query.fromType(itemType) : Query.fromAll()).
                    and(page.siteItemsPredicate()).
                    and("* matches ?", value).
                    and("cms.directory.paths != missing");

            if (query.hasMoreThan(250)) {
                result = new AggregateQueryResult<Object>(offset, limit, new DirectoryQueryIterator(Query.
                        from(Directory.class).
                        where("path startsWith /").
                        sortAscending("path")) {

                    @Override
                    protected Query<?> createQuery(Directory directory) {
                        return (itemType != null ? Query.fromType(itemType) : Query.fromAll()).
                                and(page.siteItemsPredicate()).
                                and(directory.itemsPredicate(page.getSite())).
                                and("* matches ?", value).
                                and("cms.directory.paths != missing");
                    }
                });

                count = query.count();

            } else {
                @SuppressWarnings("unchecked")
                List<Object> items = (List<Object>) query.selectAll();

                Collections.sort(items, new Comparator<Object>() {
                    @Override
                    public int compare(Object x, Object y) {
                        return ObjectUtils.compare(
                                State.getInstance(x).as(Directory.ObjectModification.class).getPermalink(),
                                State.getInstance(y).as(Directory.ObjectModification.class).getPermalink(),
                                true);
                    }
                });

                result = new PaginatedResult<Object>(offset, limit, items);
                count = (long) items.size();
            }
        }

        if (count == null && result != null && result.getFirstItemIndex() == 1 && !result.hasNext()) {
            count = (long) result.getItems().size();
        }

        page.writeStart("div", "class", "widget");
            page.writeStart("h1", "class", "icon icon-sitemap");
                page.writeHtml("Sitemap");
            page.writeEnd();

            page.writeStart("form",
                    "method", "get",
                    "action", page.url(null));
                page.writeStart("ul", "class", "oneLine");
                    page.writeStart("li");
                        page.writeTypeSelect(
                                Template.Static.findUsedTypes(page.getSite()),
                                itemType,
                                "Any Types",
                                "class", "autoSubmit",
                                "name", "itemType",
                                "data-searchable", "true");
                    page.writeEnd();

                    if (types.isEmpty()) {
                        page.writeTag("input",
                                "type", "hidden",
                                "name", "type",
                                "value", URL_TYPE);

                    } else {
                        page.writeStart("li");
                            page.writeHtml("with ");

                            page.writeStart("select",
                                    "class", "autoSubmit",
                                    "name", "type");

                                page.writeStart("option",
                                        "selected", type.equals(URL_TYPE) ? "selected" : null,
                                        "value", URL_TYPE);
                                    page.writeHtml("URL");
                                page.writeEnd();

                                for (ObjectType t : types) {
                                    String id = t.getId().toString();
                                    page.writeStart("option",
                                            "selected", type.equals(id) ? "selected" : null,
                                            "value", id);
                                        page.writeHtml(t.getDisplayName());
                                    page.writeEnd();
                                }

                            page.writeEnd();
                        page.writeEnd();
                    }

                    page.writeStart("li");
                        page.writeHtml("in ");

                        Query<?> valueQuery;
                        ObjectType valueType;

                        if (type.equals(URL_TYPE)) {
                            valueType = ObjectType.getInstance(Directory.class);
                            valueQuery = Query.from(Directory.class).sortAscending("path");

                        } else {
                            valueType = ObjectType.getInstance(ObjectUtils.to(UUID.class, type));
                            valueQuery = Query.fromType(valueType);

                            for (String fieldName : valueType.getLabelFields()) {
                                ObjectField field = valueType.getField(fieldName);

                                if (field != null && valueType.getIndex(fieldName) != null) {
                                    valueQuery.sortAscending(valueType.getInternalName() + "/" + fieldName);
                                }
                            }
                        }

                        if (valueQuery.hasMoreThan(250)) {
                            page.writeTag("input",
                                    "type", "text",
                                    "class", "autoSubmit objectId",
                                    "data-editable", false,
                                    "data-label", valueObject != null ? State.getInstance(valueObject).getLabel() : null,
                                    "data-typeIds", valueType.getId(),
                                    "name", valueParameter,
                                    "value", value);

                        } else {
                            page.writeStart("select",
                                    "class", "autoSubmit",
                                    "name", valueParameter,
                                    "data-searchable", "true");

                                if (!type.equals(URL_TYPE)) {
                                    page.writeStart("option", "value", "").writeEnd();
                                }

                                for (Object v : valueQuery.selectAll()) {
                                    State state = State.getInstance(v);

                                    page.writeStart("option",
                                            "value", state.getId(),
                                            "selected", v.equals(valueObject) ? "selected" : null);
                                        page.writeHtml(state.getLabel());
                                    page.writeEnd();
                                }

                            page.writeEnd();
                        }
                    page.writeEnd();
                page.writeEnd();
            page.writeEnd();

            if (valueObject == null) {
                page.writeStart("div", "class", "message message-warning");
                    page.writeStart("p");
                        page.writeHtml("Please select a ");
                        page.writeStart("strong").writeHtml(valueType.getLabel()).writeEnd();
                        page.writeHtml(".");
                    page.writeEnd();
                page.writeEnd();

            } else if (!result.hasPages()) {
                page.writeStart("div", "class", "message message-info");
                    page.writeStart("p");
                        page.writeHtml("No ");

                        if (itemType == null) {
                            page.writeHtml("items");

                        } else {
                            page.writeStart("strong");
                                page.writeObjectLabel(itemType);
                            page.writeEnd();
                        }

                        page.writeHtml(" matching ");
                        page.writeStart("strong");
                            page.writeObjectLabel(valueObject);
                        page.writeEnd();
                        page.writeHtml(".");
                    page.writeEnd();
                page.writeEnd();

            } else {
                page.writeStart("ul", "class", "pagination");

                    if (result.hasPrevious()) {
                        page.writeStart("li", "class", "first");
                            page.writeStart("a", "href", page.url("", "offset", result.getFirstOffset())).writeHtml("First").writeEnd();
                        page.writeEnd();

                        page.writeStart("li", "class", "previous");
                            page.writeStart("a", "href", page.url("", "offset", result.getPreviousOffset())).writeHtml("Previous ").writeHtml(result.getLimit()).writeEnd();
                        page.writeEnd();
                    }

                    if (result.getOffset() > 0 ||
                            result.hasNext() ||
                            result.getItems().size() > LIMITS[0]) {
                        page.writeStart("li");
                            page.writeStart("form",
                                    "class", "autoSubmit",
                                    "method", "get",
                                    "action", page.url(null));
                                page.writeStart("select", "name", "limit");
                                    for (int l : LIMITS) {
                                        page.writeStart("option",
                                                "value", l,
                                                "selected", limit == l ? "selected" : null);
                                            page.writeHtml("Show ");
                                            page.writeHtml(l);
                                        page.writeEnd();
                                    }
                                page.writeEnd();
                            page.writeEnd();
                        page.writeEnd();
                    }

                    if (count != null) {
                        page.writeStart("li");
                            page.writeHtml(result.getFirstItemIndex());
                            page.writeHtml(" to ");
                            page.writeHtml(result.getLastItemIndex());
                            page.writeHtml(" of ");
                            page.writeStart("strong").writeHtml(count).writeEnd();
                        page.writeEnd();
                    }

                    if (result.hasNext()) {
                        page.writeStart("li", "class", "next");
                            page.writeStart("a", "href", page.url("", "offset", result.getNextOffset())).writeHtml("Next ").writeHtml(result.getLimit()).writeEnd();
                        page.writeEnd();
                    }

                page.writeEnd();

                page.writeStart("table", "class", "links pageThumbnails table-striped");
                    page.writeStart("tbody");

                        for (Object item : result.getItems()) {
                            State itemState = State.getInstance(item);
                            String permalink = null;

                            if (type.equals(URL_TYPE)) {
                                for (Directory.Path pathObject : itemState.as(Directory.ObjectModification.class).getPaths()) {
                                    String p = pathObject.getPath();

                                    if (StringUtils.getPathInfo(p, ((Directory) valueObject).getPath()) != null) {
                                        permalink = p;
                                        break;
                                    }
                                }

                            } else {
                                permalink = itemState.as(Directory.ObjectModification.class).getPermalink();
                            }

                            page.writeStart("tr", "data-preview-url", permalink);

                                page.writeStart("td");
                                    page.writeTypeLabel(item);
                                page.writeEnd();

                                page.writeStart("td");
                                    page.writeStart("a",
                                            "target", "_top",
                                            "href", page.objectUrl("/content/edit.jsp", item));
                                        page.writeObjectLabel(item);
                                    page.writeEnd();
                                page.writeEnd();

                                if (permalink == null) {
                                    page.writeStart("td").writeEnd();

                                } else {
                                    page.writeStart("td", "data-preview-anchor", "");
                                        page.writeHtml(permalink);
                                    page.writeEnd();
                                }

                            page.writeEnd();
                        }

                    page.writeEnd();
                page.writeEnd();
            }

        page.writeEnd();
    }

    private static abstract class DirectoryQueryIterator implements Iterator<Query<?>> {

        private final Iterator<Directory> directoryIterator;

        public DirectoryQueryIterator(Query<Directory> directoryQuery) {
            this.directoryIterator = directoryQuery.iterable(0).iterator();
        }

        protected abstract Query<?> createQuery(Directory directory);

        @Override
        public boolean hasNext() {
            return directoryIterator.hasNext();
        }

        @Override
        public Query<?> next() {
            if (hasNext()) {
                return createQuery(directoryIterator.next());
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
