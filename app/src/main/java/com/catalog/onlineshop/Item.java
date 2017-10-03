package com.catalog.onlineshop;

/**
 * Created by chiru on 15/9/16.
 */
public class Item {


    public String description,price,imageurl,title,quantity,seller_id;

    public Item(){

    }

    public Item(String description, String price, String imageurl, String title, String quantity, String seller_id){

        this.description = description;
        this.price = price;
        this.imageurl = imageurl;
        this.title = title;
        this.quantity = quantity;
        this.seller_id = seller_id;
    }

    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description = description;
    }

    public String getPrice(){
        return price;
    }
    public void setPrice(String price){
        this.price = price;
    }

    public String getImageurl(){
        return imageurl;
    }
    public void setImageurl(String imageurl){
        this.imageurl = imageurl;
    }

    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }

    public String getSeller_id() { return seller_id; }
    public void setSeller_id(String seller_id) { this.seller_id = seller_id; }
}
