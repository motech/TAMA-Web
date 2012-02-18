<%@ page import="java.io.*" %>

<%@ page import="java.net.*"%>

<%!

     public HttpURLConnection httpCon(URL url, String method, String contentType) throws Exception {
         HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
         httpCon.setDoOutput(true);
	     httpCon.setRequestMethod(method);
	     if(contentType != null){
	        httpCon.setRequestProperty("Content-Type", contentType );
	     }
	     return httpCon;
     }

     public String defaultDesignDocument(){
        return "{ \"_id\" : \"_design/record\", \"views\" : { \"all\" : { \"map\" : \"function(doc){ emit(doc.key, doc.file)}\" } } }";
     }

     public BufferedReader makeGetRequest(String path) throws Exception {
         URL url = new URL(path);
         InputStream allDbs = url.openStream();
         BufferedReader reader = new BufferedReader(new InputStreamReader(allDbs));
         return reader;
     }

     public int createDatabase(URL url) throws Exception {
         HttpURLConnection httpCon = httpCon(url, "PUT", null);
         httpCon.getResponseCode();
         httpCon.disconnect();
         return createData(url, defaultDesignDocument());
     }

     public int createData(URL url, String data) throws Exception {
        HttpURLConnection httpCon = httpCon(url, "POST", "application/json");

	    OutputStreamWriter fileWritter = new OutputStreamWriter(httpCon.getOutputStream());
        fileWritter.write(data);
        fileWritter.close();

        int response = httpCon.getResponseCode();
        httpCon.disconnect();
        return response;
     }
%>

<%
    String databaseName = "http://localhost:5984/recording/";
    URL url = new URL(databaseName);

    if("create".equals(request.getParameter("command"))){
        createDatabase(url);
    }else if("rec".equals(request.getParameter("command"))){
        String file = request.getParameter("file").toString();
        String key = request.getParameter("key").toString();

        createData(url , "{\"file\" : \""+ file +"\", \"key\" : \"" + key + "\" }");
    }else if("all".equals(request.getParameter("command"))){
        String key = request.getParameter("key").toString();

        BufferedReader reader = makeGetRequest(databaseName + "_design/record/_view/all?key=\"" + key +"\"");
        for (String line; (line = reader.readLine()) != null;) {
	        out.print(line);
	    }
    }
%>