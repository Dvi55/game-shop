package com.gameshop.www.eCommerce.user.service;

import com.gameshop.www.eCommerce.user.dao.LocalUserDAO;
import com.gameshop.www.eCommerce.user.model.LocalUser;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserGeneratorService {
    private LocalUserDAO localUserDAO;
    List<LocalUser> users = new ArrayList<>();

    public UserGeneratorService(LocalUserDAO localUserDAO) {
        this.localUserDAO = localUserDAO;
    }

    public void generateUsers() {
        Faker faker = new Faker();
        for (int i = 0; i < 10; i++) {
            LocalUser user = createUser(faker);
            users.add(user);
        }
        localUserDAO.saveAll(users);
        users.clear();
    }

    private LocalUser createUser(Faker faker) {
        LocalUser user = new LocalUser();
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setEmail(faker.internet().emailAddress());
        user.setPassword(faker.internet().password());
        user.setPhoneNumber(faker.phoneNumber().cellPhone());
        user.setIsEmailVerified(true);
        return user;
    }
}
