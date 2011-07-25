var populateComboBox = function(urlToLoadFrom, target) {
    dojo.require("dojo.data.ItemFileWriteStore");
    dojo.xhrGet({
        url: urlToLoadFrom,
        handleAs: "json",
        load: function(result) {
            var options = new dojo.data.ItemFileWriteStore({data: {identifier: "id", label: "name", items: []}});
            for (var i=0; i < result.length; i++) {
              options.newItem({name: result[i].displayName, id: result[i].id });
            }
            target.attr('store', options);
            target.attr('value', result[0].id);
        },
        error: function(result, args){
           if(result.status===601){
            window.location.reload();
           }
        }
    });
}

function addDays(date, days){
    date.setDate(date.getDate()+days);
    return date;
}


var parseScript = function(_source) {
    var source = _source;
    var scripts = new Array();

    // Strip out tags
    while(source.indexOf("<script") > -1 || source.indexOf("</script") > -1) {
        var s = source.indexOf("<script");
        var s_e = source.indexOf(">", s);
        var e = source.indexOf("</script", s);
        var e_e = source.indexOf(">", e);

        // Add to scripts array
        scripts.push(source.substring(s_e+1, e));
        // Strip from source
        source = source.substring(0, s) + source.substring(e_e+1);
    }

    // Loop through every script collected and eval it
    for(var i=0; i<scripts.length; i++) {
        try {
            eval(scripts[i]);
        }
        catch(ex) {
            // do what you want here when a script fails
        }
    }

    // Return the cleaned source
    return source;
}