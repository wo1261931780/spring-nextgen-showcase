package wo1261931780.spring_nextgen_showcase.entity;

/**
 * Created by Intellij IDEA.
 * Project:spring-nextgen-showcase
 * Package:wo1261931780.spring_nextgen_showcase.entity
 *
 * @author liujiajun_junw
 * @Date 2025-05-18-47  星期日
 * @Description
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Service
public class VirtualThreadDemo {

	private static final Logger logger = LoggerFactory.getLogger(VirtualThreadDemo.class);

	/**
	 * 演示启动单个虚拟线程的基本用法。
	 * 来自文档片段: // 示例：虚拟线程使用
	 * Thread.ofVirtual().name("my-virtual-thread").start(() -> { // 业务逻辑 });
	 * <p>
	 * 注意: 此特性通常需要 JDK 19+。
	 * </p>
	 */
	public void demonstrateSingleVirtualThread() {
		logger.info("尝试启动单个虚拟线程...");
		try {
			Thread virtualThread = Thread.ofVirtual().name("my-virtual-thread").start(() -> {
				logger.info("单个虚拟线程 {} 正在运行。", Thread.currentThread());
				// 模拟一些业务逻辑
				try {
					Thread.sleep(100); // 模拟耗时操作
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					logger.warn("单个虚拟线程 {} 被中断。", Thread.currentThread().getName());
				}
				logger.info("单个虚拟线程 {} 完成。", Thread.currentThread().getName());
			});
			logger.info("已提交单个虚拟线程: {}", virtualThread.getName());
			// 等待虚拟线程结束以便观察日志，实际应用中可能不需要join
			// virtualThread.join();
		} catch (UnsupportedOperationException e) {
			logger.error("当前JDK版本/配置不支持或未启用虚拟线程。请确保使用JDK 19+并已启用相关特性。", e);
		} catch (Exception e) {
			logger.error("启动单个虚拟线程时发生错误", e);
		}
	}

	/**
	 * 演示使用 ExecutorService 为每个任务创建新的虚拟线程。
	 * 来自文档片段:
	 * // 新方案（虚拟线程）
	 * ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();
	 * // 处理10000个并发请求
	 * IntStream.range(0, 10000).forEach(i ->
	 * virtualExecutor.submit(() -> { // 处理订单逻辑 processOrder(i); })
	 * );
	 * <p>
	 * 注意: Executors.newVirtualThreadPerTaskExecutor() 是 JDK 21 中的标准API, JDK 19/20 中为预览API。
	 * </p>
	 */
	public void demonstrateVirtualThreadPerTaskExecutor() {
		logger.info("演示基于任务的虚拟线程池 (newVirtualThreadPerTaskExecutor)...");
		// 确保JDK版本支持 (JDK 21+ 标准, JDK 19/20 预览)
		try (ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor()) {
			int numberOfTasks = 100; // 文档中是10000，为了演示方便减少数量
			logger.info("向虚拟线程池提交 {} 个任务...", numberOfTasks);

			IntStream.range(0, numberOfTasks).forEach(i ->
					virtualExecutor.submit(() -> {
						processOrderTask(i);
					})
			);
			logger.info("所有 {} 个任务已提交。等待执行完成 (ExecutorService 将自动关闭)...", numberOfTasks);
			// try-with-resources 会自动关闭 executor，并等待已提交任务完成（优雅关闭）
		} catch (UnsupportedOperationException e) {
			logger.error("当前JDK版本/配置不支持或未启用 Executors.newVirtualThreadPerTaskExecutor()。", e);
			logger.warn("请确保使用支持此特性的JDK版本 (例如JDK 21+，或JDK 19/20并启用预览特性)。");
		} catch (Exception e) {
			logger.error("执行虚拟线程池演示时发生错误", e);
		}
		logger.info("基于任务的虚拟线程池演示完成。");
	}

	private void processOrderTask(int orderId) {
		// 模拟处理订单的逻辑
		// logger.info("订单 {} 正在由线程 {} 处理。", orderId, Thread.currentThread());
		try {
			Thread.sleep(10); // 模拟I/O密集型操作
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			// logger.warn("订单 {} 的处理线程 {} 被中断。", orderId, Thread.currentThread().getName());
		}
		// 为了避免日志过多，可以有选择性地打印
		if (orderId % 20 == 0) {
			logger.info("订单 {} 处理完成，执行线程: {}", orderId, Thread.currentThread());
		}
	}

	/**
	 * 作为对比: 传统的固定大小平台线程池。
	 * 来自文档片段: // 旧方案（平台线程）
	 * ExecutorService executor = Executors.newFixedThreadPool(200);
	 */
	public void demonstratePlatformThreadPoolExecutor() {
		logger.info("演示平台线程池 (newFixedThreadPool)...");
		try (ExecutorService platformExecutor = Executors.newFixedThreadPool(50)) { // 文档中是200，减少数量
			int numberOfTasks = 100; // 文档中是10000
			logger.info("向平台线程池提交 {} 个任务...", numberOfTasks);

			IntStream.range(0, numberOfTasks).forEach(i ->
					platformExecutor.submit(() -> {
						processOrderTask(i);
					})
			);
			logger.info("所有 {} 个任务已提交。等待执行完成 (ExecutorService 将自动关闭)...", numberOfTasks);
		} catch (Exception e) {
			logger.error("执行平台线程池演示时发生错误", e);
		}
		logger.info("平台线程池演示完成。");
	}

	// 你可以创建一个简单的 Controller 来触发这些演示方法，
	// 或者在主应用类中使用 ApplicationRunner 来快速测试。
	// 例如，在 SpringNextgenShowcaseApplication.java 中添加:
	// @Bean
	// public org.springframework.boot.ApplicationRunner runner(VirtualThreadDemo demo) {
	// return args -> {
	// logger.info("--- 开始虚拟线程演示 ---");
	// demo.demonstrateSingleVirtualThread();
	// try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } // 短暂暂停
	//
	// demo.demonstrateVirtualThreadPerTaskExecutor();
	// try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } // 短暂暂停
	//
	// demo.demonstratePlatformThreadPoolExecutor();
	// logger.info("--- 虚拟线程演示结束 ---");
	//     };
	// }
}
