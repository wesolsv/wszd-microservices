package br.com.wszd.cambioservice.controller;

import br.com.wszd.cambioservice.model.Cambio;
import br.com.wszd.cambioservice.repository.CambioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Tag(name = "Cambio Service")
@RestController
@RequestMapping("cambio-service")
public class CambioController {

    private Logger logger = LoggerFactory.getLogger(CambioController.class);

    @Autowired
    private Environment environment;

    @Autowired
    private CambioRepository repository;

    @Operation(description = "Get cambio from currency")
    @GetMapping(value = "/{amount}/{from}/{to}")
    public Cambio getCambio(
            @PathVariable("amount") BigDecimal amount,
            @PathVariable("from") String from,
            @PathVariable("to") String to
    ) {

        logger.info("getCambio foi chamado com -> {},{} e {}",amount, from, to);
        var cambio = repository.findByFromAndTo(from, to);
        if(cambio == null )throw new RuntimeException("Currency Unsuported");

        var port = environment.getProperty("local.server.port");
        BigDecimal conversionFactory = cambio.getConversionFactor();
        BigDecimal convertedValue = conversionFactory.multiply(amount);
        cambio.setEnviroment(port);
        cambio.setConvertedValue(convertedValue.setScale(2, RoundingMode.CEILING));

        return cambio;
    }
}
