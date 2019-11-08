package victor.training.oo.structural.proxy;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@EnableCaching()
@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
public class ProxySpringApp implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(ProxySpringApp.class, args);
	}


	@Autowired
	ExpensiveOps ops;
	// TODO [1] implement decorator
	// TODO [2] apply decorator via Spring
	// TODO [3] generic java.lang.reflect.Proxy
	// TODO [4] Spring aspect
	// TODO [5] Spring cache support
	// TODO [6] Back to singleton (are you still alive?)
	public void run(String... args) throws Exception {
		log.debug("Oare cu cine vorbesc eu aici ? " + ops.getClass());
		log.debug("\n");
		log.debug("---- CPU Intensive ~ memoization?");
		log.debug("10000169 is prime ? ");
		log.debug("Got: " + ops.isPrime(10_000_169) + "\n");
		log.debug("10000169 is prime ? ");
		log.debug("Got: " + ops.isPrime(10_000_169) + "\n");

		log.debug("---- I/O Intensive ~ \"There are only two things hard in programming...\"");
		log.debug("Folder MD5: ");
		log.debug("Got: " + ops.hashAllFiles(new File(".")) + "\n");
		log.debug("Got: " + ops.hashAllFiles(new File(".")) + "\n");

		log.debug("am detectat cu NIO ca s-a schimbat un fisier pe disk");
		ops.omorCacheul(new File("."));

		log.debug("Folder MD5: ");
		log.debug("Got: " + ops.hashAllFiles(new File(".")) + "\n");
	}
}

@Retention(RetentionPolicy.RUNTIME)
@interface LoggedClass {}
@Retention(RetentionPolicy.RUNTIME)
@interface LoggedMethod {}

@Slf4j
@Aspect
@Component
class SRI {
//	@Around("execution(* victor..ExpensiveOps.*(..))")
//	@Around("execution(* *(..)) && @within(victor.training.oo.structural.proxy.LoggedClass)")
	@Around("execution(* *(..)) && @annotation(victor.training.oo.structural.proxy.LoggedMethod)")
	public Object m(ProceedingJoinPoint point) throws Throwable {
		log.debug("Cheama fraeru metoda {} cu param {}",
				point.getSignature().getName(),
				Arrays.toString(point.getArgs()));
		return point.proceed();
	}
}