package com.cydeo.client;

import com.cydeo.dto.CurrencyDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(url = "https://cdn.jsdelivr.net/gh/fawazahmed0/",
        name = "CURRENCY-CLIENT")
public interface CurrencyClient {

@GetMapping("/currency-api@1/latest/currencies/usd.json")
    CurrencyDto getExchangeRates ();


}
