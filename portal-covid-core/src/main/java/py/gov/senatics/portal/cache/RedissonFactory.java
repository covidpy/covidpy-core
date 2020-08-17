package py.gov.senatics.portal.cache;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@ApplicationScoped
public class RedissonFactory {

	private RedissonClient redisson;

	@Inject
	private Logger logger;

	@Inject
	ConfiguracionCache conf;

	@PostConstruct
	public void initBean() {
		Config config = new Config();

		config.useSingleServer().setAddress("redis://"
				.concat(conf.get("adminportalpy.redis.host").concat(":").concat(conf.get("adminportalpy.redis.port"))));

		if (conf.get("adminportalpy.redis.env").equalsIgnoreCase("produccion")) {
			logger.info("REDIS configurado para producción");
			config.setThreads(Runtime.getRuntime().availableProcessors() * 2);

			config.setNettyThreads(Runtime.getRuntime().availableProcessors() * 2);
		} else {
			logger.warning("REDIS configurado para ambiente de desarrollo");
		}

		redisson = Redisson.create(config);

		logger.info("Redis client started");
	}

	@PreDestroy
	public void destroyBean() {

		this.redisson.shutdown(3000, 1000, TimeUnit.MILLISECONDS);

		logger.info("Redis client destroyed");
	}

	public RedissonClient getRedisClient() {
		return this.redisson;
	}

}
