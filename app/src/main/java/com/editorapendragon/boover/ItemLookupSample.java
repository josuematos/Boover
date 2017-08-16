/**********************************************************************************************
 * Copyright 2009 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file 
 * except in compliance with the License. A copy of the License is located at
 *
 *       http://aws.amazon.com/apache2.0/
 *
 * or in the "LICENSE.txt" file accompanying this file. This file is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under the License. 
 *
 * ********************************************************************************************
 *
 *  Amazon Product Advertising API
 *  Signed Requests Sample Code
 *
 *  API Version: 2009-03-31
 *
 */

package com.editorapendragon.boover;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * This class shows how to make a simple authenticated ItemLookup call to the
 * Amazon Product Advertising API.
 * 
 * See the README.html that came with this sample for instructions on
 * configuring and running the sample.
 */
public class ItemLookupSample {
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
     * Use one of the following end-points, according to the region you are
     * interested in:
     * 
     *      US: ecs.amazonaws.com 
     *      CA: ecs.amazonaws.ca 
     *      UK: ecs.amazonaws.co.uk 
     *      DE: ecs.amazonaws.de 
     *      FR: ecs.amazonaws.fr 
     *      JP: ecs.amazonaws.jp
     * 
     */
    private static final String ENDPOINT = "webservices.amazon.com.br";

    /*
     * The Item ID to lookup. The value below was selected for the US locale.
     * You can choose a different value if this value does not work in the
     * locale of your choice.
     */
    //private static final String ITEM_ID = "B01M21462L";

    public static void main(String[] args) {
        /*
         * Set up the signed requests helper 
         */
        SignedRequestsHelper helper;
        try {
            helper = SignedRequestsHelper.getInstance(ENDPOINT, AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        ArrayList<String> vTitleBook = new ArrayList<String>();
        String requestUrl = null;
        String title = null;

        /* The helper can sign requests in two forms - map form and string form */
        
        /*
         * Here is an example in map form, where the request parameters are stored in a map.
         */
        System.out.println("Map form example:");
        Map<String, String> params = new HashMap<String, String>();
        params.put("Service", "AWSECommerceService");
        params.put("Operation", "ItemSearch");
        params.put("AssociateTag", "763967706501");
        params.put("SearchIndex", "Books");
        params.put("ResponseGroup", "Medium");
        //params.put("Author", "Josué Matos");
        params.put("Publisher", "editora pendragon");
        params.put("Title", "A Phoenix: O Legado Maytreel");
        //params.put("Keywords", "9788569782414");

        requestUrl = helper.sign(params);
        System.out.println("Signed Request is \"" + requestUrl + "\"");

        fetchTitle(requestUrl);
        //System.out.println("Signed Title is \"" + vTitleBook.toString() + "\"");
        //System.out.println();

    }


    private static void fetchTitle(String requestUrl) {

        ArrayList<String> vidBook = new ArrayList<String>();
        ArrayList<String> vTitleBook = new ArrayList<String>();
        ArrayList<String> vPhotoBook = new ArrayList<String>();
        ArrayList<String> vMessageBook = new ArrayList<String>();
        ArrayList<String> vLinkBook = new ArrayList<String>();
        ArrayList<String> vAuthorBook = new ArrayList<String>();
        ArrayList<String> gStars = new ArrayList<String>();
        ArrayList<String> gCount = new ArrayList<String>();
        NodeList titleNode, PhotoNode, MessageNode, LinkNode, idNode, authorNode;

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(requestUrl);
            authorNode = doc.getElementsByTagName("Author");
            titleNode = doc.getElementsByTagName("Title");
            PhotoNode = doc.getElementsByTagName("SmallImage");
            MessageNode = doc.getElementsByTagName("Content");
            LinkNode = doc.getElementsByTagName("URL");
            idNode = doc.getElementsByTagName("ASIN");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        int i;
        for (i = 0; i<titleNode.getLength(); i++) {
            vTitleBook.add(titleNode.item(i).getTextContent());
        }
        for (i = 0; i<PhotoNode.getLength(); i++) {
            vPhotoBook.add(PhotoNode.item(i).getTextContent());
        }

        for (i = 0; i<MessageNode.getLength(); i++) {
            vMessageBook.add(MessageNode.item(i).getTextContent());
        }

        for (i = 0; i<LinkNode.getLength(); i++) {
            vLinkBook.add(LinkNode.item(i).getTextContent());
        }
        for (i = 0; i<idNode.getLength(); i++) {
            vidBook.add(idNode.item(i).getTextContent());
        }

        for (i = 0; i<authorNode.getLength(); i++) {
            vAuthorBook.add(authorNode.item(i).getTextContent());
        }
        System.out.println("Signed Author is \"" + vAuthorBook + "\"");
        System.out.println("Signed Title is \"" + vTitleBook + "\"");
        System.out.println("Signed Asin is \"" + vidBook + "\"");
        System.out.println("Signed Link is \"" + vLinkBook + "\"");
        System.out.println("Signed Content is \"" + vMessageBook + "\"");


        //return titles;

    }
}
