package org.motechproject.tamafunctional.framework;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.motechproject.tamafunctional.testdata.ivrreponse.Hangup;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;

public class KooKooResponseParser extends FunctionalTestObject {
    private static XStream xStream;

    static {
        xStream = new XStream(new DomDriver());
        xStream.processAnnotations(IVRResponse.class);
        xStream.alias("hangup", Hangup.class);
    }

    private IVRResponse fromXmlInternal(String xml) {
        try {
            return (IVRResponse) xStream.fromXML(xml);
        } catch (XStreamException exception) {
            logInfo(xml);
            throw exception;
        }
    }

    public static IVRResponse fromXml(String xml) {
        return new KooKooResponseParser().fromXmlInternal(xml);
    }

    public static String fromObject(IVRResponse ivrResponse) {
        return new KooKooResponseParser().toXMLInternal(ivrResponse);
    }

    private String toXMLInternal(IVRResponse ivrResponse) {
        return xStream.toXML(ivrResponse);
    }
}
