package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerDTOPageImpl;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

/**
 * Created by jt, Spring Framework Guru.
 */
@RequiredArgsConstructor
@Service
public class BeerClientImpl implements BeerClient {

    private final RestTemplate restTemplate;

    public static final String GET_BEER_PATH = "/api/v1/beer";
    public static final String GET_BEER_BY_ID_PATH = "/api/v1/beer/{beerId}";

    private static final String POST_BEER_PATH = "/api/v1/beer";

    @Override
    public Page<BeerDTO> listBeers() {
        return getBeerDTOPage(GET_BEER_PATH, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()),
            null, null, null, null, null);
    }

    @Override
    public Page<BeerDTO> listBeers(String beerName) {
        return getBeerDTOPage(GET_BEER_PATH, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()),
            beerName, null, null, null, null);
    }

    @Override
    public Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle) {
        return getBeerDTOPage(GET_BEER_PATH, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()),
            beerName, beerStyle, null, null, null);
    }

    @Override
    public Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory) {
        return getBeerDTOPage(GET_BEER_PATH, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()),
            beerName, beerStyle, showInventory, null, null);
    }

    @Override
    public Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageSize) {
        return getBeerDTOPage(GET_BEER_PATH, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()),
            beerName, beerStyle, showInventory, null, pageSize);
    }

    @Override
    public Page<BeerDTO> listBeers(final String beerName, final BeerStyle beerStyle, final Boolean showInventory,
                                   final Integer pageNumber, final Integer pageSize) {

        return getBeerDTOPage(GET_BEER_PATH, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()),
            beerName, beerStyle, showInventory, pageNumber, pageSize);
    }

    @Override
    public BeerDTO getBeerById(UUID beerId) {
        return restTemplate.getForObject(GET_BEER_BY_ID_PATH, BeerDTO.class, beerId);
    }

    @Override
    public BeerDTO createBeer(BeerDTO newBeer) {
        URI uri = restTemplate.postForLocation(POST_BEER_PATH, newBeer);
        assert uri != null;
        return restTemplate.getForObject(uri.getPath(), BeerDTO.class);
    }

    @Override
    public BeerDTO updateBeer(UUID beerId, BeerDTO beerDTO) {
        HttpEntity<BeerDTO> httpEntity = new HttpEntity<>(beerDTO, new HttpHeaders());
        return restTemplate.exchange(GET_BEER_BY_ID_PATH, HttpMethod.PUT, httpEntity, BeerDTO.class, beerId).getBody();
    }

    @Override
    public void deleteBeer(UUID id) {
        restTemplate.delete(GET_BEER_BY_ID_PATH, id);
    }

    private BeerDTOPageImpl getBeerDTOPage(String path, HttpMethod httpMethod, HttpEntity<?> httpEntity,
                                           String beerName, BeerStyle beerStyle, Boolean showInventory,
                                           Integer pageNumber, Integer pageSize, Object... uriVariables) {
        UriComponentsBuilder uriComponentsBuilder = getUriComponentsBuilder(path, beerName, beerStyle,
            showInventory, pageNumber, pageSize);


        ResponseEntity<BeerDTOPageImpl> response =
                restTemplate.exchange(uriComponentsBuilder.toUriString(), httpMethod, httpEntity,
                    BeerDTOPageImpl.class, uriVariables);

        return response.getBody();
    }

    private UriComponentsBuilder getUriComponentsBuilder(String path, String beerName, BeerStyle beerStyle,
                                                         Boolean showInventory, Integer pageNumber, Integer pageSize) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath(path);

        if (beerName != null) {
            uriComponentsBuilder.queryParam("beerName", beerName);
        }
        if (beerStyle != null) {
            uriComponentsBuilder.queryParam("beerStyle", beerName);
        }
        if (showInventory != null) {
            uriComponentsBuilder.queryParam("showInventory", showInventory);
        }
        if (pageNumber != null) {
            uriComponentsBuilder.queryParam("pageNumber", pageNumber);
        }
        if (pageSize != null) {
            uriComponentsBuilder.queryParam("pageSize", pageSize);
        }
        return uriComponentsBuilder;
    }
}
