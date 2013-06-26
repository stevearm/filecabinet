On load:
* walk through /cabinet looking for *.json files
* For each json found, read it and push tags, ids, and filenames into memdb

On import:
* Scan /desk for files, and allow files to be uploaded
* With new file, sha1 it and copy to destination
* Create new meta object and save the filename
* Write out meta object
* Push meta object into memdb
