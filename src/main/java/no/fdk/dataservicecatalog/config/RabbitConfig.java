package no.fdk.dataservicecatalog.config;

import com.rabbitmq.client.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.rabbitmq.*;

import static reactor.rabbitmq.BindingSpecification.binding;
import static reactor.rabbitmq.ExchangeSpecification.exchange;
import static reactor.rabbitmq.QueueSpecification.queue;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RabbitConfig {

    @Bean
    Sender sender(RabbitProperties rabbitProperties) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(rabbitProperties.getHost());
        connectionFactory.setPort(rabbitProperties.getPort());
        connectionFactory.setUsername(rabbitProperties.getUsername());
        connectionFactory.setPassword(rabbitProperties.getPassword());

        Sender rabbitFluxSender = RabbitFlux.createSender(new SenderOptions().connectionFactory(connectionFactory));

        QueueSpecification queueSpecification = queue(null).durable(false).exclusive(true).autoDelete(true);

        BindingSpecification bindingSpecification = binding(
                rabbitProperties.getTemplate().getExchange(),
                rabbitProperties.getTemplate().getRoutingKey(),
                "");

        ExchangeSpecification exchangeSpecification = exchange(rabbitProperties.getTemplate().getExchange()).type("topic");

        rabbitFluxSender.declare(exchangeSpecification)
                .then(rabbitFluxSender.declare(queueSpecification))
                .then(rabbitFluxSender.bind(bindingSpecification))
                .subscribe(r -> log.debug(r.toString()));

        return rabbitFluxSender;
    }
}
