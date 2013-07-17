    function validateMobileNumberUniqueness() {
        // get the form values

        var mobileNumber = dijit.byId('_mobilePhoneNumber_id').value;
        var codeExecuted = false;

        if(codeExecuted == false)
        {

        AjaxCall.get({
        url: "${validateMobileNumber}",
        content: {mobileNumber:mobileNumber},
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        sync: true,
        load: handleResults

        });
        }
        }

         function handleResults(response,ioArgs)
         {
               if(response.status == "FAIL")
               {
               if(Spring.validateAll()){
               codeExecuted=true;
               alert("Warning !   The phone number entered for the patient is already in use");
               }

               }
               else
               {
               console.log("PASSED");
               }
         }
