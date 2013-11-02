# File Cabinet

This is a vault app to organize documents. The web ui allows viewing, tagging, uploading, and deleting of documents. The worker jar will allow bulk upload, and do thumbnailing for all documents missing a thumbnail. It will also create downloadable archives if they are desired.

## Worker
1. Start up (given couch host and db name)
1. Get the server's worker_queue view. For each document listed:
  1. download json
  1. If it has no RAW, fail out
  1. If it has no SHA1, download the raw and compute it
  1. If it's missing a thumbnail but has a RAW file, create thumb
  1. If thumbnailing fails, set the thumbnail field to '::none' (or whatever is in FcDocument::NO_THUMBNAIL) so we don't try again next time
  1. Mark as unseen
  1. Save
1. Get worker status doc (contains import dir and archive requests)
1. Check import directory. For each file found:
  1. SHA1 file
  1. Look for SHA1 collisions, if exist, mark collision as unseen, and throw away local file
  1. Create json with sha1
  1. Attach raw file
  1. Create thumbnail and attach to document
  1. Delete local file
1. Check archive requests. For each one found:
  1. Create archive file
  1. Attach archive file to archive request
1. Exit

## Licences
File Cabinet is licenced under [Apache Licence 2.0][apache20]. It contains libraries licenced under:

* [Apache Licence 2.0][apache20] (Gson, Guava, Joda-Time, JCommander, imgscalr)
* [Gnu Lesser General Public Licence LGPL][lgpl] (org.swinglabs.pdf-renderer, Logback)
* MIT Licences ([Tag-it][mit-tagit], [jQuery & jQuery-UI][mit-jquery])

As the LGPL libraries were not modified in any way, they can be released under non-GPL licences.

[apache20]: http://www.apache.org/licenses/LICENSE-2.0.html
[lgpl]: http://www.gnu.org/copyleft/lesser.html
[mit-tagit]: http://aehlke.github.com/tag-it/LICENSE
[mit-jquery]: https://github.com/jquery/jquery/blob/master/MIT-LICENSE.txt
