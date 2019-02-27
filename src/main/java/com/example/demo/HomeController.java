package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
public class HomeController {

    @Autowired
    Validator validator;

    @GetMapping("/")
    public String loadForm(Model model){
        model.addAttribute("user", new User());
        return "login";
    }


    @RequestMapping("/processform")
    public String processForm(@Valid User user, BindingResult result){
        if(result.hasErrors()){
            return "login";
        }
        return "home";
    }

    public String loadPage(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               Model model){
        model.addAttribute("usernameval", username);
        model.addAttribute("passwordval", password);
        if (validator.validatePassword(password)){
            return "home";
        } else {
            return "redirect:/";
        }
    }
}
