// a very crude script to renumber my records.
// alter the file directory, file name, regex, start string, and starting int to renumber records with

myFileDirectory = "C:/dev/xc/mst/svn/branches/marc_agg/";
myFileName = "dedup_normalized.xml"
myNewFileName = "dedup_normalized2.xml"
myFile = new File(myFileDirectory + myFileName)
myNewFile = new File(myFileDirectory + myNewFileName)
def reader = myFile.newReader("UTF-8")

def String regex = ".*/[0-9]</identifier>";

def boolean checkForRegex(String s, int i, File f)
{
        // this string can change depending upon the input file
        def String start = "<identifier>oai:mst.rochester.edu:MetadataServicesToolkit/marcnormalization/"
        
        def String end = "</identifier>" + System.getProperty("line.separator")

        def String regex = ".*</identifier>";
        //need to check for pattern match!
        if (s ==~ regex)
        {
//               f.append(start+i+end)
               f.withWriterAppend("UTF-8") { writer -> writer.append(start+i+end) } 
               return true
         } else {
//               f.append(s + System.getProperty("line.separator"))
               f.withWriterAppend("UTF-8") { writer -> writer.append(s + System.getProperty("line.separator")) } 
               return false
        }
}

// i is what you want the renumbering to start with.
def int i = 1
reader.eachLine{
 result = checkForRegex(it, i, myNewFile) 
 if (result) {i++}
}
