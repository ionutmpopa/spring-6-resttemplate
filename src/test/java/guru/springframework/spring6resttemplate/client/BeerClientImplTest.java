package guru.springframework.spring6resttemplate.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BeerClientImplTest {

    @Autowired
    BeerClientImpl beerClient;

    @Test
    void listBeersWithoutArgument() {

        beerClient.listBeers();
    }

    @Test
    void listBeersByName() {

        beerClient.listBeers("ALE", null, null, null, null);
    }

    @Test
    void listBeersByPageSize() {

        beerClient.listBeers(null, null, null, null, 1000);
    }
}
