package wo1261931780.spring_nextgen_showcase.entity;

// 使用 Lombok 简化代码 (如果项目中已配置)
// import lombok.Data;
// import lombok.AllArgsConstructor;
// import lombok.NoArgsConstructor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于封装产品详细信息，包括产品基本信息和库存数量。
 * 在 ProductController 中作为响应体返回。
 * @author junw
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetail {

	private Product product;
	private Integer stock; // 库存数量

	@Override
	public String toString() {
		return "ProductDetail{" +
				"product=" + (product != null ? product.getName() : "null") + // 避免 product 为 null 时出错
				", stock=" + stock +
				'}';
	}
}
