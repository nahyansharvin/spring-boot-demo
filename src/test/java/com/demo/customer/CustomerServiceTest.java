package com.demo.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock  private CustomerRepository customerRepository;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerRepository);
    }

    @Test
    void canGetAllCustomers() {
        //when
        underTest.getCustomers();
        //then
        verify(customerRepository).findAll();
    }

    @Test
    void canAddNewCustomer() {
        //given
        Customer customer = new Customer(
                "Soman",
                "soman@gmail.com",
                LocalDate.of(2000,12,25)
        );
        //when
        underTest.addNewCustomer(customer);

        //then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerRepository).save(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer).isEqualTo(customer);
    }

    @Test
    void willThrowWhenEmailTaken() {
        //given
        Customer customer = new Customer(
                "Soman",
                "soman@gmail.com",
                LocalDate.of(2000,12,25)
        );
        given(customerRepository.findCustomerByEmail(customer.getEmail()))
                .willReturn(Optional.of(customer));

        //when
        //then
        assertThatThrownBy(() -> underTest.addNewCustomer(customer))
                .hasMessageContaining("Email already exist");
        verify(customerRepository, never()).save(any());

    }

    @Test
    void deleteCustomer() {
        //given
        Integer customerId = 1;
        given(customerRepository.existsById(customerId))
                .willReturn(true);
        //when
        underTest.deleteCustomer(customerId);
        //then
        verify(customerRepository).deleteById(customerId);
    }

    @Test
    void willThrowWhenIdNotFound() {
        //given
        Integer customerId = 1;
        given(customerRepository.existsById(customerId))
                .willReturn(false);
        //when
        //then
        assertThatThrownBy(() -> underTest.deleteCustomer(customerId))
                .hasMessageContaining("customer with id " + customerId + " does not exist.");
        verify(customerRepository, never()).deleteById(any());
    }

    @Test
    void canUpdateCustomer() {
        //given
        Integer customerId = 1;
        String name = "New Customer";
        String email = "newmail@gmail.com";
        Customer existingCustomer = new Customer(
                "Old Customer",
                "old@gmail.com",
                LocalDate.of(2000,12,25)
        );
        given(customerRepository.findById(customerId))
                .willReturn(Optional.of(existingCustomer));
        given(customerRepository.findCustomerByEmail(email)).willReturn(Optional.empty());
        //when
        underTest.updateCustomer(customerId, name, email);

        //then
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, times(1)).findCustomerByEmail(email);

        assertThat(existingCustomer.getName()).isEqualTo(name);
        assertThat(existingCustomer.getEmail()).isEqualTo(email);
    }

    @Test
    void cantUpdateCustomer_CustomerNotFound() {
        //given
        Integer customerId = 1;
        String name = "New Customer";
        String email = "newmail@gmail.com";

        given(customerRepository.findById(customerId))
                .willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.updateCustomer(customerId, name, email))
                .hasMessageContaining("Customer with id " + customerId + " does not exist.");

        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, times(0)).findCustomerByEmail(email);
    }

    @Test
    void cantUpdateCustomer_EmailTaken() {
        //given
        Integer customerId = 1;
        String name = "New Customer";
        String email = "newmail@gmail.com";
        Customer existingCustomer = new Customer(
                "Old Customer",
                email,
                LocalDate.of(2000,12,25));

        given(customerRepository.findById(customerId))
                .willReturn(Optional.of(new Customer(customerId,"Another name", "another@gmail.com", LocalDate.of(2000,12,25))));
        given(customerRepository.findCustomerByEmail(email)).willReturn(Optional.of(existingCustomer));

        //when
        //then
        assertThatThrownBy(() -> underTest.updateCustomer(customerId, name, email))
                .hasMessageContaining("Email already taken.");

        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, times(1)).findCustomerByEmail(email);
    }
}