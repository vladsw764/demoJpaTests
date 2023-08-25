package com.example.demowithtests.service.fillDataBase;

import com.example.demowithtests.domain.Address;
import com.example.demowithtests.domain.Employee;
import com.example.demowithtests.repository.EmployeeRepository;
import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@AllArgsConstructor
@Service
public class LoaderServiceBean implements LoaderService {

    private final EmployeeRepository employeeRepository;

    private static final int NUM_THREADS = 30;
    private static final int EMPLOYEES_PER_THREAD = 100000 / NUM_THREADS;

    /**
     *
     */
    @Override
    public void generateData() {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        for (int i = 0; i < NUM_THREADS; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                List<Employee> employees = createListEmployees();
                employeeRepository.saveAll(employees);
            }, executor);
            futures.add(future);
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOf.join();

        executor.shutdown();
    }

    /**
     * @return
     */
    @Override
    public long count() {
        return employeeRepository.count();
    }

    public List<Employee> createListEmployees() {
        List<Employee> employees = new ArrayList<>();
        long seed = Thread.currentThread().getId(); // Use thread ID as seed for better randomness

        Faker faker = new Faker(new Random(seed));
        for (int i = 0; i < EMPLOYEES_PER_THREAD; i++) {
            String name = faker.name().name();
            String country = faker.country().name();
            String email = faker.name().name();
            System.out.println("number -" + i);

            Set<Address> addresses = createAddresses(faker);

            Employee employee = Employee.builder()
                    .name(name)
                    .country(country)
                    .email(email.toLowerCase().replaceAll(" ", "") + "@mail.com")
                    .addresses(addresses)
                    .build();

            employees.add(employee);
        }

        return employees;
    }

    private Set<Address> createAddresses(Faker faker) {
        Set<Address> addresses = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            String country = faker.country().name();
            String city = faker.address().city();
            String street = faker.address().streetAddress();

            Address address = Address.builder()
                    .country(country)
                    .city(city)
                    .street(street)
                    .build();

            addresses.add(address);
        }
        return addresses;
    }
}