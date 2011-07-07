var populateComboBox = function(urlToLoadFrom, target) {
    dojo.require("dojo.data.ItemFileWriteStore");
    dojo.xhrGet({
        url: urlToLoadFrom,
        handleAs: "json",
        load: function(result) {
            var options = new dojo.data.ItemFileWriteStore({data: {identifier: 'name', items: []}});
            for (var i=0; i < result.length; i++) {
              options.newItem({name: result[i]});
            }
            target.attr('store', options);
            target.attr('value', result[0]);
        }
    });
}
