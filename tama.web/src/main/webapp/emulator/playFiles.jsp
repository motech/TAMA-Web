<html>
    <head>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
        <script type="text/javascript">
            /**
            * jQuery Cookie plugin
            *
            * Copyright (c) 2010 Klaus Hartl (stilbuero.de)
            * Dual licensed under the MIT and GPL licenses:
            * http://www.opensource.org/licenses/mit-license.php
            * http://www.gnu.org/licenses/gpl.html
            *
            */
            jQuery.cookie = function (key, value, options) {

                // key and at least value given, set cookie...
                if (arguments.length > 1 && String(value) !== "[object Object]") {
                    options = jQuery.extend({}, options);

                    if (value === null || value === undefined) {
                        options.expires = -1;
                    }

                    if (typeof options.expires === 'number') {
                        var days = options.expires, t = options.expires = new Date();
                        t.setDate(t.getDate() + days);
                    }

                    value = String(value);

                    return (document.cookie = [
                        encodeURIComponent(key), '=',
                        options.raw ? value : encodeURIComponent(value),
                        options.expires ? '; expires=' + options.expires.toUTCString() : '', // use expires attribute, max-age is not supported by IE
                        options.path ? '; path=' + options.path : '',
                        options.domain ? '; domain=' + options.domain : '',
                        options.secure ? '; secure' : ''
                    ].join(''));
                }

                // key and possibly options given, get cookie...
                options = value || {};
                var result, decode = options.raw ? function (s) { return s; } : decodeURIComponent;
                return (result = new RegExp('(?:^|; )' + encodeURIComponent(key) + '=([^;]*)').exec(document.cookie)) ? decode(result[1]) : null;
            };
        </script>

        <script src="js/recording.js"></script>
        <script type="text/javascript">
            $(document).ready(function(){
                window.cacheControl = new window.CacheControl();
                window.recording = new Recording(window.cacheControl);
                var audioDiv = $('#audios');
                var lang = $('#languages');
                lang.click(function(event){
                   var fileNames = recording.get(lang.val());
                   var html = "<div>";
                   for(var i in fileNames){
                       var filename = fileNames[i];
                       var text = filename;//.replace(/.wav/,"");
                       html += '<audio src="' + filename + '" autostart=false width=1 height=1 id="'+filename+'" enablejavascript="true" class="audio"/>' +
                       '<button id="' + filename+ '" onclick="play(\'' +filename+ '\');">'+text+' </button>';
                       html += '<source src="' + filename+ '" type="audio/wave" />';
                   }
                   html += "</div>"
                   audioDiv.html(html);
                });
            });
        </script>
    </head>
    <body>
        <label>Select Language :</label>&nbsp;<select id="languages">
            <option value="en">English</option>
            <option value="mr">Marathi</option>
            <option value="hi">Hindi</option>
            <option value="ta">Tamil</option>
        </select>
        <div id="audios"></div>
    </body>
</html>