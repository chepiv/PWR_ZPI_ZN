package com.zpi.bmarket.bmarket.repositories;

import com.zpi.bmarket.bmarket.domain.Book;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by chepiv on 08/04/2019.
 * Contact: chepurin.ivan@gmail.com
 * Github:chepiv
 */
public interface BookRepository extends CrudRepository<Book,Long> {
    
}
