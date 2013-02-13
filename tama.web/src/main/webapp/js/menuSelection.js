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
                var currentLength = self.lengthOfMatch(document.location.href, dojo.attr(links[i],"href"));
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
        var index = source.indexOf(pattern);
        if(index >= 0){
            return index + pattern.length;
        }else{
            return -1;
        }
    }
}