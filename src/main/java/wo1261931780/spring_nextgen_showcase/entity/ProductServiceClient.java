package wo1261931780.spring_nextgen_showcase.entity;

/**
 * Created by Intellij IDEA.
 * Project:spring-nextgen-showcase
 * Package:wo1261931780.spring_nextgen_showcase.entity
 *
 * @author liujiajun_junw
 * @Date 2025-05-18-55  星期日
 * @Description
 */
import org.springframework.web.bind.annotation.PathVariable; // 虽然是传统注解，但在 @HttpExchange 接口方法参数中仍可使用
import org.springframework.web.bind.annotation.RequestBody; // 同上
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * 声明式的产品服务 HTTP 客户端接口。
 * 使用 @HttpExchange 注解 (Spring Framework 6)。
 * <p>
 * 文档片段:
 * <pre>
 * {@code
 * @HttpExchange(url = "/products", accept = "application/json")
 * public interface ProductServiceClient {
 * @GetExchange("/{id}")
 * Product getProduct(@PathVariable String id);
 * @PostExchange
 * Product createProduct(@RequestBody Product product);
 * }
 * }
 * </pre>
 * </p>
 * 注意：为了让这个接口能够被 Spring 容器管理并注入，
 * 你需要在配置类中（例如 SpringNextgenShowcaseApplication）通过 HttpServiceProxyFactory 创建它的代理实例。
 * (已在 SpringNextgenShowcaseApplication.java 中提供相关配置)
 * @author junw
 */
@HttpExchange(url = "/products", accept = "application/json") // 基础URL路径，并指定接受 application/json 类型响应
public interface ProductServiceClient {

	/**
	 * 根据产品ID获取产品信息。
	 * 对应的 HTTP GET 请求会发往 <baseUrl>/products/{id}
	 *
	 * @param id 产品ID
	 * @return 产品信息
	 */
	@GetExchange("/{id}")
	Product getProduct(@PathVariable("id") String id); // @PathVariable 用于路径变量绑定

	/**
	 * 创建一个新产品。
	 * 对应的 HTTP POST 请求会发往 <baseUrl>/products
	 * 请求体将包含 product 对象序列化后的 JSON 数据。
	 *
	 * @param product 要创建的产品对象
	 * @return 创建成功后的产品对象（通常包含由服务器生成的ID等信息）
	 */
	@PostExchange
	Product createProduct(@RequestBody Product product); // @RequestBody 用于将方法参数绑定到请求体

	// 你可以根据需要添加其他产品相关的API方法，例如：
	// @PutExchange("/{id}")
	// Product updateProduct(@PathVariable("id") String id, @RequestBody Product product);
	//
	// @DeleteExchange("/{id}")
	// void deleteProduct(@PathVariable("id") String id);
	//
	// @GetExchange
	// List<Product> listAllProducts();
}
