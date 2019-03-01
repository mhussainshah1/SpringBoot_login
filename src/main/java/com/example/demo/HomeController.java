package com.example.demo;

/*
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
    }*//*



}
*/


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class HomeController {

    @GetMapping("/registration")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "registration";
    }

    @PostMapping("/registration")
    public String registerUserAccount(@ModelAttribute("user") @Valid UserRegistrationDto userDto,
                                      BindingResult result) {
        if (result.hasErrors()) {
            return "registration";
        }
        return "redirect:/registration?success";
    }
}
