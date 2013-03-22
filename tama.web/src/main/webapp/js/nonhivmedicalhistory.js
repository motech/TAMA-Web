dojo.require("dojo.NodeList-traverse");

dojo.addOnLoad(function() {

    var displayDescriptionIfAllergySelected = function (index) {
    return function() {
        var allergyCheckbox = dojo.byId('c_org_motechproject_tama_domain_Patient_allergy' + index);
        if(allergyCheckbox == null)
            return;

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
          descriptionWidget = dijit.byId("_medicalHistory.nonHivMedicalHistory.questions["+ index +"].comments_id");
          if(!element.checked) {
            descriptionWidget.domNode.parentNode.setAttribute('style', 'display:none');
          }
           dojo.connect(element, "onclick", function (evt) {
            if (element.checked) {
                setRequiredForElement(descriptionWidget, true);
                descriptionWidget.domNode.parentNode.setAttribute('style', 'display:inline-block');
            }
            else {
                clearElement(descriptionWidget);
                setRequiredForElement(descriptionWidget, false);
                descriptionWidget.domNode.parentNode.setAttribute('style', 'display:none');
            }
           });
       });
    });
  
   dojo.forEach(dojo.query(".has_description"), function(element, index) {
      dojo.connect(element, "onclick", function(event) {
        var noneOption = dojo.hasClass(element, "NONE");
      	var otherTextBox = dojo.query(element).parent().parent().query("input[type='text']")[0];
      	otherTextBox.disabled = noneOption;
      	if (noneOption) otherTextBox.value='';
      });
   });
   
   dojo.forEach(dojo.query("input.has_description:not([value='NONE']):checked"), function(element, index) {
   		element.disabled=false;
   		dojo.query(element).parent().parent().query("input[type='text']")[0].disabled = false;
   });
});