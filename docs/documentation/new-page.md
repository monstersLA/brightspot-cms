---
layout: default
title: Creating New Layouts
id: templates
section: documentation
---

<div markdown="1" class="span8">

Brightspot implements a subset of the W3 Grid Layout specification for defining grid layouts. This provides a flexible way to define grid layouts using a compact CSS format. 

Using the `<cms:render>` JSP Tag content can be rendered into the CSS grid items defined by CSS layouts.

### Overview

Grid layouts are defined in regular CSS files. Brightspot will automatically read all
CSS files in the project and find any defined grid layouts.

For each content type being rendered there should be a grid layout defined in
CSS, a JSP to use the CSS grid layout and a JSP to render the content.

Note: Brightspot does not require the use of CSS grid layouts but it makes defining
layouts much easier and is recommended.

### Creating a Grid Layout

Grid layouts are defined in CSS files contained within your project
directories. 

Brightspot implements the following css properties defined in 
the W3 Grid Layout specification: **display: grid, grid-template, grid-definition-columns, grid-definition-rows**

Grid lengths allow the following units: **percentages, em, px, auto and fr**.

Grids defined in css should use class selectors (`.grid`) instead of id selectors (`#grid`). 

An example grid:

{% highlight css %}
.layout-global {
    display: grid;
    grid-template: ".    header  .   "
                   "main  main   main"
                   ".    footer  .   ";
 
    grid-definition-columns: 1fr 1140px 1fr;
    grid-definition-rows: 158px auto auto;

    margin-bottom: 10px;
}
{% endhighlight %}

It is useful to look at a `grid-template` like a table. For this version there are three columns, and three rows. The header sits in the middle column, and the main area sits across all three. The footer, like the header, sits in the middle. The `grid-definition-columns` define the widths of each column, and the `grid-definition-rows` define the height.

### Placing Content in the Grid

Grids can be used to lay pages out in JSPs using the `<cms:layout>` JSP Tag.
This tag takes the name of the css class selector that defines a grid and uses it to
output the appropriate `<div>` tags to effect the layout.

Content can be placed inside of named (or numbered) grid items using the
`<cms:render>` JSP tag.

For example, given the example grid defined in [Creating a Grid
Layout](#creating-a-grid-layout) the following JSP will render content in
each named grid item.

{% highlight jsp %}
<cms:layout class="layout-global">
    <cms:render area="header">
        Header Area
    </cms:render>
    <cms:render area="main">
        Main
    </cms:render>
    <cms:render area="footer">
        Footer
    </cms:render>
</cms:layout>
{% endhighlight %}

It is also possible to render content objects directly using the `<cms:render>` tag.

To view your grid on a page you have created add `?_prod=false&_grid=true` to the end of the URL.

{% highlight jsp %}
<cms:render value="${mainContent}" area="main">
{% endhighlight %}

Content can be rendered from the object, or the template. An example, where the header and footer objects are defined on the template:

{% highlight jsp %}
<cms:layout class="layout-global">
    <cms:render value="${template.header}" area="header"/>
    <cms:render value="${mainContent}" area="main"/>
    <cms:render value="${template.footer}" area="footer"/>
</cms:layout>
{% endhighlight %}

### Mobile Devices

To modify the layout dynamically, based on the size of the screen being used to view, a `@media` query can be used, to override the layout class. In the example below, once the screen size drops below 700px the right rail content appears below the main content, and the content width is reduced.

{% highlight css %}
.layout {
    display: grid;
    grid-template: ". main . right .";
 
    grid-definition-columns: 1fr 680px 80px 360px 1fr;
    grid-definition-rows: auto;

    padding: 10px;
    }

@media only screen and (min-width: 300px) and (max-width:700px) {
        .layout {
        display: grid;
        grid-template: ". main    ."
                       ". right   .";
     
        grid-definition-columns: 1fr 320px 1fr;
        grid-definition-rows: auto auto;

        padding: 10px;
    }
}

{% endhighlight %}



### JSP Tags

API Definitions for JSP tags used in the grid layout system.

**&lt;cms:layout&gt;**

> `class` - Name of css class that defines a grid.

**&lt;cms:render&gt;**

The render tag will render the contents of the `value` attribute into a
provided area.

> `area` - Name of area to render content into.

> `value` - Value to render. This can be Content, ReferentialText, an Iterable or a String. 


</div>

<div class="span4 dari-docs-sidebar">
<div markdown="1" style="position:scroll;" class="well sidebar-nav">


* auto-gen TOC:
{:toc}

</div>
</div>
