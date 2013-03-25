---
layout: default
title: Editorial Guide
id: editorialGuide
section: documentation
---

<div markdown="1" class="span8">

## Introduction

Once logged into Brightspot you are brought to the Dashboard. Here you will find common widgets, your navigation to other areas of the CMS, a global search, and access to your admin and settings.

![](/img/editor/dashboard.png)


**Tool Hat**

The Tools at the top of the screen remain visible on all pages in Brightspot. From here you can access your own user profile settings, log-out and change which site you are viewing if using Multisite.

![](http://docs.brightspot.s3.amazonaws.com/dashboard_profile_2.1.png)


**Search**

The search tool can be accessed from any screen within Brightspot. Simply place your cursor in the search field to get started. Start typing to see the results change. As a default, all content types are shown in the results set, however with customizable options to filter with, using the filters on the left, you can specify exactly what you are looking for. You can also sort the results order on certain fields, or only display items with missing content.

![](http://docs.brightspot.s3.amazonaws.com/dashboard_search_2.1.png)

**Create**

Found within the Search tool pop-up, you have the ability to create new content from anywhere within Brightspot using the create drop-down. Choose from the list of existing objects or start typing to narrow the results.

![](http://docs.brightspot.s3.amazonaws.com/dashboard_create_2.1.png)


**Work Stream Creation**

Work Steams allow editors to group together content that needs editing, and then work through the task quickly. A new work stream is created directly from a set of search results. Narrow your search results to what you want to work through then click to create a new work stream, consisting of those items. The work stream will then appear within the dedicated widget on the dashboard.	

![](http://docs.brightspot.s3.amazonaws.com/dashboard_workstream_2.1.png)


**Custom Layout**

Each CMS user can customize their own dashboard view, moving the widgets around to suit their workflow. Hover over the top right corner of a widget to move. Click on an arrow direction to move the widgets around.

![](http://docs.brightspot.s3.amazonaws.com/dashboard_custom_2.1.png)


**Site Map**

The Site Map is a hierarchical view of your website content, showing the structure for the entire site. It is used to either find specific content, or to get a better understanding of the various items within each section. You can also filter a directory to show a particular object type within it. For example, all Blogs within the News directory. You can start typing on the content type of url to filter.

![](http://docs.brightspot.s3.amazonaws.com/sitemap_widget_2.1.png)


**Recent Activity**

Using the Recent Activity, you can jump to a piece of content that has just been edited, or scroll through all edits made in one day. It can also be used to verify when content was started and finished by a user. You can also adjust results by filtering between content types or other roles or users.

![](http://docs.brightspot.s3.amazonaws.com/recent_widget_2.1.png)


**Create New**

Create a new webpage using this widget. Select from a list of existing templates to get started, each one associated with a specific piece of content. These templates are created under Admin > Templates & Sections. Creating a new page creates content that can be accessed at a new URL. Click customize to hide certain templates you don't typically use, to keep the interface clean. This setting is on a per user basis, so you can set the dashboard up as you like it.

![](http://docs.brightspot.s3.amazonaws.com/create_widget_2.1.png)


**Work Streams**

Once a work stream has been created it appears in the widget. Click start to get to work on the task, or click into the users link to see who else is working on the work stream. In the right rail of your content edit screen you can see the work stream options, including skipping content.

![](http://docs.brightspot.s3.amazonaws.com/stream_widget_2.1.png)


**Bulk Upload**

Upload files in bulk right from the dashboard. Clicking on Upload Files provides a popup window, where you can choose your file type, and upload in bulk. Alternatively, drag and drop your files in the widget. Once uploaded, files are placed in a draft status for you to add metadata and publish. Find your newly uploaded files in the drafts widget on the dashboard.

![](http://docs.brightspot.s3.amazonaws.com/bulk_widget_2.1.png)


**Schedules**

Content that is set to go live at a future date appears in the Schedules section. Each day has a section, with any content due to go live on that day visible. Click into the content to edit. Scheduling is set from within the content edit screen, using a date widget.

![](http://docs.brightspot.s3.amazonaws.com/sch_widget_dash.png)


**Drafts**

All content that is not yet completed, but saved in a draft status, appears in the drafts module. Bulk uploaded files are also saved here awaiting editorial additions, such as alt text or titles.

![](http://docs.brightspot.s3.amazonaws.com/draft_widget_2.1.png)


**Resources**

The resources widget allows you to create custom links to content, internal, or resources, external that you want access to from within the CMS. Add as many links as you like from within Admin > Settings. You can also directly upload files, such as PDFs.

![](http://docs.brightspot.s3.amazonaws.com/resource_widget_2.1.png)


**Page Thumbnails**

Hovering over any content on the dashboard that is an individual page allows you to see a quick inline preview of the page. This allows editors to get a visual idea of what they want to create or edit. Hovering over the eye preview symbol toggles the view - click to see.

![](http://docs.brightspot.s3.amazonaws.com/thumb_preview_2.1.png)



## Publishing

#### Intro

For the following publisher tasks, the example will be a Demo Brightspot instance. This example CMS powers a version of a Perfect Sense Digital company website. Your Brightspot interface will not share all of the same names or UI elements, as it is derived from your own objects, but these examples should give you a clear understanding of how to carry out basic editorial tasks, and use the widgets.

To create a new content type select one found in the Create New section on the right. *(Note, Content that has an assigned Template will appear in this section. Creating content from here results in a new page, with a dedicated URL)*

![](http://cdn.psddev.com/cb/28/012be3a74ca5befec610361b44a1/editorial-work-view-20.40.01%20AM.png)

There are lots of options in the content edit screen, but for now, focus on creating your first piece of content.

- Enter a Title, a blurb and some body text.
- When clicking into an area of text notice how a toolbar appears. This is your Rich Text Editor, allowing you to style your content, add images and links.

## Previewing

Once you have your content complete make sure it is looking correct by reviewing the preview window. If you do not see the preview window on the right side of the content look for the `Preview` link text under Publish. If you are using the inline preview on a small screen click on the preview bar at the top to slide out the live preview view. The preview feature provides a live preview of your content positioned on your site, and allows you to check and then make edits before the content goes live.


![](http://cdn.psddev.com/87/ea/abe373bb4a5aac5f20f1f2178c6d/preview-tool-20.43.00%20AM.png)

## Creating URLs

When you are happy with your content close your preview window, or slide in the preview tool. URLs are automatically generated, visible in the right rail widget.  The logic at play grabs the first text field in your object (headline / title) and creates a URL. If your current headline is too long for your desired URL, you can select 'Manual' and add item, a simple URL 'test-article'. A slash is automatically added before the words entered. If you have added content, checked it and given it a URL – you are now ready to publish, by clicking ‘Publish’.

## Versioning


After editing or publishing content a new ‘History’ section appears in the right rail of the content edit screen. In Brightspot all versions are archived automatically.

Also, once published a ‘Create Another’ button appears at the top of the screen as a green bar.

## Saving a Draft

If you are not ready to publish your content, you have the option to 'Save as Draft', which saves a version for you to access later. The Drafts widget on the dashboard contains a reference to content saved in that status.

When your content is published you can return to the Dashboard by clicking the ‘Pages & Content’ tab.

## Adding Images

In Brightspot you can add items to content as 'Enhancements'. These can be various forms of media or modular content types. This section will look at how to add images.

Clicking into the `Body` content, seen below, opens the Rich Text Editor tools, click 'Add Enhancement' to open the menu.

![](http://cdn.psddev.com/20/86/47714e1746bda48a740276b79ed5/add-an-enhancement.29.27%20AM.png)

Once clicked you will see an 'Empty Enhancement' in your content. Click the Edit link to open the find tool. Here you can either select an existing image or any other content that has been designated as being applicable for inclusion. If you want to add a new object you can do that from here also.

![](http://cdn.psddev.com/e1/da/ecc133814354b5ce060030a05a3e/adding-an-enhancement-20.49.37%20AM.png)

You can search for an image already in the CMS, or upload a new one. Click on an image to select, then scroll down to save. Close your enhancement window, you should now see the enhancement area populated with your image name. (You will see the image previewed on save)

Once an enhancement has been added, you can use the same menu to position it within your content. The arrows offer alignment options, and also allow you to shift the enhancement above blocks of text. To try this out move your image to the very top of the article using the up arrow. Often images are added to a piece of content outside of a rich text area, an example would be a promo image, or gallery of images. 

## Bulk Image Upload

Multiple image files can be uploaded at once within Brightspot. On the dashboard, use the `Bulk Upload` widget select files, or drag and drop onto the widget.

Once uploaded, the new files are moved into Draft status, and can be accessed in the Drafts widget on the homepage, where each can be populated with the correct metadata.

## Image Editing

With every image object, you have the ability to apply numerous advanced edits. Brightness, contrast and filters can be chosen, as well as orientation changed. Any changes made can be reset.

![](http://cdn.psddev.com/65/ac/eb22b9f34d7dbc4a169da5770c8c/image-editing-20.54.10%20AM.png)

A new text overlay tool has also been added, allowing rich text to be added to any image and moved to anywhere on the crop. The text overlay is added by clicking "Add Text", which is found beside a given crop size.

![](http://cdn.psddev.com/88/07/ee4cb627440e9c6e1fedcace1cab/text-overlay-20.55.44%20AM.png)

## Cropping Images

Predefined crop sizes are found to the right of an image. These are grouped by aspect ratio. Click on your desired crop and resize and drag to overlay the area you would like to show. The crop preview on the right helps you to see the final view.

![](http://cdn.psddev.com/d6/5f/28f9f6404f39bd195b8d6d2a5964/cropping-an-image-20.57.48%20AM.png)

## Finding Content

Once content has been published, you can use the search tool to find it. Clicking into the 'Search' field in the top right of the Dashboard page, or any page globally brings up the search widget.

There are many ways in which you can narrow down your content in Brightspot. Typing automatically filters based on a text search, and using the filters on the left, you can specify the exact type of content you wish to search within. You can also search for content which is missing.

![](http://docs.brightspot.s3.amazonaws.com/advanced-search-2.1.png)

The search is also persistent, and can be reset using the reset link.

Once content has been accessed using the search tool, the results are still available, allowing an editor to click through results quickly in the content edit screen:

![](http://cdn.psddev.com/dims4/PSD/a4044b4/2147483647/resize/700x%3E/quality/90/?url=http%3A%2F%2Fcdn.psddev.com%2F61%2F8c%2F061d7dbc4912b86cd50bd2febdad%2Fpersistent-search-20.05.07%20PM.png)

## Inline Editing

Often editors will want to review their web content visually, not from within the CMS interface. When doing so, Brightspot gives you the ability to edit the content from the actual web view, without having to log back into the CMS to make changes.

The Inline Editor allows live content to be updated, images changed and if needed, full CMS access at a single click. The tool is accessed via a link within your browser. Start by logging into the CMS. Once logged in, open your site homepage and navigate to a page you would like to edit.

In the top right, you will see an `Edit Inline` link. Click it to start the Inline Editor. Once loaded, each piece of editable content has a small pencil icon, roll over and click to start editing. Once you have made your changes, click save and then refresh your page to see the new content. If you want to access and edit the content within the CMS, click `Edit in CMS`.

![](http://cdn.psddev.com/dims4/PSD/07068b3/2147483647/resize/700x%3E/quality/90/?url=http%3A%2F%2Fcdn.psddev.com%2Fce%2F8c%2Fafc400b2427e8f279b1a41c8a696%2Fbookmarklet-tool-20.16.11%20PM.png)

## Scheduling Content

Content does not have to be published the moment it is created. With scheduling, you can specify a time and day for when the content should go live.

Click into content and notice the right rail calendar link above the 'Publish` button.

![](http://cdn.psddev.com/dims4/PSD/e4ba2f3/2147483647/resize/700x%3E/quality/90/?url=http%3A%2F%2Fcdn.psddev.com%2F5c%2F71%2F22f448714eb096a31217d3b61fc4%2Fschedule-content-20.22.24%20PM.png)

Once a date has been picked click `Schedule` and not Publish.

![](http://cdn.psddev.com/dims4/PSD/3930651/2147483647/resize/700x%3E/quality/90/?url=http%3A%2F%2Fcdn.psddev.com%2F75%2F3f%2F8965a03b4fd6b74455bf5c11cea6%2Fscheduled-content-20.22.29%20PM.png)

If you want to reset the date for scheduling, simply click into the calendar view, and select a date. The `Publish` button will show `Reschedule`. 

Once a piece of content has been scheduled it appears in the Schedules widget on the dashboard beside the date for when it is due to go live.

![](http://cdn.psddev.com/dims4/PSD/4eaa575/2147483647/resize/700x%3E/quality/90/?url=http%3A%2F%2Fcdn.psddev.com%2Ffa%2Fdc%2F6ee9a45144e483cb8f5890655102%2Fschedule-2.0_brightspot.png)

## Production Guides

What drives the direction and substance of your content? Do you have rules and guidelines to help the editorial staff? With Brightspot, Production Guides are built into the tool automatically, allowing those who set the agenda for content creation to document it in great detail.

A simple to use interface enables each page, module or content type to have a corresponding guide, that allows production values, rules and tips to be made available to each editor as they create the content themselves.

No configuration is needed, the guides automatically appear for each page, your role is simply to write your own guide for the content you wish.

#### Creating a new Guide

Found within Admin -> Guides, Editors can create a guide for each page template that exists in the CMS. This allows every publishable page to have corresponding programming instructions.

**Step 1.** Start by selecting the page you would like to create a guide for on the left.

**Step 2.** Create a summary. The summary is the overview for the entire page.

**Step 3.** Pick an example page. The example page template will be shown to editors as a perfect example of what they should be replicating. This can be in the form of a screenshot, or an actual live piece of content.

**Step 4.** Every section found on the page can be documented, with an adjoining description added. As Editors browse the guide they can see each section highlighted, with the guide shown.

![](http://docs.brightspot.s3.amazonaws.com/create-a-guide-2.1.png)

#### Creating Field Instructions

Each field within an object can have instructions added. Within Admin -> Guides select an object type on the left. Each field within that object can have descriptive text added. This is made available to an editor creating content by clicking on the `?` icon for each field.

![](http://docs.brightspot.s3.amazonaws.com/add-field-guide-2.1.png)

#### Viewing a Guide

Once a guide has been created for a page template it is accessible from within the content edit view. In the top bar click "Guide" to open the pop-up window. 

![](http://docs.brightspot.s3.amazonaws.com/sample-guide-2.1.png)


#### Section Guides

Read the page summary, and then choose a particular section to find out more about how to program it. You can view in full, or print the guide out to have a hard copy.

## SEO

Custom SEO can be added to any page through an SEO widget found at the bottom of the content edit screen. Editors can added Title, Description and Keywords, to be referenced by search engines. Other options, such as setting no-index or no-follow can be configured here as well.

Brightspot adheres to, and encourages SEO best practices. Where an editor has not specified tags, Brightspot defaults to fill in the information with text from the object itself.

![](http://cdn.psddev.com/dims4/PSD/e3cbdab/2147483647/resize/700x%3E/quality/90/?url=http%3A%2F%2Fcdn.psddev.com%2F24%2Fd2%2Fc58cb78f4b03b2b6d7fdd3ee0650%2Fseo.png)

## Administration 

#### User Admin

Found in Admin -> Users and Roles, Brightspot provides a simple user interface that allows Administrators to add new users, and define their roles.

Create a new user, by default, all new users inherit the same role. This is configraued in Admin > Settings. The `Current Site` allows users with MultiSite access to define the default site for the user. 

![](http://docs.brightspot.s3.amazonaws.com/users-roles.png)

Once a user has been created, a `New Role` can be added, with customizable access setup. In the dropdowns you can choose to limit access to specific types, for example a role can have read only access to an Article. Find the object type you want access limited to, and change the check boxes. The `Areas` dropdown controls the top navigation the user sees within the CMS. Giving no access means no Admin access for that role within Brightspot

![](http://docs.brightspot.s3.amazonaws.com/users_roles.png)

![](http://docs.brightspot.s3.amazonaws.com/add-user.png)


</div>
<div class="span4 dari-docs-sidebar">
<div markdown="1" style="position:scroll;" class="well sidebar-nav">


* auto-gen TOC:
{:toc}

</div>
</div>