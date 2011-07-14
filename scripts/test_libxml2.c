#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/time.h>
#include <libxml/xmlmemory.h>
#include <libxml/parser.h>
#include <libxml/xmlstring.h>

void parseStory (xmlDocPtr doc, xmlNodePtr cur) {

    xmlChar *key;
    cur = cur->xmlChildrenNode;
    while (cur != NULL) {
        if ((!xmlStrcmp(cur->name, (const xmlChar *)"keyword"))) {
            key = xmlNodeListGetString(doc, cur->xmlChildrenNode, 1);
            printf("keyword: %s\n", key);
            xmlFree(key);
        }
    cur = cur->next;
    }
    return;
}

long long getms() {
    struct timeval tv0;
    gettimeofday(&tv0,(void*)0);
    long long ms = tv0.tv_sec * 1000LL;
    ms = ms + (tv0.tv_usec / 1000LL);

    return ms;
}


static void parseDoc(char *docname) {

    int loops=50;
    int j=0;
    long long totaldiffs=0;
    FILE *fp = fopen(docname, "r+");

    int i, c;
    char buffer[7994];
    /* ... Open a file to read using the stream fp ... */
    i = 0;
    while ( i < 7994 &&                 // While there is space in the buffer
            ( c = fgetc( fp )) != EOF ) // ... and the stream can deliver
      buffer[i++] = (char)c;            // characters.
    if ( i < 7994 && ! feof(fp) )
      fprintf( stderr, "Error reading.\n" );

    fprintf( stdout, "read %d chars.\n", i );
    fclose(fp);

    while (j<loops) {
        j++;
        //printf("%d\n", j);

        long long t0 = getms();

        xmlDocPtr doc;
        xmlNodePtr cur;
        
        //doc = xmlParseFile(docname);
        doc = xmlParseMemory(buffer, 7994);

        
        if (doc == NULL ) {
            fprintf(stderr,"Document not parsed successfully. \n");
            return;
        }
        
        cur = xmlDocGetRootElement(doc);
        
        /*
        if (cur == NULL) {
            fprintf(stderr,"empty document\n");
            xmlFreeDoc(doc);
            return;
        }
        
        if (xmlStrcmp(cur->name, (const xmlChar *) "story")) {
            fprintf(stderr,"document of the wrong type, root node != story");
            xmlFreeDoc(doc);
            return;
        }
        
        cur = cur->xmlChildrenNode;
        while (cur != NULL) {
            if ((!xmlStrcmp(cur->name, (const xmlChar *)"storyinfo"))){
                parseStory (doc, cur);
            }
             
        cur = cur->next;
        }
        */
        
        xmlFreeDoc(doc);

        long long t1 = getms();
        totaldiffs = totaldiffs+(t1-t0);
    }
    printf("totaldiffs: %lld\n", totaldiffs);
    printf("avg: %f", (((float)totaldiffs)/loops));

    return;
}


int main(int argc, char **argv) {

    char *docname;
        
    if (argc <= 1) {
        printf("Usage: %s docname\n", argv[0]);
        return(0);
    }

    docname = argv[1];
    parseDoc (docname);

    return (1);
}

