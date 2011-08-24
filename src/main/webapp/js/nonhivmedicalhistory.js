dojo.require("dojo.NodeList-traverse");

dojo.addOnLoad(function() {

   var neighbour = function(descripton_element) {
     if (dojo.hasClass(descripton_element, 'YES'))
        return dojo.query(descripton_element).parent().prev().query("input[type='text']");
     else if (dojo.hasClass(descripton_element, 'YES_WITH_HISTORY'))
        return dojo.query(descripton_element).parent().next().query("input[type='text']");
     else
        return dojo.query(descripton_element).parent().parent().query("input[type='text']");
   };



   dojo.forEach(dojo.query(".has_description"), function(element, index) {
      dojo.connect(element, "onclick", function(event) {
        dojo.forEach(dojo.query(element).parent().query("input[type='text']"), function(elem, index) {
           elem.disabled = false;
        });
        dojo.forEach(neighbour(element), function(elem, index) {
           elem.disabled = true;
        });
      });
   });
});