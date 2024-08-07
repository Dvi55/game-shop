package com.gameshop.ecommerce.generator.service;

import com.gameshop.ecommerce.user.dao.LocalUserDAO;
import com.gameshop.ecommerce.user.model.LocalUser;
import com.gameshop.ecommerce.order.dao.WebOrderDAO;
import com.gameshop.ecommerce.order.dao.WebOrderQuantityDAO;
import com.gameshop.ecommerce.order.model.WebOrder;
import com.gameshop.ecommerce.order.model.WebOrderQuantity;
import com.gameshop.ecommerce.product.dao.ProductDAO;
import com.gameshop.ecommerce.product.model.Product;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderGeneratorService {

    private final LocalUserDAO localUserDAO;
    private final WebOrderDAO webOrderDAO;
    private final ProductDAO productDAO;
    private final WebOrderQuantityDAO webOrderQuantityDAO;

    List<LocalUser> users = new ArrayList<>();
    List<Product> products = new ArrayList<>();
    List<WebOrderQuantity> webOrderQuantities = new ArrayList<>();

    public void generateOrders() {
        Faker faker = new Faker();
        List<WebOrder> orders = new ArrayList<>();
        if (users.isEmpty()) {
            fillData();
        }
        for (int i = 0; i < 50; i++) {
            WebOrder order = createOrder(faker);
            orders.add(order);
            webOrderQuantities.addAll(order.getQuantities());
        }
        webOrderDAO.saveAll(orders);
        webOrderQuantityDAO.saveAll(webOrderQuantities);
    }

    private WebOrder createOrder(Faker faker) {
        WebOrder order = new WebOrder();
        List<WebOrderQuantity> quantities = new ArrayList<>();
        for (int i = 0; i < faker.number().numberBetween(1, 5); i++) {
            quantities.add(createOrderQuantity(faker, order));
        }
        order.setQuantities(quantities);
        order.setUser(users.get(faker.number().numberBetween(0, users.size() - 1)));
        return order;
    }

    private WebOrderQuantity createOrderQuantity(Faker faker, WebOrder webOrder) {
        WebOrderQuantity orderQuantity = new WebOrderQuantity();
        orderQuantity.setQuantity(faker.number().numberBetween(1, 6));
        orderQuantity.setProduct(products.get(faker.number().numberBetween(0, products.size() - 1)));
        orderQuantity.setOrder(webOrder);
        return orderQuantity;
    }

    private void fillData() {
        users = localUserDAO.findAll();
        products = productDAO.findRandomProducts();
    }
}
