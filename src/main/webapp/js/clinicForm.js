dojo.addOnLoad(function() {
    dojo.connect(dojo.byId("form"), "onsubmit", function(evt) {
        dojo.forEach(dojo.query(".contacts .dijitInputInner"), function(element, index) {
            if (!element.value)
                element.disabled = true;
        });
    });
});