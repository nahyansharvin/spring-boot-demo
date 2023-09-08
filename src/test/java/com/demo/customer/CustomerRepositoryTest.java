package com.demo.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void itShouldFindCustomerByEmail() {
        //given
        String email = "soman@gmail.com";
        Customer customer = new Customer(
                "Soman",
                email,
                LocalDate.of(2000,12,25)
        );
        underTest.save(customer);

        //when
        Optional<Customer> expectedCustomer = underTest.findCustomerByEmail(email);

        //then
        assertThat(expectedCustomer).isPresent();
    }

    @Test
    void itShouldNotFindCustomerByEmail() {
        //given
        String email = "soman@gmail.com";

        //when
        Optional<Customer> expectedCustomer = underTest.findCustomerByEmail(email);

        //then
        assertThat(expectedCustomer).isNotPresent();
    }
}