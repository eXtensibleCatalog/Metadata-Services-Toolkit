/*
def dircmd = [' cmd' , ' /c' , ' dir' ]
def dir    = /\Program Files/
def proc   = (dircmd + dir) . execute()
*/
//def proc = "grep -n ../../../cool2.xml '.*'".execute()
/*
def proc = "grep -n 'marc:record' ../../../cool2.xml".execute()
for (int i=0; i<proc.length; i++) {
	def n1
}
print proc.text
*/

file = new File('../../../cool2.xml')
file.eachLine{print it}

