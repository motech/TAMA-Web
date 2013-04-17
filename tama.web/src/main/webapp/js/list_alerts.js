dojo.addOnLoad(function () {

    dojo.forEach(dojo.query("tr td"), function (element, index) {

        if(element.innerHTML.indexOf("Open") >= 0){
            dojo.addClass(element, "redText");
        }

        if(element.innerHTML.indexOf("Closed") >= 0){
            dojo.addClass(element, "greenText");
        }
    });
});
