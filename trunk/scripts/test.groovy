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

//file = new File('../../../cool2.xml')
//file.eachLine{print it}

def d0 = new Date();
d0.setYear(100);
println d0
def d1 = new Date();
def diffInSecs = (d1.getTime()-d0.getTime())/1000;
println diffInSecs;
