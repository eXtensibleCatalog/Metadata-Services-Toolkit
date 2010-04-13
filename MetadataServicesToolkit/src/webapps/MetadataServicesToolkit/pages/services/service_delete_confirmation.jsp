/*  This creates a json object to return to the client  
    This is for dealing with deleting a service
*/
<jsp:directive.page contentType="application/javascript"/>

{
    "serviceDeleted" : "${deleted}",
    "invalidServiceDeleteStatus" : "${invalidServiceDeleteStatus}",
    "message" : "${message}"
}
