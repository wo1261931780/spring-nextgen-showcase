package wo1261931780.spring_nextgen_showcase.service;

/**
 * Created by Intellij IDEA.
 * Project:spring-nextgen-showcase
 * Package:wo1261931780.spring_nextgen_showcase.service
 *
 * @author liujiajun_junw
 * @Date 2025-05-18-56  星期日
 * @Description
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wo1261931780.spring_nextgen_showcase.entity.Product;
import wo1261931780.spring_nextgen_showcase.entity.ProductServiceClient;

/**
 * 订单服务类，演示了如何注入和使用声明式的 ProductServiceClient。
 * <p>
 * 文档片段:
 * <pre>
 * {@code
 * // 自动注入使用
 * @Service
 * public class OrderService {
 * @Autowired
 * private ProductServiceClient productClient;
 *
 * public void validateProduct(String productId) {
 * Product product = productClient.getProduct(productId);
 * // 校验逻辑...
 * }
 * }
 * }
 * </pre>
 * </p>
 * @author junw
 */
@Service
public class OrderService {

	private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

	private final ProductServiceClient productClient;

	/**
	 * 通过构造函数注入 ProductServiceClient。
	 * 这是推荐的依赖注入方式，而不是字段注入。
	 *
	 * @param productClient 产品服务客户端
	 */
	@Autowired
	public OrderService(ProductServiceClient productClient) {
		this.productClient = productClient;
	}

	/**
	 * 校验产品是否存在。
	 * 此方法通过调用 ProductServiceClient 来获取产品信息。
	 *
	 * @param productId 要校验的产品ID
	 * @return 如果产品存在则返回 true，否则根据 ProductServiceClient 的行为可能抛出异常或返回null。
	 * 实际应用中应处理 ProductServiceClient 可能抛出的异常 (例如 FeignException 或 WebClientResponseException)。
	 */
	public boolean validateProduct(String productId) {
		logger.info("正在校验产品ID: {}", productId);
		try {
			Product product = productClient.getProduct(productId);
			if (product != null) {
				logger.info("产品 {} (ID: {}) 校验成功: {}", product.getName(), productId, product);
				// 在这里可以添加更多的校验逻辑，例如检查库存、价格等
				return true;
			} else {
				// 这种情况通常是客户端配置为不抛出404异常，而是返回null
				logger.warn("未找到产品ID: {} (客户端返回null)", productId);
				return false;
			}
		} catch (Exception e) {
			// 声明式客户端在遇到 HTTP 错误 (如 404 Not Found) 时通常会抛出异常
			// 例如 WebClientResponseException (对于 WebClient) 或 FeignException (对于 OpenFeign)
			// 这里应该根据实际使用的客户端技术栈来捕获具体的异常
			logger.error("调用产品服务获取产品ID {} 时发生错误: {}", productId, e.getMessage());
			// 可以根据异常类型决定是向上抛出自定义异常还是返回false
			// 例如： if (e instanceof WebClientResponseException.NotFound) { return false; }
			return false;
		}
	}

	/**
	 * 示例：创建一个产品（如果需要通过订单服务代理创建产品）
	 * @param product 要创建的产品
	 * @return 创建后的产品
	 */
	public Product createNewProduct(Product product) {
		logger.info("尝试通过产品服务客户端创建新产品: {}", product.getName());
		try {
			Product createdProduct = productClient.createProduct(product);
			logger.info("产品 {} (ID: {}) 创建成功。", createdProduct.getName(), createdProduct.getId());
			return createdProduct;
		} catch (Exception e) {
			logger.error("通过产品服务客户端创建产品 {} 时发生错误: {}", product.getName(), e.getMessage());
			// 根据业务需求处理异常
			throw new RuntimeException("创建产品失败: " + product.getName(), e);
		}
	}
}
