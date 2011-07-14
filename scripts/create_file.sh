for (int i=0; i<10; i++) {
	for (int j=0; j<1024; j++) {
		StringBuilder sb = new StringBuilder();
			for (int k=0; k<1024; k++) {
				sb.append("1");
			}
		println sb.toString();
	}
}
