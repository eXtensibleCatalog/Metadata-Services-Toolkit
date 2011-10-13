 /*
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
 YAHOO.namespace("xc.mst.repository.viewRepository");

YAHOO.xc.mst.repository.viewRepository = {

    done:function()
    {
        window.location = "allRepository.action";
    },

    createValidateDialog : function()
    {

    var reValidateFunction = function() {
      document.repositoryForm.action = "viewRepositoryValidate.action";
            document.repositoryForm.submit();
    };


    // Instantiate the Dialog
    // make it modal -
    // it should not start out as visible - it should not be shown until
    // new repository button is clicked.
    YAHOO.xc.mst.repository.viewRepository.processingDialog = new YAHOO.widget.Dialog('processingDialog',
    { width : "400px",
        visible : false,
        modal : true

      } );


    // Show the dialog
         YAHOO.xc.mst.repository.viewRepository.processingDialog.showDialog = function()
         {
       YAHOO.xc.mst.repository.viewRepository.processingDialog.show();
       YAHOO.xc.mst.repository.viewRepository.processingDialog.center();
       reValidateFunction();
         },


    // Render the Dialog
    YAHOO.xc.mst.repository.viewRepository.processingDialog.render();




     // listener for showing the dialog when clicked.
    YAHOO.util.Event.addListener("revalidateRepository", "click",
        YAHOO.xc.mst.repository.viewRepository.processingDialog.showDialog,
        YAHOO.xc.mst.repository.viewRepository.processingDialog, true);



    },

    editFunction : function()
    {
        document.repositoryForm.action = "viewEditRepository.action";
        document.repositoryForm.submit();
    },


    downloadFile: function(type,id)
    {
        window.location = "downloadLogFile.action?logType="+type+"&id="+id;
    },


  /**
   *  Dialog to confirm repository delete
   */
  createDeleteRepositoryDialog : function()
  {


    // Define various event handlers for Dialog
    var handleSubmit = function() {
        YAHOO.util.Connect.setForm('deleteRepository');

        //delete the repository
        var cObj = YAHOO.util.Connect.asyncRequest('post',
              'deleteRepository.action', callback);
    };


    // handle a cancel of deleting repository dialog
    var handleCancel = function() {
        YAHOO.xc.mst.repository.viewRepository.deleteRepositoryDialog.hide();
    };

    // handle a Yes of deleting repository dialog
    var handleYes = function() {

       YAHOO.xc.mst.repository.viewRepository.deleteRepositoryOkDialog.hide();

      // Instantiate the Dialog
      // make it modal -
      // it should not start out as visible - it should not be shown until
      // new repository button is clicked.
      YAHOO.xc.mst.repository.viewRepository.deleteProcessingDialog = new YAHOO.widget.Dialog('deleteProcessingDialog',
      { width : "400px",
          visible : false,
          modal : true

        } );


      // Show the dialog
           YAHOO.xc.mst.repository.viewRepository.deleteProcessingDialog.showDialog = function()
           {
         YAHOO.xc.mst.repository.viewRepository.deleteProcessingDialog.show();
         YAHOO.xc.mst.repository.viewRepository.deleteProcessingDialog.center();
           },


      // Render the Dialog
      YAHOO.xc.mst.repository.viewRepository.deleteProcessingDialog.render();


      YAHOO.xc.mst.repository.viewRepository.deleteProcessingDialog.showDialog();
      document.deleteRepositoryRecords.submit();

    };

    // handle a No of deleting repository dialog
    var handleNo = function() {
        YAHOO.xc.mst.repository.viewRepository.deleteRepositoryOkDialog.hide();
    };

    var handleSuccess = function(o) {

        //get the response from adding a repository
        var response = eval("("+o.responseText+")");


        //if the repository was not deleted then show the repository the error message.
        // received from the server
        if( response.repositoryDeleted == "false" )
        {
          YAHOO.xc.mst.repository.viewRepository.deleteRepositoryDialog.hide();
            var deleteRepositoryError = document.getElementById('deleteRepositoryError');
                   deleteRepositoryError.innerHTML = '<p id="newDeleteRepositoryError">'
                   + response.message + '</p>';
                 YAHOO.xc.mst.repository.viewRepository.deleteRepositoryOkDialog.showDialog();

        }
        else
        {
            // we can clear the form if the repositorys were deleted
            YAHOO.xc.mst.repository.viewRepository.deleteRepositoryDialog.hide();
            window.location = 'allRepository.action?isAscendingOrder=true&amp;columnSorted=RepositoryName';
        }
    };

    // handle form submission failure
    var handleFailure = function(o) {
        alert('repository submission failed ' + o.status);
    };



    // Instantiate the Dialog
    // make it modal -
    // it should not start out as visible - it should not be shown until
    // new repository button is clicked.
    YAHOO.xc.mst.repository.viewRepository.deleteRepositoryDialog = new YAHOO.widget.Dialog('deleteRepositoryDialog',
          { width : "400px",
        visible : false,
        modal : true,
        buttons : [ { text:'Yes', handler:handleSubmit },
              { text:'No', handler:handleCancel, isDefault:true } ]
      } );


         // Show the dialog
         YAHOO.xc.mst.repository.viewRepository.deleteRepositoryDialog.showDialog = function()
         {
             YAHOO.xc.mst.repository.viewRepository.deleteRepositoryDialog.show();
             YAHOO.xc.mst.repository.viewRepository.deleteRepositoryDialog.center();
         }


    // Wire up the success and failure handlers
    var callback = { success: handleSuccess,  failure: handleFailure };


    // Render the Dialog
    YAHOO.xc.mst.repository.viewRepository.deleteRepositoryDialog.render();

         // listener for showing the dialog when clicked.
    YAHOO.util.Event.addListener("confirmDeleteRepository", "click",
        YAHOO.xc.mst.repository.viewRepository.deleteRepositoryDialog.showDialog,
        YAHOO.xc.mst.repository.viewRepository.deleteRepositoryDialog, true);


    YAHOO.xc.mst.repository.viewRepository.deleteRepositoryOkDialog = new YAHOO.widget.Dialog('deleteRepositoryOkDialog',
    { width : "400px",
        visible : false,
        modal : true,
        buttons : [ { text:'Yes', handler:handleYes},
                 { text:'No', handler:handleNo, isDefault:true }]
    } );

         // Show the dialog with error message
         YAHOO.xc.mst.repository.viewRepository.deleteRepositoryOkDialog.showDialog = function()
         {
            YAHOO.xc.mst.repository.viewRepository.deleteRepositoryOkDialog.show();
            YAHOO.xc.mst.repository.viewRepository.deleteRepositoryOkDialog.center();
         }

    // Render the Dialog
    YAHOO.xc.mst.repository.viewRepository.deleteRepositoryOkDialog.render();

  },



  /**
   * initialize the page
   */
  init : function()
  {
      YAHOO.xc.mst.repository.viewRepository.createDeleteRepositoryDialog();
      YAHOO.xc.mst.repository.viewRepository.createValidateDialog();
  }

}

// initialize the code once the dom is ready
YAHOO.util.Event.onDOMReady(YAHOO.xc.mst.repository.viewRepository.init);
