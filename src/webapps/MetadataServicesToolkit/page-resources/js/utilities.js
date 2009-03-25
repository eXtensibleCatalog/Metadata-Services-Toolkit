 /*
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */

/*
 * Function to create error message div
 *
 */
function createErrorDiv(errorType, errorMessage) {


		var errorDiv = document.getElementById('error_div');
		
		/* Remove error div if its existing */
		var existingErrorMessageDiv = document.getElementById('message_div');
		if (existingErrorMessageDiv != null) {
			errorDiv.removeChild(existingErrorMessageDiv);
		
		}
		
		var messageDivElement = document.createElement('div');
		messageDivElement.setAttribute('id', 'message_div');
		messageDivElement.setAttribute('class',errorType);
		
		var imgElement = document.createElement('img');
		imgElement.setAttribute('src',basePath + 'page-resources/img/' + errorType +'.jpg');
		
		var newDivElement = document.createElement('div');
		newDivElement.setAttribute('class', 'jsErrorMessage');
		newDivElement.innerHTML = errorMessage;
		
		messageDivElement.appendChild(imgElement);
		messageDivElement.appendChild(newDivElement);
		errorDiv.appendChild(messageDivElement);
			
}