package com.cydeo.converter;

import com.cydeo.dto.RoleDto;
import com.cydeo.service.RoleService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component

public class RoleDtoConverter implements Converter<String, RoleDto> {

    RoleService roleService;

    public RoleDtoConverter( RoleService roleService){
        this.roleService=roleService;
    }

    @Override
    public RoleDto convert(String source) {

        if (source==null || source.equals("") ){
            return null;
        }

        return roleService.findById(Long.parseLong(source));
    }
}
