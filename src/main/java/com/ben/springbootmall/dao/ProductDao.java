package com.ben.springbootmall.dao;

import com.ben.springbootmall.dto.ProductRequest;
import com.ben.springbootmall.model.Product;

public interface ProductDao {

    Product getProductById(Integer productId);

    Integer createProduct(ProductRequest productRequest);

    void  updateProduct(Integer productId, ProductRequest productRequest);
}
