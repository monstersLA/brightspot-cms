package com.psddev.cms.tool.page;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.psddev.cms.db.Content;
import com.psddev.cms.db.Draft;
import com.psddev.cms.db.History;
import com.psddev.cms.db.Schedule;
import com.psddev.cms.tool.CmsTool;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.cms.tool.Widget;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.State;
import com.psddev.dari.util.ObjectUtils;

public class ContentRevisions extends Widget {

    {
        setDisplayName("Revisions");
        setInternalName("cms.contentRevisison");
        addPosition(CmsTool.CONTENT_RIGHT_WIDGET_POSITION, 0, 3);
    }

    @Override
    public String createDisplayHtml(ToolPageContext page, Object object) throws IOException {
        Writer oldDelegate = page.getDelegate();
        StringWriter newDelegate = new StringWriter();

        try {
            page.setDelegate(newDelegate);
            writeDisplayHtml(page, object);
            return newDelegate.toString();

        } finally {
            page.setDelegate(oldDelegate);
        }
    }

    private void writeDisplayHtml(ToolPageContext page, Object object) throws IOException {
        State state = State.getInstance(object);

        if (state.isNew()) {
            return;
        }

        List<Draft> scheduled = new ArrayList<Draft>();
        List<Draft> drafts = new ArrayList<Draft>();
        List<History> namedHistories = new ArrayList<History>();
        List<History> histories = new ArrayList<History>();

        Object selected = page.getOverlaidHistory(object);

        if (selected == null) {
            selected = page.getOverlaidDraft(object);

            if (selected == null) {
                selected = object;
            }
        }

        for (Draft d : Query.
                from(Draft.class).
                where("objectId = ?", state.getId()).
                selectAll()) {
            if (d.getSchedule() != null) {
                State objectState = State.getInstance(d.getObject());

                if (objectState.isVisible() ||
                        !d.getObjectChanges().isEmpty()) {
                    scheduled.add(d);
                }

            } else {
                drafts.add(d);
            }
        }

        Collections.sort(scheduled, new Comparator<Draft>() {
            @Override
            public int compare(Draft x, Draft y) {
                return ObjectUtils.compare(x.getSchedule().getTriggerDate(), y.getSchedule().getTriggerDate(), true);
            }
        });

        Collections.sort(drafts, new Comparator<Draft>() {
            @Override
            public int compare(Draft x, Draft y) {
                return ObjectUtils.compare(
                        x.as(Content.ObjectModification.class).getUpdateDate(),
                        y.as(Content.ObjectModification.class).getUpdateDate(),
                        true);
            }
        });

        for (History h : Query.
                from(History.class).
                where("name != missing and objectId = ?", state.getId()).
                sortAscending("name").
                selectAll()) {
            namedHistories.add(h);
        }

        for (History h : Query.
                from(History.class).
                where("name = missing and objectId = ?", state.getId()).
                sortDescending("updateDate").
                select(0, 10).
                getItems()) {
            histories.add(h);
        }

        page.writeStart("div", "class", "widget");
            page.writeStart("h1", "class", "icon icon-object-history");
                page.writeHtml("Revisions");
            page.writeEnd();

            page.writeStart("ul", "class", "links");
                page.writeStart("li", "class", object.equals(selected) ? "selected" : null);
                    page.writeStart("a", "href", page.originalUrl(null, object));
                        page.writeHtml("Current");
                    page.writeEnd();
                page.writeEnd();
            page.writeEnd();

            if (!scheduled.isEmpty()) {
                page.writeStart("h2").writeHtml("Scheduled").writeEnd();

                page.writeStart("ul", "class", "links pageThumbnails");
                    for (Draft d : scheduled) {
                        Schedule s = d.getSchedule();
                        String sn = s.getName();

                        page.writeStart("li",
                                "class", d.equals(selected) ? "selected" : null,
                                "data-preview-url", "/_preview?_cms.db.previewId=" + d.getId());
                            page.writeStart("a", "href", page.objectUrl(null, d));
                                if (ObjectUtils.isBlank(sn)) {
                                    page.writeHtml(page.formatUserDateTime(s.getTriggerDate()));
                                    page.writeHtml(" by ");
                                    page.writeObjectLabel(s.getTriggerUser());

                                } else {
                                    page.writeHtml(sn);
                                }
                            page.writeEnd();
                        page.writeEnd();
                    }
                page.writeEnd();
            }

            if (!drafts.isEmpty()) {
                page.writeStart("h2").writeHtml("Drafts").writeEnd();

                page.writeStart("ul", "class", "links pageThumbnails");
                    for (Draft d : drafts) {
                        Content.ObjectModification dcd = d.as(Content.ObjectModification.class);

                        page.writeStart("li",
                                "class", d.equals(selected) ? "selected" : null,
                                "data-preview-url", "/_preview?_cms.db.previewId=" + d.getId());
                            page.writeStart("a", "href", page.objectUrl(null, d));
                                page.writeHtml(page.formatUserDateTime(dcd.getUpdateDate()));
                                page.writeHtml(" by ");
                                page.writeObjectLabel(dcd.getUpdateUser());
                            page.writeEnd();
                        page.writeEnd();
                    }
                page.writeEnd();
            }

            if (!namedHistories.isEmpty()) {
                page.writeStart("h2").writeHtml("Named Past").writeEnd();

                page.writeStart("ul", "class", "links pageThumbnails");
                    for (History h : namedHistories) {
                        page.writeStart("li",
                                "class", h.equals(selected) ? "selected" : null,
                                "data-preview-url", "/_preview?_cms.db.previewId=" + h.getId());
                            page.writeStart("a", "href", page.objectUrl(null, h));
                                page.writeObjectLabel(h);
                            page.writeEnd();
                        page.writeEnd();
                    }
                page.writeEnd();
            }

            if (!histories.isEmpty()) {
                page.writeStart("h2").writeHtml("Past").writeEnd();

                page.writeStart("ul", "class", "links pageThumbnails");
                    for (History h : histories) {
                        page.writeStart("li",
                                "class", h.equals(selected) ? "selected" : null,
                                "data-preview-url", "/_preview?_cms.db.previewId=" + h.getId());
                            page.writeStart("a", "href", page.objectUrl(null, h));
                                page.writeHtml(page.formatUserDateTime(h.getUpdateDate()));
                                page.writeHtml(" by ");
                                page.writeObjectLabel(h.getUpdateUser());
                            page.writeEnd();
                        page.writeEnd();
                    }
                page.writeEnd();
            }
        page.writeEnd();
    }

    @Override
    public void update(ToolPageContext page, Object object) {
    }
}
