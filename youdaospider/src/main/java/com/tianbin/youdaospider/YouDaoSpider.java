package com.tianbin.youdaospider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tianbin.youdaospider.model.Wordbook;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * YouDaoSpider
 * Created by tianbin on 2017/8/13.
 */
public class YouDaoSpider {

    private HashMap<String, String> mHeaderMap;
    private HashMap<String, String> mRequestDataMap;

    public YouDaoSpider() {
        mHeaderMap = new HashMap<>();
        mHeaderMap.put(Constants.Header.Key.USER_AGENT, Constants.Header.Value.USER_AGENT);
        mHeaderMap.put(Constants.Header.Key.CONTENT_TYPE, Constants.Header.Value.CONTENT_TYPE);
        mHeaderMap.put(Constants.Header.Key.CACHE_CONTROL, Constants.Header.Value.CACHE_CONTROL);
        mHeaderMap.put(Constants.Header.Key.ACCEPT, Constants.Header.Value.ACCEPT);
        mHeaderMap.put(Constants.Header.Key.CONNECTION, Constants.Header.Value.CONNECTION);

        mRequestDataMap = new HashMap<>();
        mRequestDataMap.put(Constants.Request.APP, "web");
        mRequestDataMap.put(Constants.Request.TP, "urstoken");
        mRequestDataMap.put(Constants.Request.CF, "7");
        mRequestDataMap.put(Constants.Request.FR, "1");
        mRequestDataMap.put(Constants.Request.RU, "http://dict.youdao.com");
        mRequestDataMap.put(Constants.Request.PRODUCT, "DICT");
        mRequestDataMap.put(Constants.Request.TYPE, "1");
        mRequestDataMap.put(Constants.Request.UM, "true");
        mRequestDataMap.put(Constants.Request.SAVELOGIN, "1");
    }

    public String fetchCookie(String username, String passwordEncryptByMD5) throws Exception {
        Connection.Response loginResponse = Jsoup.connect(YouDaoURL.LOGIN_URL)
                .headers(mHeaderMap)
                .method(Connection.Method.POST)
                .data(mRequestDataMap)
                .data(Constants.User.USER_NAME, username)
                .data(Constants.User.PASSWORD, passwordEncryptByMD5)
                .execute();

        Map<String, String> cookies = loginResponse.cookies();
        if (loginResponse.statusCode() != 200 || cookies.size() != 6) {
            return null;
        }

        HashMap<String, String> cookieMap = new HashMap<>();
        for (String key : cookies.keySet()) {
            cookieMap.put(key, cookies.get(key));
        }

        return new GsonBuilder().create().toJson(cookieMap);
    }

    public int fetchWordCount(String cookie) throws IOException {
        Document document = Jsoup.connect(YouDaoURL.WORD_LIST_URL)
                .headers(mHeaderMap)
                .cookies(getCookieMap(cookie))
                .get();

        return getWordCount(document);
    }

    public Wordbook fetchWords(String cookie) throws IOException {
        Wordbook youDaoWordbook = new Wordbook();

        Map<String, String> cookieMap = getCookieMap(cookie);
        Document document = Jsoup.connect(YouDaoURL.WORD_LIST_URL)
                .headers(mHeaderMap)
                .cookies(cookieMap)
                .get();

        youDaoWordbook.wordCount = getWordCount(document);
        youDaoWordbook.pageCount = getPageCount(document);

        parseWords(youDaoWordbook.pageCount, youDaoWordbook, cookieMap);
        return youDaoWordbook;
    }

    private Map<String, String> getCookieMap(String cookieStr) {
        return new Gson().fromJson(
                cookieStr, new TypeToken<HashMap<String, String>>() {
                }.getType()
        );
    }

    private int getWordCount(Document document) {
        Element wordCountElement = document.getElementById(Constants.ElementId.CARD_MAX_ID);
        wordCountElement.childNodes();
        return Integer.parseInt(wordCountElement.childNodes().get(0).toString());
    }

    private int getPageCount(Document document) {
        int maxPageNum = 0;
        Element pageCountElement = document.getElementById(Constants.ElementId.PAGINATION);
        if (pageCountElement == null) {
            return 1;
        }
        for (Node childNode : pageCountElement.childNodes()) {
            String hrefValue = childNode.attributes().get(Constants.ElementId.HREF);
            if (hrefValue.isEmpty()) {
                continue;
            }
            String substring = hrefValue.substring(11);
            int pageNum = Integer.parseInt(substring.split("&")[0]);
            if (maxPageNum < pageNum) {
                maxPageNum = pageNum;
            }
        }
        return maxPageNum;
    }

    private void parseWords(int pageCount, Wordbook wordbook, Map<String, String> cookieMap) throws IOException {
        ArrayList<String> words = new ArrayList<>();
        for (int i = 0; i < pageCount; i++) {
            Element wordListElement = getNextPageWords(i, cookieMap).getElementById(Constants.ElementId.WORD_LIST);
            Elements wordElements = wordListElement.children().get(0).children().get(0).children();

            for (Element wordElement : wordElements) {
                words.add(wordElement.text().split("\\s+")[1]);
            }
        }

        wordbook.wordList = words;
    }

    private Document getNextPageWords(int pageNum, Map<String, String> cookieMap) throws IOException {
        return Jsoup.connect(String.format(Locale.CHINA, YouDaoURL.WORD_LIST_PAGE_URL, pageNum))
                .headers(mHeaderMap)
                .cookies(cookieMap)
                .get();
    }

}
