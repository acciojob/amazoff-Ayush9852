package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrderRepository {

    HashMap<String, Order> orderIds = new HashMap<>();
    HashMap<String, DeliveryPartner> deliveryPartner = new HashMap<>();
    HashMap<String, String> orderPartnerPair = new HashMap<>();

    public void addOrder(Order order) {
        if(order == null)
            return;

        if(!orderIds.containsKey(order.getId()))
            orderIds.put(order.getId(), order);
    }

    public void addPartner(String partnerId) {
        if(partnerId == null)
            return;

        if(!deliveryPartner.containsKey(partnerId))
            deliveryPartner.put(partnerId, new DeliveryPartner(partnerId));
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {
        if(!orderPartnerPair.containsKey(orderId)) {
            orderPartnerPair.put(orderId, partnerId);
            deliveryPartner.get(partnerId).setNumberOfOrders(deliveryPartner.get(partnerId).getNumberOfOrders() + 1);
        }
    }

    public Order getOrderById(String orderId) {
        if(orderIds.containsKey(orderId))
            return orderIds.get(orderId);
        return null;
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        if(deliveryPartner.containsKey(partnerId))
            return deliveryPartner.get(partnerId);
        return null;
    }

    public Integer getOrderCountByPartnerId(String partnerId) {
        if(deliveryPartner.containsKey(partnerId))
            return deliveryPartner.get(partnerId).getNumberOfOrders();
        return 0;
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        ArrayList<String> ordersList = new ArrayList<>();
        for(Map.Entry<String, String> pair: orderPartnerPair.entrySet())
            if(pair.getValue().equals(partnerId))
                ordersList.add(pair.getKey());

        return ordersList;
    }

    public List<String> getAllOrders() {
        ArrayList<String> orders = new ArrayList<>();
        for(String order: orderIds.keySet())
            orders.add(order);

        return orders;
    }

    public Integer getCountOfUnassignedOrders() {
        return orderIds.size() - orderPartnerPair.size();
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {
        String[] val = time.split(":");
        int HH = Integer.parseInt(val[0]);
        int MM = Integer.parseInt(val[1]);

        int Time = HH*60 + MM;
        int count = 0;

        for(Map.Entry<String, String> pair: orderPartnerPair.entrySet()) {
            if (pair.getValue().equals(partnerId))
                if (orderIds.get(pair.getKey()).getDeliveryTime() > Time)
                    count += 1;
        }

        return count;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        int time = 0;
        for(Map.Entry<String, String> pair: orderPartnerPair.entrySet()) {
            if(pair.getValue().equals(partnerId))
                time = Math.max(time, orderIds.get(pair.getKey()).getDeliveryTime());
        }

        int MM = time%60;
        int HH = time/60;
        return HH + ":" + MM;
    }

    public void deletePartnerById(String partnerId) {
        for(Map.Entry<String, String> pair: orderPartnerPair.entrySet())
            if(pair.getValue().equals(partnerId))
                orderPartnerPair.remove(pair.getKey());

        deliveryPartner.remove(partnerId);
    }

    public void deleteOrderById(String orderId) {
        orderPartnerPair.remove(orderId);
        orderIds.remove(orderId);
    }
}
