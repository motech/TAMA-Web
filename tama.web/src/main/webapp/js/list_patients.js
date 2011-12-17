dojo.addOnLoad(function() {
    dojo.forEach(dojo.query("a.post_anchor"), function(element, index) {
        dojo.connect(element, "onclick", function() {
            element.parentNode.submit();
        });
    });
})
