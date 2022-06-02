# elwiki_jsp

ElWiki (Enhanced Links wiki) is clone of project [apache/jspwiki](https://github.com/apache/jspwiki)
> clone of v2.11.0-M7-git-19

It is porting under Eclipse, using the following: Equinox OSGi, EMF, CDO and other required stuff.

ElWiki appears a complete application and works using the built-in [Jetty](https://www.eclipse.org/jetty/).


## The main differences

* links between pages are provided by the page index, not the page name
* wiki data is implemented by the EMF model
* CDO provides data storage management


### These improvements allow

* to avoid scanning all pages at startup wiki
* to avoid changing content of pages when changed the page name of targeted page
* to ensure data integrity, it is possible to repeatedly change the page name.
* adding simple implementation a page hierarchy mechanism (supported by data model)
* the ability to separate topics (one topic - is another content, wiki); that is, there are several wikis under one control. Switching between them, sharing multiple Wikis, for example for search.

## Currently

Now, JSPWiki functions are being ported.

The project is being assembled, pages are being viewed, links are working.
The project can be run only from the IDE, the installer has not yet been implemented.
