dojo.provide("ValidationTextarea");
dojo.require("dijit.form.SimpleTextarea");
dojo.require("dijit.form.ValidationTextBox");

dojo.declare(
    "ValidationTextarea",
    [dijit.form.ValidationTextBox,dijit.form.SimpleTextarea],
    {
        invalidMessage: "This field is required",
        regExp: "(.|\\s)*",

        postCreate: function() {
            this.inherited(arguments);
        },

        validate: function() {
            this.inherited(arguments);
            if (arguments.length==0) this.validate(true);
        },

        onFocus: function() {
            if (!this.isValid()) {
                this.displayMessage(this.getErrorMessage());
            }
        },

        onBlur: function() {
            this.validate(false);
        }
     }
);

