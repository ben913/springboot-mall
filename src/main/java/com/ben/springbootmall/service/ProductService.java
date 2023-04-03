package com.ben.springbootmall.service;

import com.ben.springbootmall.constant.ProductCategory;
import com.ben.springbootmall.dto.ProductRequest;
import com.ben.springbootmall.model.Product;

import java.util.List;

public interface  ProductService {

    List<Product> getProducts(ProductCategory category, String search);

    Product getProductById(Integer productId);

    Integer createProduct(ProductRequest productRequest);

    void updateProduct(Integer productId, ProductRequest productRequest);

    void deleteProductById(Integer productId);


}
