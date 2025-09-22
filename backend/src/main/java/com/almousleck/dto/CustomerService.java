package com.almousleck.dto;

import com.almousleck.model.Customer;
import com.almousleck.repository.CustomerRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public Page<CustomerDto> list(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.
                of(page, size, Sort.by(sortBy == null ? "id" : sortBy)
                .ascending());
        return customerRepository.findAll(pageable)
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<CustomerDto> get(Long id) {
        return customerRepository
                .findById(id)
                .map(this::toDto);
    }

    @Transactional
    public CustomerDto createCustomer(CreateCustomerRequest request) {
        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        return toDto(customerRepository.save(customer));
    }

    @Transactional
    public CustomerDto update(Long id, UpdateCustomerRequest request) {
        Customer customer = customerRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Customer with id " + id + " does not exist"));
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        return toDto(customerRepository.save(customer));
    }


    @Transactional
    public void delete(Long id) {
        try {
            customerRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new RuntimeException("Customer with id " + id + " does not exist");
        }
    }

    private CustomerDto toDto(Customer customer) {
        return new CustomerDto(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail()
        );
    }
}
