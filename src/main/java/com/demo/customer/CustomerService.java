package com.demo.customer;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    public void addNewCustomer(Customer customer) {
        Optional<Customer> customerOptional = customerRepository.findCustomerByEmail(customer.getEmail());
        if (customerOptional.isPresent()){
            throw new IllegalStateException("Email already exist!!");
        }
        customerRepository.save(customer);
    }

    public void deleteCustomer(Integer customerId) {
        boolean exists = customerRepository.existsById(customerId);
        if (!exists){
            throw new IllegalStateException("customer with id " + customerId + " does not exist.");
        }
        customerRepository.deleteById(customerId);
    }

    @Transactional
    public void updateCustomer(Integer customerId, String name, String email) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalStateException("Customer with id " + customerId + " does not exist."));

        if (name != null && !name.isEmpty() && !Objects.equals(customer.getName(), name)){
            customer.setName(name);
        }

        if (email != null && !email.isEmpty() && !Objects.equals(customer.getEmail(), email)){
            Optional<Customer> customerOptional = customerRepository.findCustomerByEmail(email);
            if (customerOptional.isPresent()) throw new IllegalStateException("Email already taken.");
            customer.setEmail(email);
        }
    }

}
