 /*
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

YAHOO.namespace("xc.mst.account");

YAHOO.xc.mst.account = {

  save : function()
  {

    if (document.getElementById('user_first_name').value=='') {
      createErrorDiv("error",'User first Name is required.');
      return false;
    }

    if (document.getElementById('user_last_name').value=='') {
      createErrorDiv("error",'User last Name is required.');
      return false;
    }
    if (document.getElementById('user_email').value=='') {
      createErrorDiv("error",'Email is required.');
      return false;
    } else {
      if (!YAHOO.xc.mst.account.emailCheck(document.getElementById('user_email').value)) {
        createErrorDiv("error","Invalid E-mail ID");
        return false;
      }

    }

    document.myAccountForm.action=  'saveMyAccount.action';
    document.myAccountForm.submit();

  },

  cancel : function()
  {
    document.myAccountForm.action=  'allRepository.action';
    document.myAccountForm.submit();

  },

  changePassword : function(userId)
  {
    document.myAccountForm.action=  'viewChangePassword.action';
    document.myAccountForm.submit();

  },

  emailCheck : function(str)
  {
      var at="@"
      var dot="."
      var lat=str.indexOf(at)
      var lstr=str.length
      var ldot=str.indexOf(dot)
      if (str.indexOf(at)==-1){
         return false
      }

      if (str.indexOf(at)==-1 || str.indexOf(at)==0 || str.indexOf(at)==lstr){
         return false
      }

      if (str.indexOf(dot)==-1 || str.indexOf(dot)==0 || str.indexOf(dot)==lstr){
          return false
      }

       if (str.indexOf(at,(lat+1))!=-1){
          return false
       }

       if (str.substring(lat-1,lat)==dot || str.substring(lat+1,lat+2)==dot){
          return false
       }

       if (str.indexOf(dot,(lat+2))==-1){
          return false
       }

       if (str.indexOf(" ")!=-1){
          return false
       }

        return true
  }


}

