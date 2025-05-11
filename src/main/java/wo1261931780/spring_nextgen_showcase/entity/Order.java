package wo1261931780.spring_nextgen_showcase.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// 使用 Lombok 简化代码 (如果项目中已配置)
// import lombok.Data;
// import lombok.NoArgsConstructor;

/**
 * @author junw
 */ // @Data
// @NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "orders") // JPA 表名，避免与SQL关键字冲突
public class Order {

	// --- Getters and Setters ---
	@Id
	@GeneratedValue(strategy = GenerationType.UUID) // 使用UUID作为主键
	private UUID id;

	private String customerName;
	private LocalDateTime orderDate;
	// private List<String> productIds; // 简单示例，实际可能更复杂
	private String status; // 例如: PENDING, PROCESSING, COMPLETED, CANCELLED

	public Order() {
		this.orderDate = LocalDateTime.now();
		this.status = "PENDING";
	}

	public Order(String customerName) {
		this();
		this.customerName = customerName;
	}


	@Override
	public String toString() {
		return "Order{" +
				"id=" + id +
				", customerName='" + customerName + '\'' +
				", orderDate=" + orderDate +
				", status='" + status + '\'' +
				'}';
	}
}
