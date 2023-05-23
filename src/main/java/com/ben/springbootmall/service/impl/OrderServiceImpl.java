package com.ben.springbootmall.service.impl;

import com.ben.springbootmall.dao.OrderDao;
import com.ben.springbootmall.dao.ProductDao;
import com.ben.springbootmall.dao.UserDao;
import com.ben.springbootmall.dto.BuyItem;
import com.ben.springbootmall.dto.CreateOrderRequest;
import com.ben.springbootmall.dto.OrderQueryParams;
import com.ben.springbootmall.model.Order;
import com.ben.springbootmall.model.OrderItem;
import com.ben.springbootmall.model.Product;
import com.ben.springbootmall.model.User;
import com.ben.springbootmall.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.server.ResponseStatusException;



@Component
public class OrderServiceImpl implements OrderService {

    private final static Logger log =  LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private UserDao userDao;


    @Override
    public Integer countOrder(OrderQueryParams orderQueryParams) {
        return orderDao.countOrder(orderQueryParams);
    }

    @Override
    public List<Order> getOrders(OrderQueryParams orderQueryParams) {
        List<Order> orderList = orderDao.getOrders(orderQueryParams);

        for(Order order : orderList){
            List<OrderItem> orderItemList = orderDao.getOrderItemsByOrderId(order.getOrderId());

            order.setOrderItemList(orderItemList);
        }

        return orderList;
    }

    @Override
    public Order getOrderById(Integer orderId) {
        Order order = orderDao.getOrderById(orderId);

        List<OrderItem> orderItemList = orderDao.getOrderItemsByOrderId(orderId);

        order.setOrderItemList(orderItemList);

        return order;
    }

    @Transactional
    @Override
    public Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest) {
        //check user
        User user = userDao.getUserbyId(userId);

        if(user == null){
            log.warn("userId {} is not find", userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        }

        int totalAmount = 0;
        List<OrderItem> orderItemList = new ArrayList<>();

        for(BuyItem buyItem : createOrderRequest.getBuyItemList()){
            Product product = productDao.getProductById(buyItem.getProductId());

            if(product == null){
                log.warn("product {} not find", buyItem.getProductId());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            } else if( product.getStock() < buyItem.getQuantity()){
                log.warn("product {} stock not enough, can not buy it. stock only have {}, your buy quantity is {} ",
                        buyItem.getProductId(), product.getStock(), buyItem.getQuantity());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

            productDao.updateStock(product.getProduct_id(), product.getStock() - buyItem.getQuantity());

            int amount = buyItem.getQuantity() * product.getPrice();
            totalAmount = totalAmount + amount;

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(buyItem.getProductId());
            orderItem.setQuantity(buyItem.getQuantity());
            orderItem.setAmount(amount);

            orderItemList.add(orderItem);


        }

        Integer orderId = orderDao.createOrder(userId, totalAmount);

        orderDao.createOrderItems(orderId, orderItemList);

        return orderId;
    }
}
