package com.ben.springbootmall.service;

import com.ben.springbootmall.dto.ProductRequest;
import com.ben.springbootmall.model.Product;

public interface  ProductService {

    Product getProductById(Integer productId);

    Integer createProduct(ProductRequest productRequest);

    void updateProduct(Integer productId, ProductRequest productRequest);

    void deleteProductById(Integer productId);
}
