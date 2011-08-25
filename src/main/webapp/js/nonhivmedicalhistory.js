dojo.require("dojo.NodeList-traverse");

dojo.addOnLoad(function() {

    var displayDescriptionIfAllergySelected = function (index) {
    return function() {
        var allergyCheckbox = dojo.byId('c_org_motechproject_tama_domain_Patient_allergy' + index);
        var allergyDescription = dojo.byId('_c_org_motechproject_tama_domain_Patient_allergy_arv_description' + index + '_id');
        var allergyDescriptionWidget = dijit.byId('_medicalHistory.nonHivMedicalHistory.allergiesHistory[' + index + '].description_id');
        if (allergyCheckbox.checked) {
            setRequiredForElement(allergyDescriptionWidget, true);
            showElement([allergyDescription]);
        }
        else {
            clearElement(allergyDescriptionWidget);
            setRequiredForElement(allergyDescriptionWidget, false);
            hideElement([allergyDescription]);
        }
      };
    }
    displayDescriptionIfAllergySelected();
    dojo.forEach([0, 1, 2], function(element, index) {
      displayDescriptionIfAllergySelected(index)();
      dojo.connect(dojo.byId('c_org_motechproject_tama_domain_Patient_allergy'+index), 'onclick', displayDescriptionIfAllergySelected(index));
    });

    dojo.forEach(dojo.query(".requires_comment_true"), function(element, index) {
       dojo.forEach(dojo.query("#" + element.id).parent().next(), function(widgetElement, index) {
          descriptionWidget = dijit.byId(widgetElement.getAttribute("widgetid"));
          descriptionWidget.domNode.setAttribute('style', 'display:none');
           dojo.connect(element, "onclick", function (evt) {
            if (element.checked) {
                setRequiredForElement(descriptionWidget, true);
                descriptionWidget.domNode.setAttribute('style', 'display:inline-block');
            }
            else {
                clearElement(descriptionWidget);
                setRequiredForElement(descriptionWidget, false);
                descriptionWidget.domNode.setAttribute('style', 'display:none');
            }
           });
       });
    });

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