/*  This creates a json object to return to the client  
    This is for dealing with deleting a repository
*/
<jsp:directive.page contentType="application/javascript"/>

{
    "repositoryDeleted" : "${deleted}",
    "message" : "${message}"
}
