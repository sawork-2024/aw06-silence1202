package com.micropos.products.db;
import com.micropos.products.jpa.ProductRepository;
import com.micropos.products.model.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.cache.annotation.Cacheable;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Connection;
import java.io.FileOutputStream;


@Repository
public class JD implements PosDB {
    @Autowired
    private ProductRepository productRepository;
    private boolean init = false;

    public HashMap<String, String> convertCookie(String cookie) {
        HashMap<String, String> cookiesMap = new HashMap<String, String>();
        String[] items = cookie.trim().split(";");
        for (String item:items) cookiesMap.put(item.split("=")[0], item.split("=")[1]);
        return cookiesMap;
    }

    @Override
    @Cacheable(value = "products")
    public List<Product> getProducts() {
        try {
            if (!init) {
                parseJD("%E7%83%B9%E9%A5%AA");
                init = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return productRepository.findAll();
    }

    @Override
    public Product getProduct(String productId) {
        for (Product p : getProducts()) {
            if (p.getId().equals(productId)) {
                return p;
            }
        }
        return null;
    }
    private static String downloadImage(String url) throws Exception {
        Connection connection = Jsoup.connect(url).ignoreContentType(true);
        byte[] bytes=connection.execute().bodyAsBytes();
        int lastIndexOfSlash = url.lastIndexOf("/") + 1;
        String fileName = url.substring(lastIndexOfSlash);
        FileOutputStream outputStream = new FileOutputStream("../client/public/images/"+fileName);
        outputStream.write(bytes);
        outputStream.close();
        //System.out.println("已下载图片：" + fileName);
        return fileName;
    }

    public void parseJD(String keyword) throws IOException {
        //获取请求https://zh.stardewvalleywiki.com/%E7%83%B9%E9%A5%AA
        String url = "https://zh.stardewvalleywiki.com/" + keyword;
        //解析网页

        Document document = Jsoup.connect(url).cookie("_ga","GA1.1.1559461324.1714040524").cookie("_ga_1S37VCXXZ9","GS1.1.1716381083.4.1.1716384311.0.0.0").get();
        //所有js的方法都能用
        //System.out.println(document);
        Element element = document.select("table.wikitable.sortable").first();
        //获取所有li标签
        Elements rows = element.select("tr");
        int i=0;



        for (Element row : rows) { 
            i++;if(i==1)continue;
            if(i>19) break;
            //System.out.println(row);
            String id = String.valueOf(i);
            Elements cells = row.select("td"); 
            if(cells == null || cells.size()<8){i--;continue;}
            Elements imgElements = cells.first().select("img"); 
            String img=null;
            String img_arr=null;
            //byte[] bytes = new byte[1000];
            if (!imgElements.isEmpty()) {
                img_arr = imgElements.first().attr("src");
                if (!img_arr.isEmpty()) {
                    //try {img = downloadImage(img_arr);}catch(Exception e){System.out.printf("Image URL download failed.%s\n",img_arr);}
                    int lastIndexOfSlash = img_arr.lastIndexOf("/") + 1;
                    img = img_arr.substring(lastIndexOfSlash);
                }
                //System.out.println("Image URL: " + img);
            } else {
                System.out.println("Image URL not found.");
            }
            String title = cells.get(1).select("a").eq(0).attr("title");
            String price = cells.last().attr("data-sort-value");

            Product product = new Product(id, title, Double.parseDouble(price), img);//, img_arr);

            productRepository.save(product);
        }
        
    }
    @Override
    public void updateProduct(String productId, int quantity) {
        productRepository.updateProductQuantity(productId, quantity);
    }

}
