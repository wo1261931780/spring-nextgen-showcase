package wo1261931780.spring_nextgen_showcase.client;

/**
 * Created by Intellij IDEA.
 * Project:spring-nextgen-showcase
 * Package:wo1261931780.spring_nextgen_showcase.client
 *
 * @author liujiajun_junw
 * @Date 2025-05-18-57  星期日
 * @Description
 */

/**
 * 当无法找到请求的产品时抛出的自定义异常。
 * <p>
 * 文档中的 GlobalExceptionHandler 会捕获此异常，并将其转换为 ProblemDetail 响应。
 * </p>
 * <p>
 * 文档片段 (GlobalExceptionHandler):
 * <pre>
 * {@code
 * @ExceptionHandler(ProductNotFoundException.class)
 * public ProblemDetail handleProductNotFound(ProductNotFoundException ex) {
 * // ...
 * problem.setDetail("商品ID: " + ex.getProductId());
 * // ...
 * }
 * }
 * </pre>
 * </p>
 * <p>
 * 文档片段 (触发异常的 Controller):
 * <pre>
 * {@code
 * @GetMapping("/products/{id}")
 * public Product getProduct(@PathVariable String id) {
 * return productRepo.findById(id)
 * .orElseThrow(() -> new ProductNotFoundException(id));
 * }
 * }
 * </pre>
 * </p>
 */
public class ProductNotFoundException extends RuntimeException {

	private final String productId;

	public ProductNotFoundException(String productId) {
		super("未能找到产品，ID: " + productId); // 调用父类构造函数设置异常消息
		this.productId = productId;
	}

	public ProductNotFoundException(String productId, Throwable cause) {
		super("未能找到产品，ID: " + productId, cause);
		this.productId = productId;
	}

	/**
	 * 获取未找到的产品的ID。
	 *
	 * @return 产品ID字符串
	 */
	public String getProductId() {
		return productId;
	}
}
