package com.cydeo.dto;

import com.cydeo.enums.ClientVendorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientVendorDto {

    private Long id;

    @NotNull(message = "Name is a required field")
    @NotBlank(message = "Client/vendor name is a required field")
    private String clientVendorName;
    @NotNull
    @Pattern(regexp =  "^(\\+\\d{1,2}\\s)?((\\(\\d{3}\\))|(\\d{3}))[\\s.-]\\d{3}[\\s.-]\\d{4}$",
       message = "Phone Number is required field and may be in any valid phone number format.")
    private String phone;
    @Pattern(regexp ="^((http|https)://)[-a-zA-Z0-9@:%._\\+~#?&//=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%._\\+~#?&//=]*)$"
            , message = "Website should have a valid format")
    private String website;
    @NotNull(message = "Please select type")
    private ClientVendorType clientVendorType;


    @Valid
    private AddressDto address;

    private CompanyDto company;

}
