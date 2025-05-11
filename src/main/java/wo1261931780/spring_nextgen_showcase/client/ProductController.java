package wo1261931780.spring_nextgen_showcase.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import wo1261931780.spring_nextgen_showcase.client.ProductNotFoundException;
import wo1261931780.spring_nextgen_showcase.client.ProductRepository;
import wo1261931780.spring_nextgen_showcase.client.StockServiceClient;
import wo1261931780.spring_nextgen_showcase.entity.Product;
import wo1261931780.spring_nextgen_showcase.entity.ProductDetail;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 产品控制器，演示了新特性组合使用的实战案例。
 * 包括：虚拟线程处理高并发查询、声明式客户端调用、JPA Repository、自定义异常处理。
 * <p>
 * 文档片段:
 * <pre>
 * {@code
 * // 商品查询服务（组合使用新特性）
 * @RestController
 * public class ProductController {
 * // 声明式调用库存服务
 * @Autowired
 * private StockServiceClient stockClient;
 * // 虚拟线程处理高并发查询
 * @GetMapping("/products/{id}")
 * public ProductDetail getProduct(@PathVariable String id) {
 * return CompletableFuture.supplyAsync(() -> {
 * Product product = productRepository.findById(id) // 假设 productRepository 已注入
 * .orElseThrow(() -> new ProductNotFoundException(id));
 * // 并行查询库存
 * Integer stock = stockClient.getStock(id);
 * return new ProductDetail(product, stock);
 * }, Executors.newVirtualThreadPerTaskExecutor()).join();
 * }
 * }
 * }
 * </pre>
 * </p>
 */
@RestController
@RequestMapping("/api/products") // 统一的API路径前缀
public class ProductController {

	private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

	private final ProductRepository productRepository;
	private final StockServiceClient stockClient;

	// 用于执行异步任务的虚拟线程池
	// 推荐将其声明为 bean 或由 Spring 管理，而不是每次请求都创建新的
	// 但为了严格遵循文档片段，这里直接在方法内创建
	// private final ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
	// 如果要作为 bean:
	// @Bean public ExecutorService virtualThreadTaskExecutor() { return Executors.newVirtualThreadPerTaskExecutor(); }
	// 然后在这里 @Autowired private ExecutorService virtualThreadTaskExecutor;


	@Autowired
	public ProductController(ProductRepository productRepository, StockServiceClient stockClient) {
		this.productRepository = productRepository;
		this.stockClient = stockClient;
	}

