dojo.addOnLoad(function() {

    var validateLabResults = function() {
        var labResult1TestDate = dojo.byId("_labResultsUIModel.labResults[0].testDateAsDate_id");
        var labResult2TestDate = dojo.byId("_labResultsUIModel.labResults[1].testDateAsDate_id");
        var labResult1TestResult = dojo.byId("_labResultsUIModel.labResults[0].result_id");
        var labResult2TestResult = dojo.byId("_labResultsUIModel.labResults[1].result_id");

        var elementIsFilled = function(item) {
            return(item != null && item != undefined && item.value !== "");
        }

        var conditions = [
            {
                execute: function() {
                    return (elementIsFilled(labResult1TestDate) && !elementIsFilled(labResult1TestResult)) || (!elementIsFilled(labResult1TestDate) && elementIsFilled(labResult1TestResult));
                },
                message : "You must enter both test date and test result"
            },
            {
                execute: function() {
                    return (elementIsFilled(labResult2TestDate) && !elementIsFilled(labResult2TestResult)) || (!elementIsFilled(labResult2TestDate) && elementIsFilled(labResult2TestResult));
                },
                message : "You must enter both test date and test result"
            }

        ];

        for (var i = 0; i < conditions.length; i++){
            if (conditions[i].execute())
                return conditions[i].message;
        }
        return null;
     };

    dojo.connect(dojo.byId("clinicvisit"), "onsubmit", function(evt) {
        var validation_error = validateLabResults();
        if (validation_error != null) {
            var error = dojo.byId("lab_results_error");
            dojo.html.set(error, validation_error);
            evt.preventDefault();
        }
    });
});
