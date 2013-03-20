dojo.addOnLoad(function () {

    dojo.forEach(dojo.query("tr td"), function (element, index) {

        if(element.innerHTML.indexOf("Open") >= 0){
            dojo.addClass(dojo.query("a", element)[0], "blueText");
        }
    });
});
