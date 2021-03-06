package com.zpi.bmarket.bmarket.controllers;

import com.zpi.bmarket.bmarket.DTO.AddOfferDTO;
import com.zpi.bmarket.bmarket.PostStatus;
import com.zpi.bmarket.bmarket.domain.Offer;
import com.zpi.bmarket.bmarket.domain.User;
import com.zpi.bmarket.bmarket.repositories.*;
import com.zpi.bmarket.bmarket.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpSession;
import java.util.Date;
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
        User user = UsersService.getUser(session,userRepository);
        if(user == null)
            return "redirect:/login";

        model.addAttribute("addOfferDTO", addOfferDTO);
        model.addAttribute("offerTypes", offerTypeRepository.findAll());
        model.addAttribute("userBooks", user.getBooks());

        return "addOfferView";
    }


    @RequestMapping(value = "/postAddOffer", method = RequestMethod.POST)
    public String postAddOffer(@ModelAttribute AddOfferDTO offerDTO, Model model, HttpSession session) {

        PostStatus status = PostStatus.ERROR;
        User user = UsersService.getUser(session,userRepository);
        if(user == null)
            return "redirect:/login";
        Long statusId = 1L;
        offerDTO.setStatus(statusRepository.findById(statusId).orElseThrow(() -> new IllegalArgumentException("id: " + statusId)));  // TODO: jakie statusy - ustawić że oferta jest aktywna
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

        return "redirect:/userAccount";
    }

    @RequestMapping(value = "/offerView/{id}",method = RequestMethod.GET)
    public String viewOffer(Model model, @PathVariable("id")long id){

        model.addAttribute("offer",offerRepository.findOfferById(id));
        return "offerView";
    }

    @GetMapping(value = "/editOffer")
    public String editOffer(Model model, HttpSession session, long id) {

        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid offer Id:" + id));

        User user = UsersService.getUser(session,userRepository);
        if(user == null)
            return "redirect:/login";

        model.addAttribute("offer", offer);
        model.addAttribute("userBooks", user.getBooks());
        model.addAttribute("offerTypes", offerTypeRepository.findAll());

        return "editOfferView";
    }


    @RequestMapping(value = "/postEditOffer", method = RequestMethod.POST)
    public String postEditOffer( Model model, HttpSession session, @ModelAttribute Offer offer) {

        PostStatus status = PostStatus.ERROR;

        try {
            offerRepository.save(offer);
        }
        catch (Exception e) {
            status = PostStatus.DATABASE_ERROR;
        }

        model.addAttribute("status", status);

        return "postEditOfferView";
    }


    @GetMapping(value = "/deleteOffer")
    public String deleteOffer(Model model, HttpSession session, Long id) {

        offerRepository.deleteById(id);

        return "listOfferView";
    }
}


