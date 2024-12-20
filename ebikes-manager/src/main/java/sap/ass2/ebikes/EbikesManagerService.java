package sap.ass2.ebikes;

import java.net.URL;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.KafkaProducer;

import sap.ass2.ebikes.application.CustomKafkaListener;
import sap.ass2.ebikes.application.EbikesManagerAPI;
import sap.ass2.ebikes.application.EbikesManagerImpl;
import sap.ass2.ebikes.application.EbikesRepositoryImpl;
import sap.ass2.ebikes.application.KafkaConsumerConfig;
import sap.ass2.ebikes.application.KafkaProducerConfig;
import sap.ass2.ebikes.domain.RepositoryException;
import sap.ass2.ebikes.infrastructure.EbikesManagerController;

public class EbikesManagerService {
    private EbikesManagerAPI ebikesManager;
    private EbikesManagerController ebikesController;
    private URL localAddress;
    private CustomKafkaListener listener;

    public EbikesManagerService(URL localAddress) throws RepositoryException{
        this.localAddress = localAddress;
        this.listener = new CustomKafkaListener("ebike-events", KafkaConsumerConfig.defaultConsumer());
        this.ebikesManager = new EbikesManagerImpl(new EbikesRepositoryImpl(listener), KafkaProducerConfig.kafkaProducer());
    }

    public void launch(){
        // Starts the ebike controller, so that it can starts the service verticle.
        this.ebikesController = new EbikesManagerController(this.localAddress.getPort());
        this.ebikesController.init(this.ebikesManager, this.listener);
        CompletableFuture.runAsync(this.listener);
    }
}
