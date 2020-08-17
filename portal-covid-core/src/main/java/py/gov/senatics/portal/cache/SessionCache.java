package py.gov.senatics.portal.cache;

import org.redisson.api.MapOptions;
import org.redisson.api.RMapCache;
import py.gov.senatics.portal.dto.covid19.admin.Session;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@ApplicationScoped
public class SessionCache {

	private RMapCache<String, Session> sessionCache;

	@Inject
	private Logger logger;

	@Inject
	private ConfiguracionCache conf;

	@Inject
	private RedissonFactory redisson;

	private Long timeToLive;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PostConstruct
	public void initBean() {

		logger.info("Iniciando cache de sesiones");

		MapOptions sessionCacheOptions = MapOptions.defaults();

		sessionCache = redisson.getRedisClient().getMapCache("webappCovidCache", sessionCacheOptions);

		timeToLive = Long.valueOf(this.conf.get("adminportalpy.redis.timetolive"));

		logger.info("Expiracion de la sesión configurada en " + timeToLive.toString() + " minutos");

	}

	public void start() {
		logger.info("Cache de sesiones iniciado.");
	}

	public Session getSession(String k) {
		return this.sessionCache.get(k);
	}

	public void putSession(String k, Session v) {

		Boolean created = this.sessionCache.fastPut(k, v, timeToLive, TimeUnit.MINUTES);

		if (created)
			logger.info("Sesión creada: " + v.toString());
		else
			logger.info("Sesión actualizada: " + v.toString());

	}

}
