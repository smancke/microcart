package net.mancke.microcart;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import net.mancke.microcart.voucher.VoucherService;
import de.thomaskrille.dropwizard.environment_configuration.EnvironmentConfigurationFactoryFactory;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

/**
 * @author smancke
 */
public class MicrocartFrontServer extends Application<FrontConfiguration> {

	@Override
	public String getName() {
	      return "Microcart Front Server";
	}
	
    /**
     * startup with the parameter java USer server config.yaml.
     * 
     * @param args the commandline args
     */
    public static void main(final String[] args) {
        try {
            new MicrocartFrontServer().run(args);
        } catch (Exception e) {
            System.out.println("Error while startup"); // NOSONAR
            e.printStackTrace(); // NOSONAR
            System.exit(1);
        }
    }

    /**
     * Initialisation of the service.
     * 
     * @param bootstrap The Dropwizard Bootstrap Instance
     */
    @Override
    public void initialize(final Bootstrap<FrontConfiguration> bootstrap) {
    	 bootstrap.addBundle(new ViewBundle());
    	 bootstrap.setConfigurationFactoryFactory(new EnvironmentConfigurationFactoryFactory<FrontConfiguration>());
    }

    /**
     * The run method does the essential service configuration. Here we add all
     * Resources, Management Classes and HealthChecks.
     * 
     * @param configuration the configuration
     * @param environment the dropwizard environment
     */
    @Override
    public void run(final FrontConfiguration configuration,
            final Environment environment) throws Exception {

    	environment.servlets()
    		.addFilter("trackingIdFilter", new TrackingIdFilter())
    		.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
    	
        // Our Resources
    	VoucherService voucherService = new VoucherService(configuration);
    	CartService cartService = new CartService(configuration, voucherService);
    	environment.jersey().register(new CartResource(configuration, cartService, voucherService));
        environment.jersey().register(new OrderResource(configuration, cartService));
        environment.jersey().register(new DownloadResource(configuration, cartService));
    	environment.jersey().register(voucherService);
    	
        // An example HealthCheck
        environment.healthChecks().register("demo health", new Health());       
    }
}
