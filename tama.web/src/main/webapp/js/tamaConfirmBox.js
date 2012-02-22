 dojo.declare("tama.ConfirmBox", null, {
    constructor : function(title, text, handler) {
        var self = this;
        dojo.require("dijit.form.Button");
        dojo.require("dijit.Dialog");
        var dialog = this.dialog = new dijit.Dialog({
           title: title,
           style: "",
           content: "<p>" + text + "</p> <br/><button id='confirmok'>OK</button> <button id='confirmcancel'>Cancel</button>"
        });
        this.okButton = new dijit.form.Button({label:'OK',onClick:function() {self.close(); handler();}},'confirmok');
        this.cancelButton = new dijit.form.Button({label:'Cancel',onClick:function() { self.close();}},'confirmcancel');
    },
    show : function() {
        this.dialog.show();
    },
    close : function() {
        this.dialog.hide();
        this.dialog.destroy();
        this.cancelButton.destroy();
        this.okButton.destroy();
    }
});