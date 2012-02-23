dojo.declare("tama.ConfirmBox", null, {
        constructor : function(title, text, handler) {
            var self = this;
            dojo.require("dijit.form.Button");
            dojo.require("dijit.Dialog");
            var dialog = this.dialog = new dijit.Dialog({
               title: title,
               style: "",
               content: "<p>" + text + "</p> <br/><button id='confirmok'>OK</button> <button id='confirmcancel'>Cancel</button>",
               onClose : function () {alert('close');}
            });
            var okButton = new dijit.form.Button({label:'OK',onClick:function() {dialog.hide(); handler();}},'confirmok');
            var cancelButton = new dijit.form.Button({label:'Cancel',onClick:function() { dialog.hide();}},'confirmcancel');
            var close = function() {
                        dialog.destroy();
                        cancelButton.destroy();
                        okButton.destroy();
                    }
            dojo.connect(dialog,'hide', close);
        },
        show : function() {
            this.dialog.show();
        }

    });