package victor.training.oo.behavioral.strategy;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.stereotype.Service;

@SpringBootApplication
public class StrategySpringApp implements CommandLineRunner {
	public static void main(String[] args) {
		new SpringApplicationBuilder(StrategySpringApp.class)
			.profiles("localProps")
			.run(args);
	}

	
	private ConfigProvider configProvider = new ConfigFileProvider(); 
	
	// TODO [1] Break CustomsService logic into Strategies
	// TODO [2] Convert it to Chain Of Responsibility
	// TODO [3] Wire with Spring
	// TODO [4] ConfigProvider: selected based on environment props, with Spring
	public void run(String... args) {
		CustomsService service = new CustomsService();
		System.out.println("Tax for (RO,100,100) = " + service.computeCustomsTax("RO", 100, 100));
		System.out.println("Tax for (CN,100,100) = " + service.computeCustomsTax("CN", 100, 100));
		System.out.println("Tax for (UK,100,100) = " + service.computeCustomsTax("UK", 100, 100));
		
		System.out.println("Property: " + configProvider.getProperties().getProperty("someProp"));
	}
}

class CustomsService {
	public double computeCustomsTax(String originCountry, double tobaccoValue, double regularValue) { // UGLY API we CANNOT change
		TaxComputer computer = selectTaxCalculator(originCountry); 
		return computer.compute(tobaccoValue, regularValue);
	}

	private TaxComputer selectTaxCalculator(String originCountry) {
		List<TaxComputer> toate = asList(
			new BrexitTaxComputer(), 
			new ChinaTaxComputer(),
			new EUTaxComputer());
			
		return toate.stream()
			.filter(computer -> computer.supports(originCountry))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Not a valid country ISO2 code: " + originCountry));
	}
}
interface TaxComputer {
	double compute(double tobaccoValue, double regularValue);
	boolean supports(String originCountry)	;
}
class EUTaxComputer implements TaxComputer {
	public double compute(double tobaccoValue, double regularValue) {
		return tobaccoValue/3;
	}
	public boolean supports(String originCountry) {
		return asList("FR","RO","ES").contains(originCountry);
	}
}
class BrexitTaxComputer implements TaxComputer {
	public double compute(double tobaccoValue, double regularValue) {
		return tobaccoValue/2 + regularValue;
	}
	public boolean supports(String originCountry) {
		return "UK".equals(originCountry);
	}
}
class ChinaTaxComputer implements TaxComputer {
	public double compute(double tobaccoValue, double regularValue) {
		// logica muuuulta 30-100 linii
		return tobaccoValue + regularValue;
	}
	public boolean supports(String originCountry) {
		return "CN".equals(originCountry);
	}
}





