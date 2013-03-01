package com.psddev.cms.tool.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;

import org.joda.time.DateTime;

import com.psddev.cms.db.Draft;
import com.psddev.cms.db.Schedule;
import com.psddev.cms.db.Site;
import com.psddev.cms.tool.PageServlet;
import com.psddev.cms.tool.PageWriter;
import com.psddev.cms.tool.ToolPageContext;
import com.psddev.dari.db.Query;
import com.psddev.dari.util.RoutingFilter;

@RoutingFilter.Path(application = "cms", value = "/misc/scheduledEvents.jsp")
@SuppressWarnings("serial")
public class ScheduledEvents extends PageServlet {

    @Override
    protected String getPermissionId() {
        return "area/dashboard";
    }

    @Override
    protected void doService(ToolPageContext page) throws IOException, ServletException {
        Mode mode = page.pageParam(Mode.class, "mode", Mode.WEEK);
        DateTime date = new DateTime(page.param(Date.class, "date"));
        DateTime begin = mode.getBegin(date);
        DateTime end = mode.getEnd(date);
        Map<DateTime, List<Schedule>> schedulesByDate = new TreeMap<DateTime, List<Schedule>>();
        boolean hasSchedules = false;

        for (DateTime i = begin; i.isBefore(end); i = i.plusDays(1)) {
            schedulesByDate.put(i, new ArrayList<Schedule>());
        }

        Site currentSite = page.getSite();

        for (Schedule schedule : Query.
                from(Schedule.class).
                where("triggerDate >= ? and triggerDate < ?", begin, end).
                sortAscending("triggerDate").
                iterable(0)) {

            if (currentSite != null && !currentSite.equals(schedule.getTriggerSite())) {
                continue;
            }

            DateTime scheduleDate = new DateTime(schedule.getTriggerDate()).toDateMidnight().toDateTime();
            List<Schedule> schedules = schedulesByDate.get(scheduleDate);

            if (schedules != null) {
                schedules.add(schedule);
                hasSchedules = true;
            }
        }

        PageWriter writer = page.getWriter();

        writer.start("div", "class", "widget widget-scheduledEvents" + (hasSchedules ? "" : " widget-scheduledEvents-empty"));
            writer.start("h1", "class", "icon icon-calendar");

                writer.html("Scheduled Events: ");

                String beginMonth = begin.monthOfYear().getAsText();
                int beginYear = begin.year().get();
                String endMonth = end.monthOfYear().getAsText();
                int endYear = end.year().get();

                writer.html(beginMonth);
                writer.html(" ");
                writer.html(begin.dayOfMonth().get());

                if (beginYear != endYear) {
                    writer.html(", ");
                    writer.html(beginYear);
                }

                writer.html(" - ");

                if (!endMonth.equals(beginMonth)) {
                    writer.html(endMonth);
                    writer.html(" ");
                }

                writer.html(end.dayOfMonth().get());
                writer.html(", ");
                writer.html(endYear);

            writer.end();

            /*
            writer.start("form", "method", "get", "class", "autoSubmit", "action", page.url(null));
                writer.start("select", "name", "mode");
                    for (Mode m : Mode.values()) {
                        writer.start("option",
                                "value", m.name(),
                                "selected", m.equals(mode) ? "selected" : null);
                            writer.html(m);
                        writer.end();
                    }
                writer.end();
            writer.end();
            */

            writer.start("ul", "class", "pagination");

                DateTime previous = mode.getPrevious(date);
                DateTime today = new DateTime().toDateMidnight().toDateTime();

                if (!previous.isBefore(today)) {
                    writer.start("li", "class", "previous");
                        writer.start("a",
                                "href", page.url("", "date", previous.getMillis()));
                            writer.html("Previous ").html(mode);
                        writer.end();
                    writer.end();
                }

                if (begin.isAfter(today) || end.isBefore(today)) {
                    writer.start("li");
                        writer.start("a",
                                "href", page.url("", "date", System.currentTimeMillis()));
                            writer.html("Today");
                        writer.end();
                    writer.end();
                }

                writer.start("li", "class", "next");
                    writer.start("a",
                            "href", page.url("", "date", mode.getNext(date).getMillis()));
                        writer.html("Next ").html(mode);
                    writer.end();
                writer.end();

            writer.end();

            mode.display(page, schedulesByDate);
        writer.end();
    }

