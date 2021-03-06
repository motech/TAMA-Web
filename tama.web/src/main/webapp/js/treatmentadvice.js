dojo.require("dojo.data.ItemFileWriteStore");

dojo.addOnLoad(function() {
    var regimens_data;
    var regimens_data_hash = {};
    var groups_data_hash = {};
    var compositions_data_hash = {};

    var regimens = dijit.byId('_treatmentAdvice.regimenId_id');
    var composition_groups = dijit.byId('_treatmentAdvice.drugCompositionGroupId_id');
    var compositions = dijit.byId('_treatmentAdvice.drugCompositionId_id');

    var drug2_name = dojo.byId('_c_org_motechproject_tama_domain_TreatmentAdvice_drugName1_drugName_id');

    var init = function() {
        var options = new dojo.data.ItemFileWriteStore({data: {identifier: "id", label: "name", items: []}});
        options.newItem({name: null, id: null });
        dojo.forEach(regimens_data, function(regimen, i) {
            options.newItem({name: regimen.displayName, id: regimen._id });
        });
        regimens.set('store', options);
        regimens.setDisplayedValue("");
        regimens.setValue(null);

        dojo.connect(regimens, "onChange", function(regimen_id) {
            var options = new dojo.data.ItemFileWriteStore({data: {identifier: "id", label: "name", items: []}});
            var valueToSet = "";
            if(!regimen_id){
                options.newItem({name: null, id: null});
            }
            else{
                dojo.forEach(regimens_data_hash[regimen_id].drugCompositionGroups, function(group, i) {
                    options.newItem({name: group.name, id: group._id });
                });
                valueToSet = regimens_data_hash[regimen_id].drugCompositionGroups[0]._id;
            }
            composition_groups.attr('store', options);
            composition_groups.setValue(valueToSet);
        });

        dojo.connect(composition_groups, "onChange", function(group_id) {
            var regimen_index = regimens.attr("value");
            var options = new dojo.data.ItemFileWriteStore({data: {identifier: "id", label: "name", items: []}});

            var valueToSet = "";
            if(!group_id){
                options.newItem({name: null, id: null});
            }
            else {
                dojo.forEach(groups_data_hash[group_id].drugCompositions, function(composition, i) {
                    options.newItem({name: composition.displayName, id: composition.id });
                });
                valueToSet = groups_data_hash[group_id].drugCompositions[0].id;
            }
            compositions.attr('store', options);
            compositions.setValue(valueToSet);
        });


        var drug_brands = [dijit.byId('_treatmentAdvice.drugDosages[0].brandId_id'), dijit.byId('_treatmentAdvice.drugDosages[1].brandId_id')];

        var secondDosageWidgets = dijit.registry.filter(function(w) {
            return w.id.indexOf("_treatmentAdvice.drugDosages[1]") !== -1;
        });

        dojo.connect(compositions, "onChange", function(composition_id) {
            var selected_drug = null;
            var other_drug = null;

            if(composition_id){
                dojo.forEach(compositions_data_hash[composition_id].drugs, function(drug, i) {
                    if (compositions_data_hash[composition_id].displayName === drug.name)
                        selected_drug = drug;
                    else
                        other_drug = drug;
                });
            }

            var composition_drugs = dojo.filter([selected_drug, other_drug], function(drug) {
                return drug != null;
            });

            var drugIds = [dojo.byId("drugDosages[0].drugId"), dojo.byId("drugDosages[1].drugId")];
            dojo.forEach(composition_drugs, function(drug, i) {
                drugIds[i].value = drug._id;
            });

            if (composition_drugs.length > 1) {
                drug2_name.innerHTML = other_drug.name;
            }
            var nonRequiredWidgets = ["_treatmentAdvice.drugDosages[1].advice_id"];

            _changeFirstDosage();

            secondDosageWidgets.forEach(function(widget, i) {

                if ((!composition_id) || compositions_data_hash[composition_id].drugs.length == 1) {
                    widget.set("disabled", true);
                    widget.set("required", false);
                    dojo.query(".dosage:last-child").style("display", "none");
                    dojo.forEach(dojo.query(".dosage:last-child input"), function(element, i) {
                        element.disabled = true;
                    });
                } else {
                    widget.set("disabled", false);
                    if (dojo.indexOf(nonRequiredWidgets, widget.id) === -1)
                        widget.set("required", true);
                    dojo.query(".dosage:last-child").style("display", "block");
                    dojo.forEach(dojo.query(".dosage:last-child input"), function(element, i) {
                        element.disabled = false;
                    });
                    _changeSecondDosage();

                }
            });

        });

        var changeDosageType = function(dosage) {
            return function() {
                var morning_label = dojo.byId('_c_org_motechproject_tama_domain_TreatmentAdvice_dosageSchedules' + dosage + '0_id');
                var morning_time = dijit.byId('_treatmentAdvice.drugDosages[' + dosage + '].morningTime_id');
                var evening_label = dojo.byId('_c_org_motechproject_tama_domain_TreatmentAdvice_dosageSchedules' + dosage + '1_id');
                var evening_time = dijit.byId('_treatmentAdvice.drugDosages[' + dosage + '].eveningTime_id');

                morning_time.setValue('');
                evening_time.setValue('');

                var showMorningTime = function() {
                    morning_time.set('disabled', false);
                    morning_time.set('required', true);
                    morning_label.style.display = 'block';
                };
                var showEveningTime = function() {
                    evening_time.set('disabled', false);
                    evening_time.set('required', true);
                    evening_label.style.display = 'block';
                };
                var hideMorningTime = function() {
                    morning_time.set('disabled', true);
                    morning_time.set('required', false);
                    morning_label.style.display = 'none';
                    morning_time.set('value', '');
                };
                var hideEveningTime = function() {
                    evening_time.set('disabled', true);
                    evening_time.set('required', false);
                    evening_label.style.display = 'none';
                    evening_time.set('value', '');
                };

                var schedule = dijit.byId('_treatmentAdvice.drugDosages[' + dosage + '].dosageTypeId_id');
                if (schedule._lastDisplayedValue === 'Morning Daily') {
                    showMorningTime();
                    hideEveningTime();
                } else if (schedule._lastDisplayedValue === 'Evening Daily') {
                    showEveningTime();
                    hideMorningTime();
                } else {
                    showMorningTime();
                    showEveningTime();
                }

                showOffSet(dosage);
            };
        };

        var showOffSet = function(dosage) {
            var schedule = dijit.byId('_treatmentAdvice.drugDosages[' + dosage + '].dosageTypeId_id');
            var id = "_c_org_motechproject_tama_domain_TreatmentAdvice_dosageSchedules_" + dosage + "_offsetDays_id";
            dojo.byId(id).style.display = schedule._lastDisplayedValue === 'Variable Dosage' ? 'block' : 'none';
            dijit.byId("_treatmentAdvice.drugDosages[" + dosage + "].offsetDays_id").set('required', schedule._lastDisplayedValue === 'Variable Dosage');
        }

        var _changeFirstDosage = changeDosageType(0);
        var _changeSecondDosage = changeDosageType(1);

        dojo.connect(dijit.byId('_treatmentAdvice.drugDosages[0].dosageTypeId_id'), 'onChange', _changeFirstDosage);
        dojo.connect(dijit.byId('_treatmentAdvice.drugDosages[1].dosageTypeId_id'), 'onChange', _changeSecondDosage);

        var validateDosageTimes = function() {
            var drug1AmTime = dijit.byId("_treatmentAdvice.drugDosages[0].morningTime_id");
            var drug1PmTime = dijit.byId("_treatmentAdvice.drugDosages[0].eveningTime_id");
            var drug2AmTime = dijit.byId("_treatmentAdvice.drugDosages[1].morningTime_id");
            var drug2PmTime = dijit.byId("_treatmentAdvice.drugDosages[1].eveningTime_id");
            var amTimesArray = dojo.filter([drug1AmTime, drug2AmTime], function(element) {
                return element.required;
            });
            var pmTimesArray = dojo.filter([drug1PmTime, drug2PmTime], function(element) {
                return element.required;
            });

            var amTimes = {}, pmTimes = {};
            dojo.forEach(amTimesArray, function(item) {
                if (item != null && item != undefined && item.value !== "")
                    amTimes[item.value] = "dummy";
            });
            dojo.forEach(pmTimesArray, function(item) {
                if (item != null && item != undefined && item.value !== "")
                    pmTimes[item.value] = "dummy";
            });

            var getProperties = function(object) {
                var result = [];
                for (key in object)
                    result.push(key);
                return result;
            };

            var conditions = [
                {
                    execute: function() {
                        return getProperties(amTimes).length > 1 && getProperties(pmTimes).length > 1;
                    },
                    message : "You have entered different times for morning and evening doses. Please change the times to the same time for both dosages."
                },
                {
                    execute: function() {
                        return getProperties(amTimes).length > 1;
                    },
                    message : "You have entered different times for morning dose. Please change the times to the same time for both morning dosages."
                },
                {
                    execute: function() {
                        return getProperties(pmTimes).length > 1;
                    },
                    message : "You have entered different times for evening dose. Please change the times to the same time for both evening dosages."
                }
            ];

            for (var i = 0; i < conditions.length; i++) {
                if (conditions[i].execute())
                    return conditions[i].message;
            }
            return null;
        };

        dojo.connect(dojo.byId("treatmentAdvice"), "onsubmit", function(evt) {
            var validation_error = validateDosageTimes();
            if (validation_error != null) {
                var error = dojo.byId("dosage_times_error");
                dojo.html.set(error, validation_error);
                evt.preventDefault();
            }
        });
    }

    var build_regimens_hash = function() {
        for (var i = 0; i < regimens_data.length; i++) {
            regimens_data_hash[regimens_data[i]._id] = regimens_data[i];
            for (var j = 0; j < regimens_data[i].drugCompositionGroups.length; j++) {
                groups_data_hash[regimens_data[i].drugCompositionGroups[j]._id] = regimens_data[i].drugCompositionGroups[j];
                for (var k = 0; k < regimens_data[i].drugCompositionGroups[j].drugCompositions.length; k++) {
                    compositions_data_hash[regimens_data[i].drugCompositionGroups[j].drugCompositions[k].id] = regimens_data[i].drugCompositionGroups[j].drugCompositions[k];
                }
            }
        }
    };

    var requestURL = document.URL.indexOf('treatmentadvices') > 0 ? 'regimens' : 'treatmentadvices/regimens';
    AjaxCall.get({
        url: requestURL,
        load: function(result) {
            regimens_data = result;
            build_regimens_hash();
            init();
        }
    });
});
