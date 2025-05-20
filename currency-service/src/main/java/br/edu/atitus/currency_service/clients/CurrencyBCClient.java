package br.edu.atitus.currency_service.clients;

import br.edu.atitus.currency_service.dto.CurrencyBCResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "bancoCentralClient", url = "https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1/odata")
public interface CurrencyBCClient {

    @GetMapping("/CotacaoMoedaDia")
    CurrencyBCResponse getCotacaoMoedaDia(
            @RequestParam("moeda") String moeda,
            @RequestParam("dataCotacao") String dataCotacao,
            @RequestParam("$format") String format
    );
}
