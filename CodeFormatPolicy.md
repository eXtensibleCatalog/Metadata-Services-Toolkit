  * spaces instead of tabs for all source files
  * 4 spaces for java files
  * We will follow the ["Java Code Conventions for the Java Programming Language"](http://www.oracle.com/technetwork/java/codeconvtoc-136057.html)
```
if {
    //do something
} else {
    //do somethinng else
}
```
  * 2 spaces for all non-java source files (xml, js, jsp, html, etc)
  * Our current code base is not in conformity with the above.  Our strategy will be to slowly apply these formats as we work in certain files, but not go out of our way to do so.
  * To configure eclipse to support the above:
    * window->preferences from the menubar
      * General->Editors->Text Editors
        * Displayed tab width: 4
        * insert spaces for tabs
        * show line numbers
      * General->Workspace
        * Text file encoding: other/utf-8
        * new text file line delimiter: other/unix
      * General->Workspace
        * web->html files->editor
          * line width: 500
          * indent using spaces
            * indentation size: 2
      * java->code style->formatter
        * under "Active profile:" change the drop-down to "Java Conventions [in](built.md)"
        * click edit
          * change "Profile name:" to "XC convention"
          * "Indentation" tab
            * Tab Policy: "Spaces Only"
            * change "Indentation size" to 4
          * "Line Wrapping" tab
            * change "Maximum line width" to 500
            * check "Never join already wrapped lines"
            * change "Line wrapping policy" drop down to "Do not wrap"
          * "Comments" tab
            * check "Never join lines"
            * change "Maximum line width for comments" to 500
          * click "OK"
    * for easy switching from tabs to spaces, install http://andrei.gmxhome.de/anyedit/
  * change your author name - http://mhashem.wordpress.com/2009/01/17/change-author-name-in-eclipse/
  * If a code file has a need to a great deal of change to fit the above standards (besides white space changes), make that commit on it's own.  IOW, don't pack a bunch of formatting changes in with actual code changes in one commit.  Do them separately.

### Eclipse build options ###
I have had a problem with Eclipse resolving references properly at times, this helped:

http://philip.yurchuk.com/2008/12/03/eclipse-cannot-be-resolved-to-a-type-error/

in particular this helped me today:

Based on the comments here, I started checking how I could make sure that my Ant build wouldn’t interfere with my Eclipse build.

I found an option in Eclipse that clears up the problem (and is possibly more efficient than changing the 2 build system’s output folders).

Windows–>Preferences–>Java–>Compiler–>Building–>Output folder–>”Rebuild class files modified by others”. This exists in Eclipse 3.5; I’m not sure about earlier versions.

### mass edit ###
on 2011-08-19 I finally had enough with formatting and decided to do one massive change (revisions 2460-2463).
  * eclipse: highlighted all folders in "package explorer" except for the projects themselves and JRE System Library, References Library, and build.  file->convert tabs to spaces
  * eclipse: highlighted all folders in "package explorer" except for the projects themselves and JRE System Library, References Library, and build.  right click->source->format
  * CRLF -> LF
```
$ find . -printf '"%h/%f"\n' | grep -v svn | grep -v build | grep -v bin | xargs grep -IUl $(echo '0d0a' | xxd -r -p)  | xargs dos2unix
```