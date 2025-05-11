package wo1261931780.spring_nextgen_showcase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * @author junw
 */
@SpringBootApplication
public class SpringNextgenShowcaseApplication {


	public static void main(String[] args) {
		SpringApplication.run(SpringNextgenShowcaseApplication.class, args);
		System.out.println("\n🎉 Spring NextGen Showcase Application Started! 🎉");
		System.out.println("访问 http://localhost:8080 查看应用状态或API。");
		System.out.println("Prometheus 指标: http://localhost:8080/actuator/prometheus");
		// 如果配置了OAuth2授权服务器，你可能需要访问特定的OAuth2端点进行测试
		// 例如: http://localhost:8080/.well-known/oauth-authorization-server (如果 issuer-uri 是 http://localhost:8080)
		// 或者你配置的 issuer-uri + /.well-known/oauth-authorization-server
	}

	// 为 RestTemplate Exchange 声明式 HTTP 客户端提供 RestTemplate bean (如果选择这种方式)
	// 注意：@HttpExchange 更常与 WebClient 一起使用，如下面的 WebClient bean 所示。
	// 如果你确实想将 @HttpExchange 与 RestTemplate 一起使用，你需要额外的配置。
	// @Bean
	// public RestTemplate restTemplate(RestTemplateBuilder builder) {
	//     return builder.build();
	// }


	// 为 @HttpExchange 声明式 HTTP 客户端提供 WebClient bean
	// 这是推荐的方式
	@Bean
	public WebClient webClient() {
		return WebClient.builder()
				.baseUrl("http://localhost:8080/api") // 示例基础URL，实际应配置或动态获取
				.build();
	}

	// 创建 UserClient 代理实例
	// 你可以为每个 @HttpExchange 接口创建一个类似的 bean
	// @Bean
	// public UserClient userClient(WebClient webClient) {
	// 	HttpServiceProxyFactory factory = HttpServiceProxyFactory
	// 			.builder(WebClientAdapter.forClient(webClient))
	// 			.build();
	// 	return factory.createClient(UserClient.class);
	// }
	//
	// // 创建 ProductServiceClient 代理实例
	// @Bean
	// public ProductServiceClient productServiceClient(WebClient webClient) {
	// 	// 假设 ProductServiceClient 的 baseUrl 与 UserClient 不同，或者可以共享同一个 WebClient 实例
	// 	// 如果 baseUrl 不同，可以创建一个新的 WebClient 实例或配置多个。
	// 	// 例如:
	// 	// WebClient productWebClient = WebClient.builder().baseUrl("http://product-service/api").build();
	//
	// 	HttpServiceProxyFactory factory = HttpServiceProxyFactory
	// 			.builder(WebClientAdapter.forClient(webClient)) // 这里暂时复用上面的 webClient
	// 			.build();
	// 	return factory.createClient(ProductServiceClient.class);
	// }
	//
	// // 创建 StockServiceClient 代理实例 (在 ProductController 中用到)
	// @Bean
	// public StockServiceClient stockServiceClient(WebClient webClient) {
	// 	// 假设 StockServiceClient 的 baseUrl
	// 	// WebClient stockWebClient = WebClient.builder().baseUrl("http://stock-service/api").build();
	//
	// 	HttpServiceProxyFactory factory = HttpServiceProxyFactory
	// 			.builder(WebClientAdapter.forClient(webClient)) // 这里暂时复用上面的 webClient
	// 			.build();
	// 	return factory.createClient(StockServiceClient.class);
	// }
}
