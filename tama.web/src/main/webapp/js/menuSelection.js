var MenuSelection = function(element){
    this.menuElement = element;
    this.init();
}

MenuSelection.prototype = {
    init: function(){
        var self = this;
        dojo.addOnLoad(function(){
           var links = dojo.query("a", self.menuElement);
           var longestMatch = 0;
           var longestMatchIndex = -1;
           for(i = 0;i<links.length;i++){
                var currentLength = self.lengthOfMatch(document.location.pathname, dojo.attr(links[i],"href"));
                if(currentLength > longestMatch){
                    longestMatch = currentLength;
                    longestMatchIndex = i;
                }
           }
           if(longestMatchIndex >= 0){
                dojo.addClass(links[longestMatchIndex], "selected");
           }
        });
    },

    lengthOfMatch: function(source, pattern){
        var endIndex = pattern.indexOf("?") > 0 ? pattern.indexOf("?") : pattern.length;
        pattern = pattern.substring(0, endIndex);
        var index = source.indexOf(pattern);
        if(index >= 0){
            return index + pattern.length;
        }else{
            return -1;
        }
    }
}