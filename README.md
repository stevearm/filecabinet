# File Cabinet

This is a vault app to organize documents. The web ui allows viewing, tagging, uploading, and deleting of documents. The worker jar will allow bulk upload, and do thumbnailing for all documents missing a thumbnail. It will also create downloadable archives if they are desired.

## Models

    {
      type: "document"
      raw: String (the key for finding the raw file in attachments)
      sha1: String (the SHA1 of the attachment pointed to by 'raw')
      thumbnail: String (the key for finding the thumbnail in attachments, if one is chosen)
      seen: boolean (if false, show this to users)
      processed: boolean (if false, show this to the worker)
      uploaded: ISO-8601 String
      effective: ISO-8601 String (what day the document relates to. If it's a bill, this would be the issue day)
      tags: String[] (a list of tags this document relates to)
      _attachments: {
        The raw file is uploaded here. The name is the original filename, with anything [^0-9A-Za-z-_.()] replaced with an underscore.
        Thumbnails are uploaded here, all starting with "thumb/"
      }
    }

## Views
* Worker queue (anything with `processed` missing or false)
* Human queue (anything with `seen` missing or false)
* Problems (anything with missing or invalid `raw`, or invalid `thumbnail`)

## Worker
1. Start up (given couch host and db name)
1. For each doc in worker queue:
  1. download json
  1. If it has no RAW, fail out
  1. If it has no SHA1, download the raw and compute it
  1. If thumbnail is missing or null or empty:
    1. Create a thumbnail for every algorithm known
  1. Mark as processed
  1. Save

## Additional worker jobs (not yet implemented)
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
