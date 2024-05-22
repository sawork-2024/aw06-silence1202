package com.micropos.products.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long ID;
    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.REMOVE})
    private Product product;
    private int quantity;
    //@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER    )
    //@JoinColumn(name = "cart_id")
    //private Cart cart;

    public Item(){}
    public Item(Product product,int quantity){this.product=product;this.quantity=quantity;}
    public void plus() {quantity++;}
    public void minus() {quantity--;}
    @Override
    public String toString() {
        return product.toString() + "\t" + quantity;
    }
}
