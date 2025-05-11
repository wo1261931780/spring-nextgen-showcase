package wo1261931780.spring_nextgen_showcase.client;

import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 声明式的库存服务 HTTP 客户端接口。
 * 用于 ProductController 查询产品库存。
 * <p>
 * 文档片段 (ProductController):
 * <pre>
 * {@code
 * // 声明式调用库存服务
 * @Autowired
 * private StockServiceClient stockClient;
 * // ...
 * Integer stock = stockClient.getStock(id);
 * }
 * </pre>
 * </p>
 * <p>
 * 假设库存服务有一个端点如 /api/stock/{productId} 返回该产品的库存数量。
 * </p>
 * 注意：为了让这个接口能够被 Spring 容器管理并注入，
 * 你需要在配置类中（例如 SpringNextgenShowcaseApplication）通过 HttpServiceProxyFactory 创建它的代理实例。
 * (已在 SpringNextgenShowcaseApplication.java 中添加了此 Bean 的配置)
 */
@HttpExchange(url = "/api/stock") // 假设库存服务的基础路径是 /api/stock
public interface StockServiceClient {

	/**
	 * 根据产品ID获取库存数量。
	 *
	 * @param productId 产品ID
	 * @return 库存数量 (Integer)
	 */
	@GetExchange("/{productId}")
	Integer getStock(@PathVariable("productId") String productId);

	// 你可以根据需要添加其他库存相关的API方法，例如：
	// @PostExchange("/{productId}/decrease")
	// void decreaseStock(@PathVariable("productId") String productId, @RequestParam("amount") int amount);
}
