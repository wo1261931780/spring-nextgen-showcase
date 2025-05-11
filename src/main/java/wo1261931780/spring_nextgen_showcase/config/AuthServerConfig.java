package wo1261931780.spring_nextgen_showcase.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
// OAuth2AuthorizationServerConfiguration 用于 jwtDecoder，可能不再直接用于 applyDefaultSecurity
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;

/**
 * OAuth2 授权服务器配置。
 * <p>
 * 更新以解决 http.apply(Configurer) 的弃用问题。
 * </p>
 * @author junw
 */
@Configuration
@EnableWebSecurity // 启用 Spring Security 的 Web 安全支持
public class AuthServerConfig {

	@Value("${spring.security.oauth2.authorizationserver.issuer-uri}") // 从 application.yml 读取 issuer-uri
	private String issuerUri;

	/**
	 * 配置 OAuth2 授权服务器核心协议端点的 SecurityFilterChain。
	 * 这个 SecurityFilterChain 的优先级应该比较高。
	 *
	 * @param http HttpSecurity
	 * @return SecurityFilterChain
	 * @throws Exception 配置异常
	 */
	@Bean
	@Order(1)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
		// 使用 http.with(Configurer, Customizer) 替代 http.apply(Configurer)
		// 1. 应用 OAuth2AuthorizationServerConfigurer 的默认配置
		http.with(new OAuth2AuthorizationServerConfigurer(), Customizer.withDefaults());

		// 2. 获取已应用的 configurer 实例以进行进一步的自定义，例如启用 OIDC
		http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
				.oidc(Customizer.withDefaults());   // 启用OIDC UserInfo 端点和 Client Registration 端点等

		http
				// 当未认证时，重定向到登录页面 (通常是从 /oauth2/authorize 端点)
				.exceptionHandling(exceptions -> exceptions
						.defaultAuthenticationEntryPointFor(
								new LoginUrlAuthenticationEntryPoint("/login"), // 定义登录页面的路径
								new MediaTypeRequestMatcher(MediaType.TEXT_HTML) // 只对接受HTML的请求进行重定向
						)
				)
				// 如果需要，配置资源服务器以接受访问令牌进行端点保护
				// 在这个独立的授权服务器中，可能主要是保护 /userinfo 端点
				.oauth2ResourceServer(resourceServer -> resourceServer
						.jwt(Customizer.withDefaults())); // 使用JWT作为资源服务器的令牌类型

		return http.build();
	}

	/**
	 * 配置用户认证的 SecurityFilterChain。
	 * 这个 SecurityFilterChain 用于处理用户通过表单登录进行身份验证。
	 *
	 * @param http HttpSecurity
	 * @return SecurityFilterChain
	 * @throws Exception 配置异常
	 */
	@Bean
	@Order(2) // 优先级低于授权服务器的FilterChain
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/login", "/error", "/webjars/**", "/css/**", "/js/**").permitAll() // 允许访问登录页面、错误页和静态资源
						.anyRequest().authenticated() // 其他所有请求都需要认证
				)
				// 配置表单登录
				.formLogin(formLogin -> formLogin
						.loginPage("/login") // 指定自定义登录页面的路径 (如果需要，否则使用默认)
						.permitAll()
				);
		return http.build();
	}

	/**
	 * 配置 UserDetailsService 用于用户身份验证。
	 * 在生产环境中，你应该替换为例如从数据库加载用户的实现。
	 *
	 * @param passwordEncoder 密码编码器
	 * @return UserDetailsService
	 */
	@Bean
	public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
		UserDetails userDetails = User.builder()
				.username("user")
				.password(passwordEncoder.encode("password")) // 密码是 "password"
				.roles("USER")
				.build();
		return new InMemoryUserDetailsManager(userDetails);
	}

	/**
	 * 配置密码编码器。
	 *
	 * @return PasswordEncoder
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	/**
	 * 配置 RegisteredClientRepository 用于管理客户端信息。
	 * 在生产环境中，你应该替换为例如 JdbcRegisteredClientRepository。
	 *
	 * @param passwordEncoder 密码编码器
	 * @return RegisteredClientRepository
	 */
	@Bean
	public RegisteredClientRepository registeredClientRepository(PasswordEncoder passwordEncoder) {
		RegisteredClient oidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId("oidc-client")
				.clientSecret(passwordEncoder.encode("secret")) // 客户端密钥是 "secret"
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.redirectUri("http://127.0.0.1:8080/login/oauth2/code/oidc-client") // 客户端应用的回调地址
				.redirectUri("https://oauth.pstmn.io/v1/callback") // Postman 测试用的回调地址
				.redirectUri("http://localhost:3000/callback") // 常见的本地开发前端回调
				.scope(OidcScopes.OPENID)
				.scope(OidcScopes.PROFILE)
				.scope("message.read")
				.scope("message.write")
				.clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build()) // 需要用户授权确认
				.tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofHours(1)).build()) // 访问令牌有效期1小时
				.build();

		return new InMemoryRegisteredClientRepository(oidcClient);
	}

	/**
	 * 配置 JWKSource 用于 JWT 签名。
	 * JWK (JSON Web Key) 是用于签发JWT的密钥。
	 *
	 * @return JWKSource
	 */
	@Bean
	public JWKSource<SecurityContext> jwkSource() {
		KeyPair keyPair = generateRsaKey();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		RSAKey rsaKey = new RSAKey.Builder(publicKey)
				.privateKey(privateKey)
				.keyID(UUID.randomUUID().toString())
				.build();
		JWKSet jwkSet = new JWKSet(rsaKey);
		return new ImmutableJWKSet<>(jwkSet);
	}

	/**
	 * 生成 RSA 密钥对的辅助方法。
	 * 在生产中，你应该从安全的地方加载密钥，而不是每次启动时生成。
	 *
	 * @return KeyPair
	 */
	private static KeyPair generateRsaKey() {
		KeyPair keyPair;
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			keyPair = keyPairGenerator.generateKeyPair();
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
		return keyPair;
	}

	/**
	 * 配置 JwtDecoder，用于解码 JWT (例如，当授权服务器也作为资源服务器时)。
	 *
	 * @param jwkSource JWKSource
	 * @return JwtDecoder
	 */
	@Bean
	public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
		// 使用 OAuth2AuthorizationServerConfiguration 提供的静态方法来创建默认的 jwtDecoder
		// 这通常会配置正确的 issuer 和 audience 验证（如果适用）
		return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
	}

	/**
	 * 配置 AuthorizationServerSettings，用于定义授权服务器的端点和 issuer URI。
	 *
	 * @return AuthorizationServerSettings
	 */
	@Bean
	public AuthorizationServerSettings authorizationServerSettings() {
		return AuthorizationServerSettings.builder()
				.issuer(this.issuerUri) // 设置 Issuer URI，从 application.yml 读取
				.build();
	}
}
