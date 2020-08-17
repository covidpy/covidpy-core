package py.gov.senatics.portal.cache;


import py.gov.senatics.portal.persistence.covid19.admin.ConfiguracionDao;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;

@ApplicationScoped
public class ConfiguracionCache {

	private Map<String, String> conf;

	@Inject
	private ConfiguracionDao confDao;

	@PostConstruct
	public void initBean() {
		this.conf = this.confDao.obtenerConfiguracionesMap();
	}

	public String get(String key) {
		return this.conf.get(key);
	}

	public String getOrDefault(String key) {
		return this.conf.getOrDefault(key, "");
	}

	public String getOrDefault(String key, String defaultVal) {
		return this.conf.getOrDefault(key, defaultVal);
	}

}
