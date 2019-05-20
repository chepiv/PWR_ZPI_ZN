package com.zpi.bmarket.bmarket.controllers;

import com.zpi.bmarket.bmarket.DTO.AddOfferDTO;
import com.zpi.bmarket.bmarket.PostStatus;
import com.zpi.bmarket.bmarket.domain.Book;
import com.zpi.bmarket.bmarket.domain.Offer;
import com.zpi.bmarket.bmarket.domain.User;
import com.zpi.bmarket.bmarket.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class OfferController {

    private static Logger logger = Logger.getLogger(OfferController.class.getName());

    @Autowired
    OfferRepository offerRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    OfferTypeRepository offerTypeRepository;
    @Autowired
    StatusRepository statusRepository;
    @Autowired
    BookRepository bookRepository;


    @GetMapping(value = "/addOffer")
    public String addOffer(WebRequest request, Model model, HttpSession session) {

        AddOfferDTO addOfferDTO = new AddOfferDTO();
        Long id = ((Long) session.getAttribute("userId")).longValue();
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("id: " + id));
//        List<Book> books = bookRepository.findAllByUserId(id);

        model.addAttribute("addOfferDTO", addOfferDTO);
        model.addAttribute("offerTypes", offerTypeRepository.findAll());
        model.addAttribute("userBooks", user.getBooks());

        return "addOfferView";
    }


    @RequestMapping(value = "/postAddOffer", method = RequestMethod.POST)
    public String postAddOffer(@ModelAttribute AddOfferDTO offerDTO, Model model, HttpSession session) {

        PostStatus status = PostStatus.ERROR;
        Long id = (Long) session.getAttribute("userId");
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("id: " + id));

        offerDTO.setStatus(statusRepository.findById(0L));  // TODO: jakie statusy - ustawić że oferta jest aktywna
        offerDTO.setPublishDate(new Date());

        Offer offer = offerDTO.getOffer(user);

        try {
            offerRepository.save(offer);
            model.addAttribute("offer", offer);
            status = PostStatus.SUCCESS;
        } catch (Exception e) {
            status = PostStatus.DATABASE_ERROR;
            logger.log(Level.WARNING, "Database Error", e);
        }

        model.addAttribute("status", status);

        return "postAddOfferView";
    }
}
