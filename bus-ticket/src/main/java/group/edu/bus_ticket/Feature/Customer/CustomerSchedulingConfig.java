package group.edu.bus_ticket.Feature.Customer;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Bat co che @Scheduled cho cac job phia Customer (vi du: giai phong ghe qua han).
 * Dat trong package Customer de khong dung cham file khoi dong dung chung.
 */
@Configuration
@EnableScheduling
public class CustomerSchedulingConfig {
}
