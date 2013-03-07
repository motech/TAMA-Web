dojo.addOnLoad(function() {
    dojo.forEach(dojo.query("a.post_anchor"), function(element, index) {
        dojo.connect(element, "onclick", function() {
            element.parentNode.submit();
        });
    });

    dojo.forEach(dojo.query("tr"), function(element, index) {

        if(element['cells'].length < 8 || element['cells'][0].nodeName !== "TD")
            return;

        var cssStyle = "highlightClosedAlert";

        if(element['cells'][8].innerText === "Open")
            cssStyle = "highlightOpenAlert";

        dojo.forEach(element.childNodes, function(cell, index){
            dojo.addClass(cell, cssStyle);
        });
    });
});
