package com.micropos.products.jpa;

import com.micropos.products.model.Categories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriesRepository  extends JpaRepository<Categories, String> {
}
