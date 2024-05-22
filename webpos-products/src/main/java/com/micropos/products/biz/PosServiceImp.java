package com.micropos.products.biz;

import com.micropos.products.model.*;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.cache.annotation.CacheEvict;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;
import com.micropos.products.db.JD;
import com.micropos.products.jpa.CategoriesRepository;
import com.micropos.products.jpa.SettingsRepository;
@Component
public class PosServiceImp implements PosService {

    //private CartRepository cartRepository;

    private JD productRepository;
    //private ProductRepository productRepository;
    private SettingsRepository settingsRepository;
    private CategoriesRepository categoriesRepository;


    @Autowired
    public void setProductRepository(JD productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    public void setSettingsRepository(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Autowired
    public void setCategoriesRepository(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }
    /*
    @Autowired
    public void setCart(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public List<Item> getCart() {

        List<Item> cart = cartRepository.findAll();
//        if (cart.isEmpty()) {
//            //System.out.printf("NEW\n");
//            return newCart();
//        }
        return cart;
    }

    @Override
    public List<Item> newCart() {
        cartRepository.deleteAll();
        return cartRepository.findAll();
    }

    @Override
    public void checkout(Cart cart) {

    }

    @Override
    public void total(Cart cart) {

    }

    @Override
    public boolean add(Product product, int amount) {
        return false;
    }

    @Override
    public boolean add(String productId, int amount) {
        Product product = null;
        for (Product p : productRepository.findAll()) {
            if (p.getId().equals(productId)) {
                product = p;
                break;
            }
        }

        if (product == null)
            return false;
        List<Item> c = this.getCart();
        for (Item i : c) if (i.getProduct().getId() == product.getId()) return true;

        cartRepository.save(new Item(product, amount));
        //System.out.printf("%dNEW\n",this.getCart().getItems().size());
        return true;
    }

    @Override
    public void updateCartItemQuantity(long id, int quantity) {
        Optional<Item> optionalCartItem = cartRepository.findById(id);
        if (optionalCartItem.isPresent()) {
            Item cartItem = optionalCartItem.get();
            cartItem.setQuantity(quantity);
            cartRepository.save(cartItem);
        } else {
            throw new IllegalArgumentException("Cart item not found with id: " + id);
        }
    }

    @Override
    public boolean clear() {
        cartRepository.deleteAll();
        return true;
    }*/
    @Override
    @CacheEvict(value = "products")
    public void clearCache() { }

    @Override
    @CircuitBreaker(name = "products-breaker", fallbackMethod = "getDefaultProducts")
    public List<Product> products() {
        List<Product> re = productRepository.getProducts();
        /*for(Product i:re){
            try {Connection connection = Jsoup.connect(i.getImg_arr()).ignoreContentType(true);
            byte[] bytes=connection.execute().bodyAsBytes();
            }catch(Exception e){System.out.println("Image URL not found.");}
        }*/
        return re;
    }

    @Override
    public Product getProductById(String productId) {
        //Optional<Product> optionalProduct = productRepository.findById(productId);
        //return optionalProduct.orElse(null);
        return productRepository.getProduct(productId);
    }

//    @Override
//    public Product updateProduct(Product product) {
//        return productRepository.save(product);
//    }

   @Override
   public List<Settings> getSettings() {
       return settingsRepository.findAll();
   }
   @Override
   public List<Categories> getCategories() {
       return categoriesRepository.findAll();
   }

   @Override
    public void updateProduct(String productId, int quantity) {
        productRepository.updateProduct(productId, quantity);
    }

    public List<Product> getDefaultProducts(Throwable throwable) {
        System.out.printf("getDefaultProducts:%s\n",throwable.getMessage());
        List<Product> list = new ArrayList<>();
        try{
            Product p = new Product("999","999",999,"");
            list.add(p);
            return list;
        } catch (Exception e){}
        return list;
    }
}