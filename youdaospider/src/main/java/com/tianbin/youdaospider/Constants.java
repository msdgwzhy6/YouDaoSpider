package com.tianbin.youdaospider;

/**
 * Contants
 * Created by tianbin on 2017/8/13.
 */
class Constants {

    static class User {
        static String USER_NAME = "username";
        static String PASSWORD = "password";
    }

    static class Header {
        static class Key {
            static String USER_AGENT = "User-Agent";
            static String CONTENT_TYPE = "Content-Type";
            static String CACHE_CONTROL = "Cache-Control";
            static String ACCEPT = "Accept";
            static String CONNECTION = "Connection";
        }

        static class Value {
            static String USER_AGENT = "Mozilla/5.0 (Macintosh Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36";
            static String CONTENT_TYPE = "application/x-www-form-urlencoded";
            static String CACHE_CONTROL = "no-cache";
            static String ACCEPT = "*/*";
            static String CONNECTION = "Keep-Alive";
        }
    }

    static class Request {
        static String APP = "app";
        static String TP = "tp";
        static String CF = "cf";
        static String FR = "fr";
        static String RU = "ru";
        static String PRODUCT = "product";
        static String TYPE = "type";
        static String UM = "um";
        static String SAVELOGIN = "savelogin";
    }

    static class ElementId {
        static String CARD_MAX_ID = "card_max_id";
        static String PAGINATION = "pagination";
        static String HREF = "href";
        static String WORD_LIST = "wordlist";
    }
}
