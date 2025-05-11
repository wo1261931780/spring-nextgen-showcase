package wo1261931780.spring_nextgen_showcase.client;

/**
 * Created by Intellij IDEA.
 * Project:spring-nextgen-showcase
 * Package:wo1261931780.spring_nextgen_showcase.client
 *
 * @author liujiajun_junw
 * @Date 2025-05-18-51  星期日
 * @Description
 */

import org.springframework.web.bind.annotation.GetMapping; // 传统 Spring MVC 注解，在 @HttpExchange 中不直接使用
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import wo1261931780.spring_nextgen_showcase.entity.User;

import java.util.List;

/**
 * 声明式的用户服务 HTTP 客户端接口。
 * 使用 @HttpExchange 注解 (Spring Framework 6)。
 * <p>
 * 文档片段:
 * <pre>
 * {@code
 * @HttpExchange(url = "/api/users")
 * public interface UserClient {
 * @GetExchange
 * List<User> listUsers();
 * }
 * }
 * </pre>
 * </p>
 * 注意：为了让这个接口能够被 Spring 容器管理并注入，
 * 你需要在配置类中（例如 SpringNextgenShowcaseApplication）通过 HttpServiceProxyFactory 创建它的代理实例。
 * <p>
 * 示例配置 (已在 SpringNextgenShowcaseApplication.java 中提供):
 * <pre>
 * {@code
 * @Bean
 * public UserClient userClient(WebClient webClient) { // 假设已配置 WebClient bean
 * HttpServiceProxyFactory factory = HttpServiceProxyFactory
 * .builder(WebClientAdapter.forClient(webClient))
 * .build();
 * return factory.createClient(UserClient.class);
 * }
 * }
 * </pre>
 */
@HttpExchange(url = "/api/users") // 基础URL路径，相对于 WebClient 配置的 baseUrl
public interface UserClient {

	/**
	 * 获取用户列表。
	 * 对应的 HTTP GET 请求会发往 <baseUrl>/api/users
	 *
	 * @return 用户列表
	 */
	@GetExchange
	List<User> listUsers();

	// 你可以在这里根据需要添加其他用户相关的API方法，例如：
	// @GetExchange("/{userId}")
	// User getUserById(@PathVariable("userId") Long userId);
	//
	// @PostExchange
	// User createUser(@RequestBody User newUser);
}
