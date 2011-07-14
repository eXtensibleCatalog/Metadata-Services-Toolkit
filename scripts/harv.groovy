c=486000
for (i=0; i<10; i++) {
	n = (i*5000) + c;
	println "$n"	
	
	def proc = """curl -s http://128.151.244.135:8080/OAIToolkit/oai-request.do?verb=ListRecords&resumptionToken=18%7C$n""".execute();
	t = proc.in.text
	new File("oai-${n}.out.xml").write(t);
	println proc.err.text;
	println proc.exitValue();
}

