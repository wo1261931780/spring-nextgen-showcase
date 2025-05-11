package wo1261931780.spring_nextgen_showcase.client;

/**
 * Created by Intellij IDEA.
 * Project:spring-nextgen-showcase
 * Package:wo1261931780.spring_nextgen_showcase.client
 *
 * @author liujiajun_junw
 * @Date 2025-05-18-58  星期日
 * @Description
 */

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;

/**
 * 全局异常处理器，用于将特定异常转换为 RFC 7807 ProblemDetail 响应。
 * <p>
 * 文档片段:
 * <pre>
 * {@code
 * @RestControllerAdvice
 * public class GlobalExceptionHandler {
 * @ExceptionHandler(ProductNotFoundException.class)
 * public ProblemDetail handleProductNotFound(ProductNotFoundException ex) {
 * ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
 * problem.setType(URI.create("/errors/product-not-found"));
 * problem.setTitle("商品不存在");
 * problem.setDetail("商品ID: " + ex.getProductId());
 * return problem;
 * }
 * }
 * }
 * </pre>
 * </p>
 * <p>
 * 注意：@RestControllerAdvice 结合了 @ControllerAdvice 和 @ResponseBody，
 * 意味着 @ExceptionHandler 方法的返回值将直接作为 HTTP 响应体。
 * </p>
 * @author junw
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler { // 继承 ResponseEntityExceptionHandler 可以方便地处理 Spring MVC 的标准异常

	/**
	 * 处理 ProductNotFoundException 异常。
	 * 当 ProductNotFoundException 被抛出时，此方法会捕获它，
	 * 并构造一个 ProblemDetail 对象作为 HTTP 响应。
	 *
	 * @param ex 捕获到的 ProductNotFoundException 实例
	 * @return 一个 ProblemDetail 对象，包含了错误的详细信息
	 */
	@ExceptionHandler(ProductNotFoundException.class)
	public ProblemDetail handleProductNotFound(ProductNotFoundException ex) {
		// 创建一个 ProblemDetail 实例，状态码设置为 404 NOT_FOUND
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getLocalizedMessage());

		// 设置 'type' 字段，指向一个描述此错误类型的 URI (可选，但推荐)
		// 这个 URI 可以是你的 API 文档中关于此错误类型的链接
		problemDetail.setType(URI.create("/errors/product-not-found"));

		// 设置 'title' 字段，一个对此错误的简短、人类可读的摘要
		problemDetail.setTitle("商品不存在");

		// 'detail' 字段已经通过 forStatusAndDetail 设置为异常的 message，
		// 如果需要更具体的细节，可以覆盖或追加
		// problemDetail.setDetail("请求的商品ID: " + ex.getProductId() + " 未找到。");

		// 你还可以添加自定义的扩展属性
		problemDetail.setProperty("productId", ex.getProductId());
		problemDetail.setProperty("timestamp", System.currentTimeMillis());

		// 返回 ProblemDetail 对象，Spring 会自动将其序列化为 JSON (或其他协商的内容类型)
		return problemDetail;
	}

	// 你可以在这里添加更多的 @ExceptionHandler 方法来处理其他自定义异常
	// 例如：
	// @ExceptionHandler(InvalidInputException.class)
	// public ProblemDetail handleInvalidInput(InvalidInputException ex) {
	//     ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage());
	//     problemDetail.setTitle("无效的输入参数");
	//     problemDetail.setType(URI.create("/errors/invalid-input"));
	//     problemDetail.setProperty("invalidFields", ex.getInvalidFields()); // 假设 InvalidInputException 有 getInvalidFields 方法
	//     return problemDetail;
	// }

	// 处理一般性或未捕获的异常 (作为最后的防线)
	@ExceptionHandler(Exception.class)
	public ProblemDetail handleGenericException(Exception ex) {
		// 对于未知错误，通常返回 500 Internal Server Error
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部发生未知错误，请稍后重试。");
		problemDetail.setTitle("内部服务器错误");
		problemDetail.setType(URI.create("/errors/internal-server-error"));
		logger.error("未捕获的异常: ", ex); // 记录未知异常的堆栈信息
		return problemDetail;
	}
}
