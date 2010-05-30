function post_to_url(path, params, method) {
    method = method || "post"; // Set method to post by default, if not specified.

    // The rest of this code assumes you are not using a library.
    // It can be made less wordy if you use one.
    var form = document.createElement("form");
    form.setAttribute("method", method);
    form.setAttribute("action", path);

    for(var key in params) {
        var hiddenField = document.createElement("input");
        hiddenField.setAttribute("type", "hidden");
        hiddenField.setAttribute("name", key);
        hiddenField.setAttribute("value", params[key]);

        form.appendChild(hiddenField);
    }

    document.body.appendChild(form);    // Not entirely sure if this is necessary
    form.submit();
}


function initialiseStateFromURL() {
     var hash = window.location.hash;
	 if (hash.length > 1)
     {
    	 var url = window.location.protocol + "//" + window.location.host + window.location.pathname;
    	 var params = new Array();
    	 hash = hash.substring(1);
    	 var pairs = hash.split('&');
    	 for (var i in pairs)
    	 {
    		 var split = pairs[i].split('=');
    		 params[split[0]] = split[1];
    	 }
    	 post_to_url(url, params);
     }
}