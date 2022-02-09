package no.fdk.dataservicecatalog.utils;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.fdk.dataservicecatalog.model.DataService;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static no.fdk.dataservicecatalog.utils.ApiTestContext.mongoContainer;
import static no.fdk.dataservicecatalog.utils.TestData.*;

@Slf4j
public class TestUtils {

    public static String authorizedGet(
            String path,
            Integer port,
            String token
    ) {
        RestTemplate request = new RestTemplate();
        request.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        String url = "http://localhost:" + port + path;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = request.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (Exception ex) {
            return "";
        }

    }

    public static void resetDB() {
        ConnectionString connectionString = new ConnectionString("mongodb://" + MONGO_USER + ":" + MONGO_PASSWORD + "@localhost:" + mongoContainer.getMappedPort(MONGO_PORT) + "/?authSource=admin&authMechanism=SCRAM-SHA-1");
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(), CodecRegistries.fromProviders(
                        PojoCodecProvider.builder().automatic(true).build()));

        MongoClient client = MongoClients.create(connectionString);
        MongoDatabase mongoDatabase = client.getDatabase(MONGO_DB_NAME).withCodecRegistry(pojoCodecRegistry);

        MongoCollection<Document> collection = mongoDatabase.getCollection("dataservices");
        val pop = dataServicePopulation();
        Flux.from(collection.deleteMany(new Document()))
                .flatMap(result -> collection.insertMany(pop))
                .subscribe(result -> client.close());
    }

    private static List<org.bson.Document> dataServicePopulation() {
        List<DataService> services0 = createDataServices("123456789");
        List<DataService> services1 = createDataServices("987654321");

        ArrayList<org.bson.Document> mongoDocuments = new ArrayList<>();
        for (DataService service : services0) {
            mongoDocuments.add(mongoDocumentFromDataService(service));
        }
        for (DataService service : services1) {
            mongoDocuments.add(mongoDocumentFromDataService(service));
        }

        return mongoDocuments;
    }

    private static org.bson.Document mongoDocumentFromDataService(DataService ds) {
        return new org.bson.Document()
                .append("_id", ds.getId())
                .append("organizationId", ds.getOrganizationId());
    }

}
