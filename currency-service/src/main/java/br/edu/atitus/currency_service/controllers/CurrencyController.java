package br.edu.atitus.currency_service.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.edu.atitus.currency_service.entities.CurrencyEntity;
import br.edu.atitus.currency_service.repositories.CurrencyRepository;
import br.edu.atitus.currency_service.clients.CurrencyBCClient;
import br.edu.atitus.currency_service.dto.CurrencyBCResponse;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("currency")
public class CurrencyController {

    private final CurrencyRepository repository;
    private final CurrencyBCClient currencyBCClient;

    public CurrencyController(CurrencyRepository repository, CurrencyBCClient currencyBCClient) {
        this.repository = repository;
        this.currencyBCClient = currencyBCClient;
    }

    @Value("${server.port}")
    private int serverPort;

    @GetMapping("/{value}/{source}/{target}")
    public ResponseEntity<CurrencyEntity> getCurrency(
            @PathVariable double value,
            @PathVariable String source,
            @PathVariable String target) throws Exception {
        CurrencyEntity currency = repository.findBySourceAndTarget(source, target)
                .orElseThrow(() -> new Exception("Currency not supported!!!"));

        currency.setConvertedValue(value * currency.getConversionRate());
        currency.setEnviroment("Currency-Service running on port: " + serverPort);

        return ResponseEntity.ok(currency);
    }

    // 🔥 Endpoint com integração dinâmica com o Banco Central
    @GetMapping("/cotacao-bcb/{moeda}")
    public ResponseEntity<Double> getCotacaoBCB(@PathVariable String moeda) {
        String dataCotacao = getLastAvailableBusinessDay();

        // ⚠️ Correção: agora passamos também o $format=json
        CurrencyBCResponse response = currencyBCClient.getCotacaoMoedaDia(moeda.toUpperCase(), dataCotacao, "json");

        if (response.getValue() == null || response.getValue().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        double cotacao = response.getValue().get(0).getCotacaoVenda();
        return ResponseEntity.ok(cotacao);
    }

    // 🔍 Utilitário: retorna o último dia útil no formato MM-dd-yyyy
    private String getLastAvailableBusinessDay() {
        LocalDate data = LocalDate.now();

        while (data.getDayOfWeek() == DayOfWeek.SATURDAY || data.getDayOfWeek() == DayOfWeek.SUNDAY) {
            data = data.minusDays(1);
        }

        return data.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
    }
}
