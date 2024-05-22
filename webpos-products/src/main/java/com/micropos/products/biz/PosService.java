package com.micropos.products.biz;

import com.micropos.products.model.*;

import java.util.List;

public interface PosService {
    public void clearCache();
    /*public List<Item> getCart();

    public List<Item> newCart();

    public void checkout(Cart cart);

    public void total(Cart cart);

    public boolean add(Product product, int amount);

    public boolean add(String productId, int amount);


    public boolean clear();*/
    public List<Product> products();

    public Product getProductById(String productId);
    //public Product updateProduct(Product product);

    public List<Settings> getSettings();
    public List<Categories> getCategories();
    public void updateProduct(String productId, int quantity);
}
