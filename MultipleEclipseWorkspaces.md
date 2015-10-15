My strategy for working with multiple branches has been thus:

  * create separate working directories for each branch you work on
  * create separate eclipse workspaces (in a separate directory) for each branch

The downside to doing this is that you had to reimport your project for each branch in eclipse (settings, projects, etc).  I thought the solution would be to find a way to clone an eclipse workspace, but it seems that is not possible.  I'm going to try another option.  Apparently NTFS has symlinks now (http://en.wikipedia.org/wiki/NTFS_symbolic_link).  I will attempt to create 1 (or 2) workspaces.  All you'd have to do is change the symlink and you're good to go.

  * .project and .classpath inside each project are checked into svn
  * eclipse workspace potentially could be ported assuming the same directory for the workspace (C:\dev\eclipse\workspaces\mst\_1) and linked directory (C:\dev\xc\mst\svn\workspace\_1)
```
rmdir workspace_1
mklink /D workspace_1 trunk
```

#### when creating your new workspace ####
  * see the CodeFormatPolicy page