    private enum Mode {
        WEEK("Week") {

            @Override
            public DateTime getBegin(DateTime date) {
                return date.toDateMidnight().toDateTime();
            }

            @Override
            public DateTime getEnd(DateTime date) {
                return getBegin(date).plusWeeks(1);
            }

            @Override
            public DateTime getPrevious(DateTime date) {
                return date.plusWeeks(-1);
            }

            @Override
            public DateTime getNext(DateTime date) {
                return date.plusWeeks(1);
            }

            @Override
            public void display(ToolPageContext page, Map<DateTime, List<Schedule>> schedulesByDate) throws IOException {
                PageWriter writer = page.getWriter();

                writer.start("div", "class", "calendar calendar-week");
                    for (Map.Entry<DateTime, List<Schedule>> entry : schedulesByDate.entrySet()) {
                        DateTime date = entry.getKey();
                        List<Schedule> schedules = entry.getValue();

                        writer.start("div", "class", "calendarRow");
                            writer.start("div", "class", "calendarDay" + (date.equals(new DateTime().toDateMidnight()) ? " calendarDay-today" : ""));
                                writer.start("span", "class", "calendarDayOfWeek").html(date.dayOfWeek().getAsShortText()).end();
                                writer.start("span", "class", "calendarDayOfMonth").html(date.dayOfMonth().get()).end();
                            writer.end();

                            writer.start("div", "class", "calendarCell").start("table", "class", "links table-striped pageThumbnails").start("tbody");
                                for (Schedule schedule : schedules) {
                                    DateTime triggerDate = new DateTime(schedule.getTriggerDate());
                                    List<Draft> drafts = Query.from(Draft.class).where("schedule = ?", schedule).selectAll();

                                    if (drafts.isEmpty()) {
                                        continue;
                                    }

                                    boolean first = true;

                                    for (Draft draft : drafts) {
                                        Object draftObject = draft.getObject();

                                        writer.start("tr", "data-preview-url", "/_preview?_cms.db.previewId=" + draft.getId());
                                            writer.start("td", "class", "time");
                                                if (first) {
                                                    writer.html(triggerDate.toString("hh:mm a"));
                                                    first = false;
                                                }
                                            writer.end();

                                            writer.start("td");
                                                writer.typeLabel(draftObject);
                                            writer.end();

                                            writer.start("td", "data-preview-anchor", "");
                                                writer.start("a",
                                                        "href", page.objectUrl("/content/edit.jsp", draft),
                                                        "target", "_top");
                                                    writer.objectLabel(draftObject);
                                                writer.end();
                                            writer.end();
                                        writer.end();
                                    }
                                }
                            writer.end().end().end();
                        writer.end();
                    }
                writer.end();
            }
        },

        MONTH("Month") {

            @Override
            public DateTime getBegin(DateTime date) {
                return date.toDateMidnight().withDayOfMonth(1).toDateTime();
            }

            @Override
            public DateTime getEnd(DateTime date) {
                return getBegin(date).plusMonths(1);
            }

            @Override
            public DateTime getPrevious(DateTime date) {
                return date.plusMonths(-1);
            }

            @Override
            public DateTime getNext(DateTime date) {
                return date.plusMonths(1);
            }

            @Override
            public void display(ToolPageContext page, Map<DateTime, List<Schedule>> schedulesByDate) throws IOException {
            }
        };

        private final String displayName;

        private Mode(String displayName) {
            this.displayName = displayName;
        }

        public abstract DateTime getBegin(DateTime date);

        public abstract DateTime getEnd(DateTime date);

        public abstract DateTime getPrevious(DateTime date);

        public abstract DateTime getNext(DateTime date);

        public abstract void display(ToolPageContext page, Map<DateTime, List<Schedule>> schedulesByDate) throws IOException;

        @Override
        public String toString() {
            return displayName;
        }
    }
}
