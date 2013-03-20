dojo.addOnLoad(function () {

    dojo.forEach(dojo.query("tr td"), function (element, index) {

        if(element.innerHTML.indexOf("Deactivate") >= 0){
            dojo.addClass(dojo.query("a", element), "greyText");
        }
    });
});
