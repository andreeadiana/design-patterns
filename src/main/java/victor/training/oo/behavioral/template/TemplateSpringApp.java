package victor.training.oo.behavioral.template;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.Data;
import org.springframework.stereotype.Service;

@SpringBootApplication
public class TemplateSpringApp implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(TemplateSpringApp.class, args);
    }

    @Autowired
    private EmailSenderService service;

    @Autowired
    private EmailTemplates templates;

    public void run(String... args) {
        service.sendEmail("a@b.com", templates::writeOrderReceivedEmail);
        service.sendEmail("a@b.com", templates::writeOrderShippedEmail);
    }
}

@Service
class EmailSenderService {

    public interface EmailContentWriter {
        void write(Email email);
    }
    public void sendEmail(String emailAddress, EmailContentWriter emailWriter) {
        EmailContext context = new EmailContext(/*smtpConfig,etc*/);
        int MAX_RETRIES = 3;
        for (int i = 0; i < MAX_RETRIES; i++) {
            Email email = new Email(); // constructor generates new unique ID
            email.setSender("noreply@corp.com");
            email.setReplyTo("/dev/null");
            email.setTo(emailAddress);
            emailWriter.write(email);
            boolean success = context.send(email);
            if (success) break;
        }
    }

}


@Service
class EmailTemplates {
    public void writeOrderReceivedEmail(Email email) {
        email.setSubject("Order Received");
        email.setBody("Thank you for your order");
    }

    public void writeOrderShippedEmail(Email email) {
        email.setSubject("Order Shipped");
        email.setBody("Ti-am trimas! Speram s-ajunga (de data asta)!");
    }
}

class EmailContext {
    public boolean send(Email email) {
        System.out.println("Trying to send " + email);
        return new Random(System.nanoTime()).nextBoolean();
    }
}

@Data
class Email {
    private final long id = new Random(System.nanoTime()).nextLong();
    private String sender;
    private String subject;
    private String body;
    private String replyTo;
    private String to;
}