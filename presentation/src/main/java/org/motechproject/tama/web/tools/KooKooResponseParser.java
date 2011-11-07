package org.motechproject.tama.web.tools;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class KooKooResponseParser {
    private static XStream xStream;

    static {
        xStream = new XStream(new DomDriver());
        xStream.processAnnotations(Response.class);
    }

    public static Response fromXml(String xml) {
        try {
            return (Response) xStream.fromXML(xml);
        } catch (XStreamException exception) {
            System.out.println(xml);
            throw exception;
        }
    }
}
