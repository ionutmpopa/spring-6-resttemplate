package guru.springframework.spring6resttemplate.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

@SpringBootTest
class BeerClientImplTest {

    @Autowired
    BeerClientImpl beerClient;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void listBeersWithoutArgument() {
        Page<BeerDTO> beerDTOPage = beerClient.listBeers();
        Assertions.assertThat(beerDTOPage.getTotalElements()).isEqualTo(2435L);
        Assertions.assertThat(beerDTOPage.getSize()).isEqualTo(25);
    }

    @Test
    void listBeersByName() {
        Page<BeerDTO> beerDTOPage = beerClient.listBeers("ALE", null, null,
            null, null);
        Assertions.assertThat(beerDTOPage.getTotalElements()).isEqualTo(636);
        Assertions.assertThat(beerDTOPage.getSize()).isEqualTo(25);
    }

    @Test
    void listBeersByPageSize() {
        Page<BeerDTO> beerDTOPage = beerClient.listBeers(null, null, null,
            null, 55);
        Assertions.assertThat(beerDTOPage.getSize()).isEqualTo(55);
    }

    @Test
    void getBeerById() {
        BeerDTO beerDTO = beerClient.listBeers().getContent().get(0);
        BeerDTO beerDTOFromServer = beerClient.getBeerById(beerDTO.getId());
        Assertions.assertThat(beerDTOFromServer).isNotNull();
    }

    @Test
    void createBeer() {

        BeerDTO newBeer = BeerDTO.builder()
            .beerName("Stella")
            .beerStyle(BeerStyle.PILSNER)
            .upc("221345")
            .price(BigDecimal.valueOf(5.75))
            .quantityOnHand(100)
            .build();

        BeerDTO savedBeer = beerClient.createBeer(newBeer);

        Assertions.assertThat(savedBeer).isNotNull();

    }

    @Test
    void updateBeer() {

        BeerDTO beerDTO = beerClient.listBeers().getContent().get(0);
        Assertions.assertThat(beerDTO.getPrice()).isEqualTo(new BigDecimal("10.00"));

        BeerDTO newBeer = BeerDTO.builder()
            .beerName("#666 Golden Amber Lager")
            .beerStyle(beerDTO.getBeerStyle())
            .upc(beerDTO.getUpc())
            .quantityOnHand(333)
            .price(BigDecimal.valueOf(15.22))
            .build();

        BeerDTO updatedBeer = beerClient.updateBeer(beerDTO.getId(), newBeer);
        Assertions.assertThat(updatedBeer.getPrice()).isEqualTo(BigDecimal.valueOf(15.22));

    }

    @Test
    void deleteBeer() {

        BeerDTO beerDTO = beerClient.listBeers().getContent().get(0);

        Assertions.assertThat(beerClient.listBeers().getTotalElements()).isEqualTo(2436);

        beerClient.deleteBeer(beerDTO.getId());
        Assertions.assertThat(beerClient.listBeers().getTotalElements()).isEqualTo(2435);

    }
}
