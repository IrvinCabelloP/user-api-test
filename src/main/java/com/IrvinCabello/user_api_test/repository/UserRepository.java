package com.IrvinCabello.user_api_test.repository;

import com.IrvinCabello.user_api_test.model.User;
import com.IrvinCabello.user_api_test.model.Address;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class UserRepository {

    private static List<User> users = new ArrayList<>();

    static {

        Address work1 = new Address();
        work1.setId(1); work1.setName("workaddress"); work1.setStreet("street No. 1"); work1.setCountry_code("UK");
        Address home1 = new Address();
        home1.setId(2); home1.setName("homeaddress"); home1.setStreet("street No. 2"); home1.setCountry_code("AU");

        Address work2 = new Address();
        work2.setId(3); work2.setName("workaddress"); work2.setStreet("street No. 3"); work2.setCountry_code("UK");
        Address home2 = new Address();
        home2.setId(4); home2.setName("homeaddress"); home2.setStreet("street No. 4"); home2.setCountry_code("AU");

        Address work3 = new Address();
        work3.setId(5); work3.setName("workaddress"); work3.setStreet("street No. 5"); work3.setCountry_code("UK");
        Address home3 = new Address();
        home3.setId(6); home3.setName("homeaddress"); home3.setStreet("street No. 6"); home3.setCountry_code("AU");

        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setEmail("user1@mail.com");
        user1.setName("Julian");
        user1.setPhone("+1 55 555 555 55");
        user1.setPassword("7c4a8d09ca3762af61e59520943dc26494f8941b");
        user1.setTax_id("AARR990101XXX");
        user1.setCreated_at("01-01-2026 00:00:00");
        user1.setAddresses(List.of(work1, home1));
        users.add(user1);

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setEmail("user2@mail.com");
        user2.setName("Andres");
        user2.setPhone("+52 555 123 45 67");
        user2.setPassword("password_encriptada_aqui");
        user2.setTax_id("ANDR850101ABC");
        user2.setCreated_at("02-01-2026 10:00:00");
        user2.setAddresses(List.of(work2, home2));
        users.add(user2);

        User user3 = new User();
        user3.setId(UUID.randomUUID());
        user3.setEmail("user3@mail.com");
        user3.setName("Carlos");
        user3.setPhone("+34 600 000 000");
        user3.setPassword("password_encriptada_aqui");
        user3.setTax_id("ZULE700101XYZ");
        user3.setCreated_at("03-01-2026 15:30:00");
        user3.setAddresses(List.of(work3, home3));
        users.add(user3);
    }

    public List<User> findAll() {
        return users;
    }

    public User findByTaxId(String taxId) {
        return users.stream()
                .filter(u -> u.getTax_id().equals(taxId))
                .findFirst()
                .orElse(null);
    }

    public User save(User user) {
        users.add(user);
        return user;
    }

    public User findById(UUID id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    public boolean deleteById(UUID id) {
        return users.removeIf(u -> u.getId().equals(id));
    }
}