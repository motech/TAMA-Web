(function main(){

    window.CacheControl = function(){

        if(!$.cookie('recordingKey')){
            $.cookie('recordingKey', Math.floor(Math.random() * 10000000000));
        }

        this.key = function(){
            return $.cookie('recordingKey');
        }
    }

    window.AudioFileLocation = function(fileLocation){

        var defaultLocation = "/en/";

        this.forLang = function(lang){
            return fileLocation.replace(defaultLocation,'/' + lang + '/');
        }
    };

    window.FilesResponse = function(data){
        this.data = JSON.parse(data);

        this.allFiles = function(){
            var files = [];
            for(var i in this.data.rows){
                row = this.data.rows[i];
                files.push(new AudioFileLocation(row.value));
            }
            return files;
        }
    };

    window.Recording = function(cacheControl){

        (function init(){
            $.ajax({
                 url: 'recording.jsp?command=create',
                 async:   false
            });
        })();

        this.record = function(file){
            $.ajax({
                 url: 'recording.jsp?command=rec&file=' + file + '&key=' + cacheControl.key().trim(),
                 async:   false
            });
        };

        all = function(){
            var result = {};
            var key =  cacheControl.key();
            $.ajax({
                 url: 'recording.jsp?command=all&key=' + key.trim(),
                 success: function(data){
                    result = data;
                 },
                 async:   false
            });
            return result;
        };

        this.get = function(language){
            var message = all();
            var fileResponse = new FilesResponse(message);
            var result = [];
            var allFiles = fileResponse.allFiles()

            for(var i in allFiles){
                result.push(allFiles[i].forLang(language));
            }
            return result;
        };
    };
})();