	/**
	 * 根据产品ID获取产品详细信息（包括库存）。
	 * 使用虚拟线程处理并发请求，并组合调用产品数据和库存数据。
	 *
	 * @param id 产品ID
	 * @return ProductDetail 包含产品信息和库存
	 */
	@GetMapping("/{id}")
	public CompletableFuture<ProductDetail> getProductDetails(@PathVariable String id) {
		logger.info("接收到产品详情请求，产品ID: {} (将使用虚拟线程处理)", id);

		// 为了严格遵循文档片段中的 .join()，这里返回 CompletableFuture<ProductDetail>
		// 然后在客户端（或测试中）调用 .join() 或 .get() 来获取结果。
		// 如果希望控制器直接返回 ProductDetail (阻塞直到完成)，则可以在这里 .join()。
		// 文档示例直接在方法体返回类型为 ProductDetail，并在 supplyAsync 后 .join()。
		// 为了更符合异步非阻塞控制器的风格，返回 CompletableFuture 通常更好。
		// 但这里我们先按照文档的返回类型 ProductDetail 来写，并在内部 join。

		// ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor(); // 每次调用都创建新的执行器不是最佳实践
		// 更好的做法是注入一个共享的 ExecutorService bean
		// 为了简单并贴合文档，暂时这样处理

		return CompletableFuture.supplyAsync(() -> {
					logger.info("虚拟线程 {} 开始处理产品ID: {}", Thread.currentThread(), id);

					// 1. 查询产品基本信息
					Product product = productRepository.findById(id)
							.orElseThrow(() -> {
								logger.warn("产品ID: {} 未找到，将抛出 ProductNotFoundException", id);
								return new ProductNotFoundException(id); // 由 GlobalExceptionHandler 处理
							});
					logger.info("产品ID: {} 查询成功: {}", id, product.getName());

					// 2. (并行)查询库存信息
					// 在这个简化的 supplyAsync 块中，stockClient.getStock(id) 是同步调用。
					// 如果 stockClient.getStock(id) 本身也是异步的 (返回 CompletableFuture)，
					// 则可以使用 thenCombine 等方法来组合结果。
					// 为了模拟文档中的“并行查询库存”概念，可以理解为这两个操作相对于其他请求是并行的。
					Integer stock = -1; // 默认值
					try {
						stock = stockClient.getStock(id);
						logger.info("产品ID: {} 库存查询成功: {}", id, stock);
					} catch (Exception e) {
						// 如果库存服务调用失败，记录错误并可能返回一个默认库存值或特定错误指示
						logger.error("调用库存服务查询产品ID {} 的库存时发生错误: {}", id, e.getMessage(), e);
						// 根据业务需求，这里可以抛出异常，或者返回一个表示库存未知的 ProductDetail
						// 例如: throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "无法获取库存信息", e);
						// 为了演示，我们继续并可能使用默认库存值
					}

					logger.info("虚拟线程 {} 完成处理产品ID: {}", Thread.currentThread(), id);
					return new ProductDetail(product, stock);
				}, Executors.newVirtualThreadPerTaskExecutor()) // 每次都创建新的虚拟线程执行器
				.exceptionally(ex -> {
					// 处理 CompletableFuture 中发生的异常
					logger.error("处理产品详情请求时发生异步异常 (产品ID: {}): {}", id, ex.getMessage(), ex);
					if (ex.getCause() instanceof ProductNotFoundException pnfe) {
						// 如果是 ProductNotFoundException，GlobalExceptionHandler 应该已经处理了
						// 但如果 supplyAsync 内部直接抛出，这里可以再次包装或处理
						throw pnfe; // 重新抛出，让Spring的异常处理机制捕获
					}
					// 对于其他类型的异常，可以转换为合适的HTTP状态码
					// 例如，如果库存服务调用失败且在内部抛出了特定异常
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "获取产品详情时发生内部错误", ex);
				});
	}


	/**
	 * 文档中给出的 getProduct 方法签名是直接返回 ProductDetail，
	 * 这意味着在方法内部会阻塞等待 CompletableFuture 完成。
	 * 下面是更贴近文档原始签名的实现。
	 */
	@GetMapping("/v2/products/{id}") // 使用不同路径以区分
	public ProductDetail getProductDetailsBlocking(@PathVariable String id) {
		logger.info("接收到产品详情请求 (阻塞版本)，产品ID: {}", id);
		try {
			// 每次调用都创建新的执行器不是最佳实践
			ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();
			ProductDetail result = CompletableFuture.supplyAsync(() -> {
				logger.info("虚拟线程 {} (阻塞版本) 开始处理产品ID: {}", Thread.currentThread(), id);
				Product product = productRepository.findById(id)
						.orElseThrow(() -> new ProductNotFoundException(id));
				logger.info("产品ID: {} (阻塞版本) 查询成功: {}", id, product.getName());

				Integer stock = -1;
				try {
					stock = stockClient.getStock(id);
					logger.info("产品ID: {} (阻塞版本) 库存查询成功: {}", id, stock);
				} catch (Exception e) {
					logger.error("调用库存服务查询产品ID {} (阻塞版本) 的库存时发生错误: {}", id, e.getMessage(), e);
					// 这里可以决定如何处理库存查询失败
				}
				return new ProductDetail(product, stock);
			}, virtualExecutor).join(); // .join() 会阻塞当前线程直到 CompletableFuture 完成

			// 关闭执行器是一个好习惯，但如果每次请求都创建，则需要小心管理
			// virtualExecutor.shutdown(); // 如果是共享的bean，不应在这里关闭

			return result;
		} catch (ProductNotFoundException pnfe) {
			logger.warn("产品ID: {} (阻塞版本) 未找到，重新抛出 ProductNotFoundException", id);
			throw pnfe; // 确保自定义异常被 GlobalExceptionHandler 捕获
		} catch (Exception e) {
			logger.error("获取产品详情 (阻塞版本) 时发生意外错误 (产品ID: {}): {}", id, e.getMessage(), e);
			// 对于其他在 .join() 期间可能发生的异常 (如 CompletionException)
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "获取产品详情时发生错误", e);
		}
	}
}
