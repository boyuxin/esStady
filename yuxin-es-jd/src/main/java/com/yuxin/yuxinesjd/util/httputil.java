package com.yuxin.yuxinesjd.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author boyuxin
 * @description
 * @date 2020/7/5 14:25
 */
public class httputil {

    public static void main(String[] args) throws Exception {
        get("避孕套").forEach(System.out::println);
    }

    public static ArrayList get(String args) throws Exception {
        //
        String url = "https://search.jd.com/Search?keyword="+args;
        //返回对象
        Document parse = Jsoup.parse(new URL(url), 3000);
        Element element = parse.getElementById("J_goodsList");
        /*System.out.println(element.html());*/
        Elements li = element.getElementsByTag("li");
        ArrayList<Dto> dtos = new ArrayList<>();
        for (Element el :li) {
            String img = el.getElementsByTag("img").get(0).attr("src");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String name = el.getElementsByClass("p-name").eq(0).text();
            Dto dto = new Dto(img,price,name);
            dtos.add(dto);
        }
        return dtos;
    }


}
