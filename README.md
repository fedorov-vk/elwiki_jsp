# elwiki_jsp

ElWiki is clone of project [apache/jspwiki](https://github.com/apache/jspwiki)
> clone of v2.11.0-M7-git-19

It is porting under Eclipse, using the following: Equinox OSGi, EMF, CDO and other required stuff.

ElWiki appears a complete application and works using the built-in [Jetty](https://www.eclipse.org/jetty/).


## The main differences

* links between pages are provided by the page index, not the page name.
* wiki data is implemented by the EMF model
* Data storage management provides CDO


# This allows

* avoid scaning all pages, when changed the page name, to modify links to this page
* to ensure data integrity, it is possible to repeatedly change the page name.
* simple implement a page hierarchy mechanism
* the ability to separate topics == multiple Wikis. Switching between them, sharing multiple Wikis, for example for search.

## Currently

Now, JSPWiki functions are being ported.

The project is being assembled, pages are being viewed, links are working.
The project can be run only from the IDE, the installer has not yet been implemented.
