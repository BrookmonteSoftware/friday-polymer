/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.manager.amazon;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.brookmonte.friday.FridayPolymer.manager.proxy.ProxyManager;

/**
 * @author Pete
 * 
 */
@Component
public class AmazonServiceManager
{
    @Autowired
    ProxyManager proxyManager;
    
    /*
     * Your AWS Access Key ID, as taken from the AWS Your Account page.
     */
    private static final String AWS_ACCESS_KEY_ID = "AKIAJ5E3XPGXSBQ7LCSA";

    /*
     * Your AWS Secret Key corresponding to the above ID, as taken from the AWS
     * Your Account page.
     */
    private static final String AWS_SECRET_KEY = "mVICEMZM2Ud9tmyAGfb8qtW8zS54cxzjWpbQVQzs";

    /*
     * Use one of the following end-points, according to the region you are
     * interested in:
     * 
     * US: ecs.amazonaws.com CA: ecs.amazonaws.ca UK: ecs.amazonaws.co.uk DE:
     * ecs.amazonaws.de FR: ecs.amazonaws.fr JP: ecs.amazonaws.jp
     */
    private static final String ENDPOINT = "ecs.amazonaws.com";

    public AmazonServiceManager(){}
    
    public void itemLookup(String itemId)
    {
        /*
         * Set up the signed requests helper
         */
        SignedRequestsHelper helper;
        try
        {
            helper = SignedRequestsHelper.getInstance(ENDPOINT, AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        String requestUrl = null;
        String title = null;

        /* The helper can sign requests in two forms - map form and string form */

        /*
         * Here is an example in map form, where the request parameters are
         * stored in a map.
         */
        Map<String, String> params = new HashMap<String, String>();
        params.put("Service", "AWSECommerceService");
        params.put("Version", "2011-08-01");
        params.put("Operation", "ItemLookup");
        params.put("ItemId", itemId);
        params.put("AssociateTag", "myfr08-20");
        params.put("ResponseGroup", "Small");

        requestUrl = helper.sign(params);
        System.out.println("Signed Request is \"" + requestUrl + "\"");

        title = this.fetchTitle(requestUrl);
        System.out.println("Signed Title is \"" + title + "\"");
        System.out.println();
    }
    
    public void itemSearch(String searchIndex, String searchTerms) throws
        ParserConfigurationException, SAXException, IOException, TransformerException
    {
        /*
         * Set up the signed requests helper
         */
        SignedRequestsHelper helper;
        try
        {
            helper = SignedRequestsHelper.getInstance(ENDPOINT, AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        String requestUrl = null;

        /* The helper can sign requests in two forms - map form and string form */

        Map<String, String> params = new HashMap<String, String>();
        params.put("Service", "AWSECommerceService");
        params.put("Version", "2011-08-01");
        params.put("Operation", "ItemSearch");
        params.put("Keywords", searchTerms);
        params.put("AssociateTag", "myfr08-20");
        params.put("ResponseGroup", "Large");
        params.put("Condition", "All");
        params.put("SearchIndex", searchIndex);

        requestUrl = helper.sign(params);
        System.out.println("Signed Request is \"" + requestUrl + "\"");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(requestUrl);
        
        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");        
        transformer.transform(domSource, result);
        
        System.out.println("XML IN String format is: \n" + writer.toString());
    }
    
    

    /*
     * Utility function to fetch the response from the service and extract the
     * title from the XML.
     */
    private String fetchTitle(String requestUrl)
    {
        String title = null;
        try
        {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(requestUrl);
            Node titleNode = doc.getElementsByTagName("Title").item(0);
            title = titleNode.getTextContent();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return title;
    }
}
