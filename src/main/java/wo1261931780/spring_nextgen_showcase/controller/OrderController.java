package wo1261931780.spring_nextgen_showcase.controller;

// 假设有一个 OrderService 用于处理业务逻辑

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry; // 更推荐通过 MeterRegistry 创建 Counter
import io.micrometer.core.instrument.Metrics; // 另一种方式，但不推荐在 bean 中直接使用静态 Metrics 类
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct; // For initializing metrics after construction
import wo1261931780.spring_nextgen_showcase.entity.Order;

/**
 * 订单控制器，演示如何集成自定义业务指标以供 Prometheus 监控。
 * <p>
 * 文档片段:
 * <pre>
 * {@code
 * // 自定义业务指标
 * @RestController
 * public class OrderController {
 * private final Counter orderCounter = Metrics.counter("orders.total");
 * @PostMapping("/orders")
 * public Order createOrder() {
 * orderCounter.increment();
 * // 创建订单逻辑...
 * }
 * }
 * }
 * </pre>
 * </p>
 * <p>
 * 注意：更推荐的方式是通过 MeterRegistry 注入来创建和注册指标，而不是直接使用静态的 Metrics.counter。
 * </p>
 * @author junw
 */
@RestController
@RequestMapping("/api/orders") // 统一的API路径前缀
public class OrderController {

	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

	// Micrometer MeterRegistry 用于创建和管理指标
	private final MeterRegistry meterRegistry;

	// 自定义计数器：统计创建的订单总数
	private Counter ordersTotalCounter;
	// 自定义计数器：统计不同状态的订单数量
	private Counter ordersStatusPendingCounter;
	private Counter ordersStatusCompletedCounter;

	// 假设有一个 OrderService 来处理订单创建的业务逻辑
	// 如果 OrderService 还没有创建订单的逻辑，这里可以先简化处理
	// private final OrderService orderService;

	@Autowired
	public OrderController(MeterRegistry meterRegistry /*, OrderService orderService */) {
		this.meterRegistry = meterRegistry;
		// this.orderService = orderService;

		// 初始化指标 (在构造函数中或使用 @PostConstruct)
		initializeMetrics();
	}

	/**
	 * 初始化自定义指标。
	 * 推荐在构造函数或 @PostConstruct 方法中进行，以确保 MeterRegistry 已注入。
	 */
	private void initializeMetrics() {
		// 订单总数计数器
		// 文档示例: Metrics.counter("orders.total");
		// 推荐方式: meterRegistry.counter(...)
		this.ordersTotalCounter = Counter.builder("orders.total")
				.description("Total number of orders created.")
				.tag("type", "creation") // 可以添加标签以提供更多维度
				.register(this.meterRegistry);

		// 按状态统计订单的计数器示例
		this.ordersStatusPendingCounter = this.meterRegistry.counter("orders.status", "status", "pending");
		this.ordersStatusCompletedCounter = this.meterRegistry.counter("orders.status", "status", "completed");
	}


	/**
	 * 创建一个新订单。
	 * 每次调用此端点时，相关的 Prometheus 指标会增加。
	 *
	 * @param orderRequest 包含订单信息的请求体 (简化示例，实际可能是一个DTO)
	 * @return 创建的订单对象和 HTTP 状态
	 */
	@PostMapping
	public ResponseEntity<Order> createOrder(@RequestBody OrderCreationRequest orderRequest) {
		logger.info("接收到创建订单请求: {}", orderRequest.getCustomerName());

		// 模拟订单创建逻辑
		Order newOrder = new Order(orderRequest.getCustomerName());
		// 实际应用中会调用 orderService.create(newOrder);
		// newOrder = orderService.createOrder(newOrder); // 假设 OrderService 有此方法

		// 增加订单总数计数器
		this.ordersTotalCounter.increment();
		logger.info("orders.total 计数器已增加。当前值 (近似): {}", this.ordersTotalCounter.count());

		// 根据订单状态增加相应计数器
		if ("PENDING".equalsIgnoreCase(newOrder.getStatus())) {
			this.ordersStatusPendingCounter.increment();
		}
		// 当订单完成时: this.ordersStatusCompletedCounter.increment();

		// 模拟返回创建的订单
		// 实际应用中，ID 等信息会在保存到数据库后由JPA等设置
		// newOrder.setId(java.util.UUID.randomUUID()); // 临时设置ID，如果Order类没有自动生成

		logger.info("订单创建成功: {}", newOrder);
		return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
	}

	// 内部类或单独的DTO用于请求体
	public static class OrderCreationRequest {
		private String customerName;
		// 可以添加其他需要的字段，如产品列表等
		// private List<String> productIds;

		public String getCustomerName() {
			return customerName;
		}
		public void setCustomerName(String customerName) {
			this.customerName = customerName;
		}
		// public List<String> getProductIds() { return productIds; }
		// public void setProductIds(List<String> productIds) { this.productIds = productIds; }
	}

	// 你可以添加其他端点来演示其他类型的指标，例如 Gauge, Timer, DistributionSummary
	// 例如，一个端点用于更新订单状态，并更新相应的状态计数器
}
