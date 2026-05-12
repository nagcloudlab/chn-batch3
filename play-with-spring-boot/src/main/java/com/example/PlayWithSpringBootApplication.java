package com.example;

import java.util.List;
import javax.sql.DataSource;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestTemplate;
// import org.springframework.web.reactive.function.client.WebClient;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
// import reactor.core.publisher.Mono;

@Configuration
class RedisCacheManagerConfiguration {

	@Bean
	public org.springframework.cache.CacheManager redisCacheManager(
			org.springframework.data.redis.connection.RedisConnectionFactory redisConnectionFactory) {
		org.springframework.data.redis.cache.RedisCacheConfiguration cacheConfig = org.springframework.data.redis.cache.RedisCacheConfiguration
				.defaultCacheConfig()
				.entryTtl(java.time.Duration.ofMinutes(10)) // Set TTL for cache entries
				.disableCachingNullValues(); // Optional: Disable caching of null values

		return org.springframework.data.redis.cache.RedisCacheManager.builder(redisConnectionFactory)
				.cacheDefaults(cacheConfig)
				.build();
	}

}

@Service
class AccountsService {
	@Cacheable(cacheNames = "accountDetails", key = "#id", cacheManager = "redisCacheManager", sync = true)
	public String getAccountDetails(String id) {
		System.out.println("Fetching account details for ID: " + id);
		if (id.equals("123")) {
			throw new IllegalArgumentException("Invalid account ID: " + id);
		}
		return "Account details for ID: " + id;
	}

}

@RestController
class AccountsRestController {
	private final AccountsService accountsService;

	public AccountsRestController(AccountsService accountsService) {
		this.accountsService = accountsService;
	}

	@RequestMapping("/accounts/{id}")
	public String getAccount(@PathVariable String id) {
		return accountsService.getAccountDetails(id);
	}
}

@Getter
@Setter
class Todo {
	private int userId;
	private int id;
	private String title;
	private boolean completed;
}

@Configuration
class RestTemplateConfig {
	@Bean
	public RestTemplate restTemplate() {
		var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(5000); // 5 seconds connection timeout
		factory.setReadTimeout(5000); // 5 seconds read timeout
		return new RestTemplate(factory);
	}
}

@RestController
class TodosController {

	// create a RestTemplate bean with connection and read timeouts

	private final RestTemplate restTemplate;

	public TodosController(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	// Mono -> 0 or 1 element
	// Flux -> 0 or N elements

	@RequestMapping("/todos")
	public List<Todo> getTodos() {
		System.out.println(Thread.currentThread().getName() + " - Handling /todos request");
		String url = "https://jsonplaceholder.typicode.com/todos?_limit=5";
		ResponseEntity<List<Todo>> response = restTemplate.exchange(
				url,
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<Todo>>() {
				});
		return response.getBody() != null ? response.getBody() : List.of();
		// return webClient.get()
		// .uri(url)
		// .retrieve()
		// .bodyToFlux(Todo.class)
		// .collectList();
	}
}

@RestControllerAdvice
@org.springframework.core.annotation.Order(1)
class FooControllerAdvice {
	@ExceptionHandler(IllegalArgumentException.class)
	public String handleIllegalArgumentException(IllegalArgumentException ex) {
		return "Handled IllegalArgumentException FooControllerAdvice " + ex.getMessage();
	}
}

@RestControllerAdvice
@org.springframework.core.annotation.Order(2) // Higher precedence than FooControllerAdvice
class BarControllerAdvice {
	@ExceptionHandler(IllegalArgumentException.class)
	public String handleIllegalArgumentException(IllegalArgumentException ex) {
		return "Handled IllegalArgumentException in BarControllerAdvice " + ex.getMessage();
	}
}

@RestController
class GreetRestController {

	@RequestMapping("/greet/{name}")
	public String greet(@PathVariable String name) {
		if (name.equals("poorna")) {
			throw new IllegalArgumentException("Name cannot be 'poorna'");
		}
		return "Hello, " + name + "!";
	}
}

@Getter
// @Setter
@AllArgsConstructor
// @NoArgsConstructor
@ConfigurationProperties("cassandra")
class CassandraConfigurationProperties {
	private final String contactPoints;
	private final int port;
	private final String keyspace;
}

@Service
class EmailService {
	@Async
	public void sendEmail(String to, String subject, String body) {
		// Code to send email
		String threadName = Thread.currentThread().getName();
		System.out.println("Sending email in thread: " + threadName);
		try {
			Thread.sleep(2000); // Simulate time taken to send email
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		System.out.println("To: " + to);
		System.out.println("Subject: " + subject);
		System.out.println("Body: " + body);
	}
}

@Service
class TransferService {
	private EmailService emailService;

