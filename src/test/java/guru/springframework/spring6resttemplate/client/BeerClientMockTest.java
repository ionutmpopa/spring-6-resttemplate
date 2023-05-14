package guru.springframework.spring6resttemplate.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6resttemplate.config.RestTemplateBuilderConfig;
import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerDTOPageImpl;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest
@Import(RestTemplateBuilderConfig.class)
class BeerClientMockTest {

    static final String URL = "http://localhost:8080";

    BeerClient beerClient;

    MockRestServiceServer server;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    BeerDTO beerDTO;
    String payload;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        server = MockRestServiceServer.bindTo(restTemplate).build();
        beerClient = new BeerClientImpl(restTemplate);

        beerDTO = getBeerDto();
        payload = objectMapper.writeValueAsString(beerDTO);
    }

    @Test
    void listBeers() throws JsonProcessingException {

        String payload = objectMapper.writeValueAsString(getPage());

        server.expect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andExpect(requestTo(URL + BeerClientImpl.GET_BEER_PATH))
            .andExpect(header("Authorization", "Basic dXNlcjE6cGFzc3dvcmQ="))
            .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));

        Page<BeerDTO> dtos = beerClient.listBeers();
        Assertions.assertThat(dtos.getContent()).isNotEmpty();
    }

    @Test
    void listBeersByName() throws JsonProcessingException {

        String payload = objectMapper.writeValueAsString(getPage());

        URI uri = UriComponentsBuilder.fromHttpUrl(URL + BeerClientImpl.GET_BEER_PATH)
                .queryParam("beerName", "No-Name-Beer")
                    .build().toUri();

        server.expect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andExpect(requestTo(uri))
            .andExpect(header("Authorization", "Basic dXNlcjE6cGFzc3dvcmQ="))
            .andExpect(queryParam("beerName", "No-Name-Beer"))
            .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));

        Page<BeerDTO> dtos = beerClient.listBeers("No-Name-Beer");
        Assertions.assertThat(dtos.getContent()).isNotEmpty();
    }

    @Test
    void getBeerById() {
        server.expect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andExpect(requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH, beerDTO.getId()))
            .andExpect(header("Authorization", "Basic dXNlcjE6cGFzc3dvcmQ="))
            .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));

        BeerDTO beerDTORetrieved = beerClient.getBeerById(beerDTO.getId());
        Assertions.assertThat(beerDTORetrieved.getId()).isEqualTo(beerDTO.getId());
    }

    @Test
    void createBeer() throws JsonProcessingException {

        URI uri = UriComponentsBuilder.fromPath(BeerClientImpl.GET_BEER_BY_ID_PATH)
                .build(beerDTO.getId());

        server.expect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andExpect(requestTo(URL + BeerClientImpl.GET_BEER_PATH))
            .andExpect(header("Authorization", "Basic dXNlcjE6cGFzc3dvcmQ="))
            .andRespond(withSuccess().location(uri));

        server.expect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andExpect(requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH, beerDTO.getId()))
            .andExpect(header("Authorization", "Basic dXNlcjE6cGFzc3dvcmQ="))
            .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));

        BeerDTO responseDto = beerClient.createBeer(beerDTO);
        Assertions.assertThat(responseDto.getId()).isEqualTo(beerDTO.getId());

    }

    @Test
    void updateBeer() {
        server.expect(MockRestRequestMatchers.method(HttpMethod.PUT))
            .andExpect(requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH, beerDTO.getId()))
            .andExpect(header("Authorization", "Basic dXNlcjE6cGFzc3dvcmQ="))
            .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));

        BeerDTO responseDto = beerClient.updateBeer(beerDTO.getId(), beerDTO);
        Assertions.assertThat(responseDto.getId()).isEqualTo(beerDTO.getId());

    }

    @Test
    void deleteBeer() {
        server.expect(MockRestRequestMatchers.method(HttpMethod.DELETE))
            .andExpect(requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH, beerDTO.getId()))
            .andExpect(header("Authorization", "Basic dXNlcjE6cGFzc3dvcmQ="))
            .andRespond(withNoContent());

        beerClient.deleteBeer(beerDTO.getId());
        server.verify();
    }

    @Test
    void deleteBeerNotFound() {
        server.expect(MockRestRequestMatchers.method(HttpMethod.DELETE))
            .andExpect(requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH, beerDTO.getId()))
            .andExpect(header("Authorization", "Basic dXNlcjE6cGFzc3dvcmQ="))
            .andRespond(withResourceNotFound());
        assertThrows(HttpClientErrorException.class, () -> {
            beerClient.deleteBeer(beerDTO.getId());
            server.verify();
        });
    }

    BeerDTO getBeerDto() {
        return BeerDTO.builder()
            .id(UUID.randomUUID())
            .price(new BigDecimal("11.12"))
            .beerName("No-Name-Beer")
            .beerStyle(BeerStyle.ALE)
            .quantityOnHand(333)
            .upc("123456789")
            .build();
    }

    BeerDTOPageImpl getPage() {
        return new BeerDTOPageImpl(Arrays.asList(getBeerDto()), 1, 25, 1);
    }

}
