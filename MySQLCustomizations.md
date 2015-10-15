an unorganized list of things we did to optimize our mysql

  * myisam instead of innodb
  * batch inserts using PreparedStatements
  * custom my.cnf - although this didn't really help.  The initial changes can be viewed [here](http://code.google.com/p/xcmetadataservicestoolkit/source/diff?spec=svn1127&r=1076&format=side&path=/branches/bens_perma_branch/server-conf/my.cnf&old_path=/branches/bens_perma_branch/server-conf/my.cnf&old=1074).
  * no one-off queries during record processing