	public TransferService(EmailService emailService) {
		this.emailService = emailService;
	}

	public void transferMoney(String fromAccount, String toAccount, double amount) {
		// Code to transfer money
		String threadName = Thread.currentThread().getName();
		System.out.println("Transferring money in thread: " + threadName);
		emailService.sendEmail(fromAccount, "Transfer Notification",
				"You have transferred " + amount + " to " + toAccount);
	}
}

@Getter
@Setter
@Entity
class Item {
	@Id
	int id;
	String name;
	double price;
}

@Getter
@Setter
@Entity
@Table(name = "orders")
class Order {
	@Id
	int id;
	String itemName;
	int quantity;
	double totalPrice;
	@OneToMany(cascade = jakarta.persistence.CascadeType.ALL, fetch = jakarta.persistence.FetchType.LAZY)
	@JoinTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"), inverseJoinColumns = @JoinColumn(name = "item_id"))
	java.util.List<Item> items = new java.util.ArrayList<>();
}

interface ItemRepository extends JpaRepository<Item, Integer> {
}

interface OrderRepository extends JpaRepository<Order, Integer> {
	@Query("SELECT o FROM Order o JOIN FETCH o.items")
	List<Order> findAllWithItems();

}

@Service
class ReportingService {
	private OrderRepository orderRepository;

	public ReportingService(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Transactional
	public void insertOrders() {
		for (int i = 1; i <= 10; i++) {
			Order order = new Order();
			order.setId(i);
			order.setItemName("Item" + i);
			order.setQuantity(10);
			order.setTotalPrice(10 * 10.0); // Assuming each item costs 10.0
			java.util.List<Item> items = new java.util.ArrayList<>();
			for (int j = 1; j <= 1; j++) {
				Item item = new Item();
				item.setId((i - 1) * 10 + j);
				item.setName("Item" + ((i - 1) * 10 + j));
				item.setPrice(10.0);
				items.add(item);
			}
			order.setItems(items);
			orderRepository.save(order);
		}
		System.out.println("Inserted 10 orders with 10 items each");
	}

	@Transactional
	public void generateReport() {
		System.out.println("-".repeat(100));
		// N + 1 query problem demonstration
		orderRepository.findAllWithItems().forEach(order -> {
			// System.out.println("Order ID: " + order.getId() + ", Item Name: " +
			// order.getItemName() + ", Quantity: "
			// + order.getQuantity() + ", Total Price: " + order.getTotalPrice());
			order.getItems().forEach(item -> {
				// System.out.println(
				// " Item ID: " + item.getId() + ", Name: " + item.getName() + ", Price: " +
				// item.getPrice());
			});
		});
		System.out.println("-".repeat(100));

	}
}

@org.springframework.stereotype.Component("customReadiness")
class CustomReadinessHealthIndicator implements HealthIndicator {
	private final DataSource dataSource;

	public CustomReadinessHealthIndicator(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Health health() {
		try (var connection = dataSource.getConnection(); var statement = connection.createStatement()) {
			statement.execute("SELECT 1");
			String database = connection.getMetaData().getDatabaseProductName();
			return Health.up().withDetail("condition", "database reachable")
					.withDetail("database", database).build();
		} catch (Exception ex) {
			return Health.down(ex).withDetail("condition", "database unreachable").build();
		}
	}
}

@SpringBootApplication
@EnableAsync
@EnableCaching
@EnableConfigurationProperties(CassandraConfigurationProperties.class)
public class PlayWithSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlayWithSpringBootApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(TransferService transferService, ItemRepository itemRepository,
			OrderRepository orderRepository, ReportingService reportingService,
			CassandraConfigurationProperties cassandraConfig) {
		return (args) -> {

			// transferService.transferMoney("AccountA", "AccountB", 100.0);
			// transferService.transferMoney("AccountC", "AccountD", 200.0);

			// reportingService.insertOrders();
			// System.out.println("Generating report...");
			// reportingService.generateReport();

			System.out.println("Cassandra Configuration:");
			System.out.println("Contact Points: " + cassandraConfig.getContactPoints());
			System.out.println("Port: " + cassandraConfig.getPort());
			System.out.println("Keyspace: " + cassandraConfig.getKeyspace());

		};
	}

	// Create TaskExecutor bean to configure the thread pool for @Async methods
	@Bean
	public org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor taskExecutor() {
		org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor executor = new org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(4);
		executor.setQueueCapacity(10);
		executor.initialize();
		return executor;
	}

}
