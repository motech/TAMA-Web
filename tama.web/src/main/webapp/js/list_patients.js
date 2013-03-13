dojo.addOnLoad(function () {

    dojo.forEach(dojo.query("tr"), function (element, index) {

        if (element['cells'].length < 6 || element['cells'][0].nodeName !== "TD")
            return;

        dojo.forEach(element.childNodes, function (cell, index) {
            if (index === 6 && cell.innerText.replace(/\s+/g, '') === "Deactivate") {
                if (cell != null && cell.children != null && cell.children.length > 0 && cell.children[0].children != null && cell.children[0].children.length > 0)
                    dojo.addClass(cell.children[0].children[0], "greyText");
            }
        });
    });
});
