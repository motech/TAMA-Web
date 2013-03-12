dojo.addOnLoad(function () {

    dojo.forEach(dojo.query("tr"), function (element, index) {

        if (element['cells'].length < 8 || element['cells'][0].nodeName !== "TD")
            return;

        var cssStyle = "highlightClosedAlert";

        if (element['cells'][8].innerText === "Open")
            cssStyle = "highlightOpenAlert";

        dojo.forEach(element.childNodes, function (cell, index) {
            dojo.addClass(cell, cssStyle);
            if (cell.innerText === "Open") {
                dojo.addClass(cell, "blueText");
            }
        });
    });
});
