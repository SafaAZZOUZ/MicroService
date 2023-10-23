package org.sid.billingservice;
import org.sid.billingservice.entities.Bill;
import org.sid.billingservice.entities.ProductItems;
import org.sid.billingservice.feign.CustomerRestClient;
import org.sid.billingservice.feign.ProductItemRestClient;
import org.sid.billingservice.model.Customer;
import org.sid.billingservice.model.Product;
import org.sid.billingservice.repository.BillRepository;
import org.sid.billingservice.repository.ProductItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.PagedModel;

import java.util.Date;
import java.util.Random;

@SpringBootApplication
@EnableFeignClients
public class BillingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BillingServiceApplication.class,
				args);
	}

	@Bean
	CommandLineRunner strat(BillRepository billRepository,
							ProductItemRepository productItemRepository,
							CustomerRestClient customerRestClient,
							ProductItemRestClient productItemRestClient)
	{
		return args -> {
			Customer customer = customerRestClient.getCustomerById(1L);
			Bill bill=billRepository.save(new Bill(null, new Date(),null,
					customer.getId(),null));
			PagedModel<Product> products=productItemRestClient.pageProducts(3,10);
			products.forEach(p-> {
				ProductItems productItems=new ProductItems();
				productItems.setPrice(p.getPrice());
				productItems.setQuantity(1+new Random().nextInt(100));
				productItems.setBill(bill);
				productItemRepository.save(productItems);
			});
			System.out.println("---------------------------");
			System.out.println(customer.getId());
			System.out.println(customer.getName());
			System.out.println(customer.getEmail());
		};
	}
}
