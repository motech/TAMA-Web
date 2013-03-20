dojo.addOnLoad(function () {

    dojo.forEach(dojo.query("tr td"), function (element, index) {

        if(element.innerHTML.indexOf("Open") >= 0){
            dojo.addClass(element, "blueText");
        }
    });
});
