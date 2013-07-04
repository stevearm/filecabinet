# File Cabinet

This jar starts a webserver to allow for an easy interface. Any files in ./incoming will be hashed, thumbnailed, and filed in ./files. The web ui will show all files, allow for tagging and setting a date, marks new files as unseen until someone sets otherwise, and allows for exporting files again.

This was developed mostly so I could keep track of large collections of bills, documents, bank statements, taxes, etc while still keeping everything on a simple directory (or backed-up folder like Cloudstation or Dropbox).

## To do
* Add a bulk export that lets you select some files and download a zip
** Renaming service for bulk export?

## Licences
File Cabinet contains libraries licenced under:

* [Apache Licence 2.0][apache20] (Jetty, Gson, Guava, Joda-Time, JCommander)
* [Gnu Lesser General Public Licence LGPL][lgpl] (org.swinglabs.pdf-renderer, Logback)

As the LGPL libraries were not modified in any way, they can be released under non-GPL licences, so File Cabinet is licenced under [Apache Licence 2.0][apache20].

[apache20] : http://www.apache.org/licenses/LICENSE-2.0.html
[lgpl] : http://www.gnu.org/copyleft/lesser.html
