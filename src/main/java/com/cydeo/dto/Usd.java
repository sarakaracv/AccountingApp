package com.cydeo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Usd {

@JsonProperty("cad")
public Double canadianDollar;
@JsonProperty("eur")
public Double euro;
@JsonProperty("gbp")
public Double britishPound;
@JsonProperty("inr")
public Double indianRupee;
@JsonProperty("jpy")
public Double japaneseYen;

}