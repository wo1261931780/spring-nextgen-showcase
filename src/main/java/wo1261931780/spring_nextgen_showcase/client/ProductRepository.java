package wo1261931780.spring_nextgen_showcase.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wo1261931780.spring_nextgen_showcase.entity.Product;

import java.util.Optional;

/**
 * Spring Data JPA Repository 接口，用于 Product 实体的数据库操作。
 * <p>
 * 文档片段 (ProductController):
 * <pre>
 * {@code
 * Product product = productRepository.findById(id)
 * .orElseThrow(() -> new ProductNotFoundException(id));
 * }
 * </pre>
 * </p>
 * Product 实体类已定义，其 ID 类型为 String。
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, String> { // Product 的主键类型是 String

	// JpaRepository 已经提供了 findById(ID id) 方法，返回 Optional<Product>
	// 你可以在这里根据需要添加自定义的查询方法，例如：
	// List<Product> findByNameContainingIgnoreCase(String name);
	// Optional<Product> findBySku(String sku);
}
