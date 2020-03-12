package com.ts.graphql.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.ts.graphql.model.Book;
import com.ts.graphql.service.BMSService;

@Component
public class BMSMutation implements GraphQLMutationResolver {
    private static final Logger LOGGER = LogManager.getLogger(BMSMutation.class);

    @Autowired
    BMSService service;

    SimpleDateFormat sdf;
    public BMSMutation(){
        sdf = new SimpleDateFormat("yyyy-MM-dd");
    }
    public Boolean save( String isbn, String title,String pubYear, int price) throws ParseException {
        Book book = new Book();
        book.setIsbn(isbn);
        book.setTitle(title);
        book.setPrice(price);
        try {
            service.save(book);
            return true;
        }catch (Exception e){
            LOGGER.error(e);
            return false;
        }
    }
}
