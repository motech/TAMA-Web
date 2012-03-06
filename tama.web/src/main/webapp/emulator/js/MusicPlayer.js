(function main(){

    this.Queue = function(){

        var queue = [];

        this.insert = function(element){
            queue.push(element);
            return element;
        };

        this.reset = function(){
            queue = [];
        };

        this.size = function(){
            return queue.length;
        };

        this.remove = function(){
            if(queue.length === 0){
                return null;
            }
            return queue.shift();
        };
    };

    this.MusicPlayer = function(){

        var queue = new Queue();
        var state = 0;

        this.play = function(element){
            queue.insert(element);
            if(state === 0){
                state = 1;
                playNext();
            }
        };

        this.reset = function(){
          queue.reset();
          state = 0;
        };

        var playSelf = this.play;

        this.fetch = function(elementId){
            playSelf(document.getElementById(elementId));
        };

        var playNext = function(){
            var nextElement = queue.remove();
            if(nextElement){
               nextElement.play();
               $(nextElement).bind('ended',function(){
                   playNext();
               });
            }else{
                state = 0;
            }
        };
    };

})();