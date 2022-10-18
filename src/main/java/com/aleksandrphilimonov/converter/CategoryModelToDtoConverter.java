package com.aleksandrphilimonov.converter;

import s03.dao.CategoryModel;
import s03.service.CategoryDTO;

public class CategoryModelToDtoConverter implements Converter<CategoryModel, CategoryDTO> {
    @Override
    public CategoryDTO convert(CategoryModel source) {

        CategoryDTO categoryDTO = new CategoryDTO();

        categoryDTO.setId(source.getId());
        categoryDTO.setName(source.getName());
        categoryDTO.setUser_id(source.getUser_id());

        return categoryDTO;
    }
}
