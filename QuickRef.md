  * 137
    * imports
      * become voyager user (ssh as benjamina, sudo su - voyager)
      * /import/marc\_extract is where the update files are
      * I moved files to /export/home/voyager/marc\_extract\_dont\_import\_quite\_yet
      * cd /import/OAIToolkit/
      * run custom\_cml.sh

```
ls -1 /cygdrive/c/dev/xc/mst/svn/branches/bens_perma_branch/mst-service/example/build/lib/*.jar | gawk '{t=t";"$1} END {gsub(/\/cygdrive\/c/, "c:", t); print t}'
```