package guru.springframework.spring6resttemplate.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import org.springframework.data.domain.Page;

import java.util.UUID;

/**
 * Created by jt, Spring Framework Guru.
 */
public interface BeerClient {

    Page<BeerDTO> listBeers();

    Page<BeerDTO> listBeers(final String beerName);

    Page<BeerDTO> listBeers(final String beerName, final BeerStyle beerStyle);

    Page<BeerDTO> listBeers(final String beerName, final BeerStyle beerStyle, final Boolean showInventory);

    Page<BeerDTO> listBeers(final String beerName, final BeerStyle beerStyle, final Boolean showInventory,
                            final Integer pageSize);


    Page<BeerDTO> listBeers(final String beerName, final BeerStyle beerStyle, final Boolean showInventory,
                            final Integer pageNumber, final Integer pageSize);

    BeerDTO getBeerById(UUID beerId);

    BeerDTO createBeer(BeerDTO newBeer) throws JsonProcessingException;

    BeerDTO updateBeer(UUID beerId, BeerDTO newBeer);

    void deleteBeer(UUID id);
}
