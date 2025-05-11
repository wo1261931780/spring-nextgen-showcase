package wo1261931780.spring_nextgen_showcase.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Intellij IDEA.
 * Project:spring-nextgen-showcase
 * Package:wo1261931780.spring_nextgen_showcase.entity
 *
 * @author liujiajun_junw
 * @Date 2025-05-18-49  星期日
 * @Description
 */

// 使用 Lombok 来简化代码，如果项目中配置了 Lombok
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

	private Long id;
	private String username;
	private String email;

	// 如果不使用 Lombok，需要手动添加构造函数、getter 和 setter

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", username='" + username + '\'' +
				", email='" + email + '\'' +
				'}';
	}
}
