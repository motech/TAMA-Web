dojo.addOnLoad(function () {

    dojo.forEach(dojo.query("tr"), function (element, index) {

        if (element['cells'].length < 8 || element['cells'][0].nodeName !== "TD")
            return;

        dojo.forEach(element.childNodes, function (cell, index) {
            if (index === 8 && cell.innerText === "Open") {
                dojo.addClass(cell, "blueText");
            }
        });
    });
});
