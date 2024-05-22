package com.micropos.products.db;

import com.micropos.products.model.Product;

import java.util.List;

public interface PosDB {

    public List<Product> getProducts();

    public Product getProduct(String productId);

    void updateProduct(String productId, int quantity);
}
