#!/bin/sh
DIR=`dirname $0`
cd $DIR

LIBDIR="lib"
for J in `ls $LIBDIR`; do
   CLASSPATH="${CLASSPATH}:${LIBDIR}/${J}";
done

SOLRCOREDIR="solr"
SOLRDATADIR="solr/data"

java -cp "$CLASSPATH" xc.mst.utils.OptimizeSolrIndex "${SOLRCOREDIR}" "${SOLRDATADIR}"
