package wo1261931780.spring_nextgen_showcase.entity;

/**
 * Created by Intellij IDEA.
 * Project:spring-nextgen-showcase
 * Package:wo1261931780.spring_nextgen_showcase.entity
 *
 * @author liujiajun_junw
 * @Date 2025-05-18-53  星期日
 * @Description
 */

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;

// 使用 Lombok 来简化代码
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity // 假设这是一个JPA实体，如果 ProductRepository.findById(id) 存在的话
public class Product {

	@Id
	// @GeneratedValue(strategy = GenerationType.IDENTITY) // 如果ID是自增的
	private String id; // 文档中的 ProductServiceClient 和 ProductController 示例使用 String id
	private String name;
	private String description;
	private BigDecimal price;
	// private Integer stock; // 库存信息可能会在 ProductDetail 中或者由专门的库存服务管理

	@Override
	public String toString() {
		return "Product{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", price=" + price +
				'}';
	}
}
