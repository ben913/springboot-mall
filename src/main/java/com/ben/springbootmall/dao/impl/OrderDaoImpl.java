package com.ben.springbootmall.dao.impl;

import com.ben.springbootmall.dao.OrderDao;
import com.ben.springbootmall.dto.OrderQueryParams;
import com.ben.springbootmall.dto.ProductQueryParams;
import com.ben.springbootmall.model.Order;
import com.ben.springbootmall.model.OrderItem;
import com.ben.springbootmall.model.Product;
import com.ben.springbootmall.rowmapper.OrderItemRowMapper;
import com.ben.springbootmall.rowmapper.OrderRowMapper;
import com.ben.springbootmall.rowmapper.ProductRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderDaoImpl implements OrderDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Integer countOrder(OrderQueryParams orderQueryParams) {
        String sql = "SELECT count(order_id) FROM `order` WHERE 1=1";

        Map<String, Object> map = new HashMap<>();

        sql = addFilteringSql(sql, map, orderQueryParams);

        Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

        return total;
    }

    @Override
    public List<Order> getOrders(OrderQueryParams orderQueryParams) {
        String sql ="SELECT order_id, user_id, total_amount, created_date, last_modified_date FROM `order` WHERE 1=1";
                   // "FROM order WHERE 1=1";
        Map<String, Object> map = new HashMap<>();
        sql = addFilteringSql(sql, map, orderQueryParams);
        sql = sql + " ORDER BY created_date DESC";

        sql = sql + " LIMIT :limit OFFSET :offset";
        map.put("limit", orderQueryParams.getLimit());
        map.put("offset", orderQueryParams.getOffset());

        List<Order> orderList = namedParameterJdbcTemplate.query(sql, map, new OrderRowMapper());

        return  orderList;
    }

    @Override
    public Order getOrderById(Integer orderId) {
        String sql = "SELECT order_id, user_id, total_amount, created_date, last_modified_date " +
                "FROM `order` WHERE order_id = :orderId";

        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);

       List<Order> orderList = namedParameterJdbcTemplate.query(sql, map, new OrderRowMapper());

        if(orderList.size() > 0){
            return orderList.get(0);
        } else {
            return  null;
        }

    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Integer orderId) {
        String sql = "SELECT oi.order_item_id, oi.order_id, oi.product_id, oi.quantity, oi.amount, p.product_name, p.image_url " +
                "FROM order_item as oi " +
                "LEFT JOIN product as p ON oi.product_id = p.product_id " +
                "WHERE oi.order_id = :orderId";

        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);

        List<OrderItem> orderItemList = namedParameterJdbcTemplate.query(sql, map, new OrderItemRowMapper());

        return  orderItemList;
    }

    @Override
    public Integer createOrder(Integer userId, Integer totalAmount) {
        String sql = "INSERT INTO `order` (user_id, total_amount, created_date, last_modified_date) " +
                "VALUES (:userId, :totalAmount, :createDate, :lastModifiedDate)";

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("totalAmount", totalAmount);

        Date now = new Date();
        map.put("createDate", now);
        map.put("lastModifiedDate", now);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        int orderId = keyHolder.getKey().intValue();

        return orderId;


    }

    @Override
    public void createOrderItems(Integer orderId, List<OrderItem> orderItemList) {

        String sql = "INSERT INTO order_item (order_id, product_id, quantity, amount) " +
                "VALUES (:orderId, :productId, :quantity, :amount)";

        MapSqlParameterSource[]  parameteSources = new MapSqlParameterSource[orderItemList.size()];

        for(int i = 0; i < orderItemList.size(); i++){
            OrderItem orderItem = orderItemList.get(i);

            parameteSources[i] = new MapSqlParameterSource();
            parameteSources[i].addValue("orderId", orderId);
            parameteSources[i].addValue("productId",  orderItem.getProductId());
            parameteSources[i].addValue("quantity", orderItem.getQuantity());
            parameteSources[i].addValue("amount", orderItem.getAmount());
        }

        namedParameterJdbcTemplate.batchUpdate(sql, parameteSources);
    }

    private String addFilteringSql(String sql, Map<String, Object> map, OrderQueryParams orderQueryParams) {

        if (orderQueryParams.getUserId() != null) {
            sql = sql + " AND user_id = :userId";
            map.put("userId", orderQueryParams.getUserId());
        }

        return sql;
    }
}
