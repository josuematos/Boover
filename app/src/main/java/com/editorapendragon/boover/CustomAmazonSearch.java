package com.editorapendragon.boover;

/**
 * Created by Josue on 18/03/2017.
 */

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;


public class CustomAmazonSearch {

    /*
  * Your AWS Access Key ID, as taken from the AWS Your Account page.
  */
    private static final String AWS_ACCESS_KEY_ID = "AKIAJOZ36CXGBVERFQOA";

    /*
     * Your AWS Secret Key corresponding to the above ID, as taken from the AWS
     * Your Account page.
     */
    private static final String AWS_SECRET_KEY = "jM7H51umM12M4k4THyLaOMcos03ON8jyOi79bKpH";

    /*
     * Use the end-point according to the region you are interested in.
     */
    private static final String ENDPOINT = "webservices.amazon.com.br";

    public static void main(String[] args) {

        /*
         * Set up the signed requests helper.
         */
        SignedRequestsHelper helper;

        try {
            helper = SignedRequestsHelper.getInstance(ENDPOINT, AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        String requestUrl = null;

        Map<String, String> params = new HashMap<String, String>();

        params.put("Service", "AWSECommerceService");
        params.put("Operation", "ItemSearch");
        params.put("AWSAccessKeyId", "AKIAJOZ36CXGBVERFQOA");
        params.put("AssociateTag", "763967706501");
        params.put("SearchIndex", "Books");
        params.put("ResponseGroup", "EditorialReview,Images,ItemAttributes,Offers,SalesRank");
        params.put("Author", "Josué Matos");
        params.put("Publisher", "Editora PenDragon");
        params.put("Title", "Eu, Inabalável");
        params.put("Keywords", "9788569782414");

        requestUrl = helper.sign(params);

        System.out.println("Signed URL: \"" + requestUrl + "\"");
    }
    private static String fetchTitle(String requestUrl) {
        String title = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(requestUrl);
            //Log.e("DOC",doc.toString());
            Node titleNode = doc.getElementsByTagName("Author").item(0);
            title = titleNode.getTextContent();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return title;
    }

}
