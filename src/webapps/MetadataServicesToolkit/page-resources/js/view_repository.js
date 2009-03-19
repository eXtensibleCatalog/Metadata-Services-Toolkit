YAHOO.namespace("xc.mst.repository");

YAHOO.xc.mst.repository.MyObject = {

    delRepository : function(id)
    {
        var result;
        result = confirm('Are you sure you want to delete the repository ? ');
        if (result)
        {
            window.location = "deleteRepository.action?RepositoryId="+id;
        }

    },
    doneFunction:function()
    {
        window.location = "allRepository.action";
    },
    reValidateFunction : function(id)
    {
        window.location = "viewRepositoryValidate.action?RepositoryId="+id;
    },
    editFunction : function(id)
    {
        window.location = "viewEditRepository.action?RepositoryId="+id;
    },
    removeErrorMessage : function(id)
    {
        window.location = "viewRepository.action?RepositoryId="+id;
    }

}