package com.techstoresecureapp.dto;

import com.techstoresecureapp.entity.Category;

public class CategoryResponse {

    private Long id;
    private String name;
    private String description;
    private Boolean active;

    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.description = category.getDescription();
        this.active = category.getActive();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getActive() {
        return active;
    }
}
