package victor.training.oo.creational.singleton;

import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableCaching
@SpringBootApplication
public class SingletonSpringApp implements CommandLineRunner{
	@Bean
	public static CustomScopeConfigurer defineThreadScope() {
		CustomScopeConfigurer configurer = new CustomScopeConfigurer();
		configurer.addScope("thread", new SimpleThreadScope()); // WARNING: Leaks memory. Prefer 'request' scope or read here: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/support/SimpleThreadScope.html 
		return configurer;
	}
	
	public static void main(String[] args) {
		SpringApplication.run(SingletonSpringApp.class);
	}
	
	@Autowired 
	private OrderExporter exporter;
	
	// TODO [1] make singleton; test multi-thread: state is [ | | | ]
	// TODO [2] instantiate manually, set dependencies, pass around; no AOP
	// TODO [3] prototype scope + ObjectFactory or @Lookup. Did you said "Factory"? ...
	// TODO [4] thread/request scope. HOW it works?! Leaks: @see SimpleThreadScope javadoc
	// TODO [5] (after AOP): RequestContext, @Cacheable. on thread?! @ThreadLocal
	public void run(String... args) throws Exception {
		new Thread(() -> exporter.export(Locale.ENGLISH)).start();
		new Thread(() -> exporter.export(Locale.FRENCH)).start();
	}
}

@Slf4j
@Service
class OrderExporter  {
	@Autowired
	private InvoiceExporter invoiceExporter;
	@Autowired
	private LabelService labelService;

	public void export(Locale locale) {
	    log.debug("Oare cu cine vorbesc ? " + labelService.getClass());
		labelService.load(locale);
		log.debug("Running export in " + locale);
		log.debug("Origin Country: " + labelService.getCountryName("rO")); 
		invoiceExporter.exportInvoice();
	}
}

@Slf4j
@Service 
class InvoiceExporter {
    @Autowired
    private LabelService labelService;
	public void exportInvoice() {
		log.debug("Invoice Country: " + labelService.getCountryName("ES"));
	}
}

@Slf4j
@Service
@Scope(scopeName = "thread", proxyMode = ScopedProxyMode.TARGET_CLASS)
class LabelService {
	private CountryRepo countryRepo;
	
	public LabelService(CountryRepo countryRepo) {
		this.countryRepo = countryRepo;
	}

	private Map<String, String> countryNames;
	
	public void load(Locale locale) {
		log.debug("load() in instance: " + this.hashCode());
		countryNames = countryRepo.loadCountryNamesAsMap(locale);
	}
	
	public String getCountryName(String iso2Code) {
		log.debug("getCountryName() in instance: " + this.hashCode());
		return countryNames.get(iso2Code.toUpperCase());
	}
}

class A {
    String a,b,c,d;

    public String getA() {
        return a;
    }

    static {
        new A()
                .setA("a")
                .setB("b");
    }

    public A setA(String a) {
        this.a = a;
        return this;
    }

    public String getB() {
        return b;
    }

    public A setB(String b) {
        this.b = b;
        return this;
    }

    public String getC() {
        return c;
    }

    public A setC(String c) {
        this.c = c;
        return this;
    }

    public String getD() {
        return d;
    }

    public A setD(String d) {
        this.d = d;
        return this;
    }
}