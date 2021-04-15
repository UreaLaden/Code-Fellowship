package com.urealaden.CodeFellowShip.ApplicationControllers;


import com.urealaden.CodeFellowShip.Application.ApplicationUser;
import com.urealaden.CodeFellowShip.Application.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
public class ApplicationUserController {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    ApplicationUserRepository applicationUserRepository;
    @PostMapping("/appUser")
    public RedirectView createUser(String username, String password, HttpServletRequest request){
        password = passwordEncoder.encode(password);
        ApplicationUser appUser = new ApplicationUser();
        System.out.println("password = " + password);
        System.out.println("username = " + username);
        appUser.setPassword(password);
        appUser.setUsername(username);
        appUser.setBio("");
        appUser.setFirstName("");
        appUser.setLastName("");
        appUser.setDateOfBirth("");

        try{
            applicationUserRepository.save(appUser);
        }catch(Exception e){
            return new RedirectView("/?username=duplicate");
            //TODO: Tell the user what went wrong
        }

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username,password);
        authToken.setDetails(new WebAuthenticationDetails(request));
        Authentication authentication = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new RedirectView("/");
    }

    @GetMapping("/login")
    public String showLoginPage(){
        return "sign-up";
    }

    @GetMapping("/appUser/{id}")
    public String showSingleUser(@PathVariable long id, Model m, Principal p){
        ApplicationUser appUser = applicationUserRepository.findByUsername(p.getName());
        m.addAttribute("appUser",appUser);
        if(p != null)
        {
            ApplicationUser visitor = applicationUserRepository.findByUsername(p.getName());
            if(!visitor.getUsername().equals(appUser.getUsername())){
                m.addAttribute("visitor",visitor);
            }
        }
        else
        {
            ApplicationUser visitor = new ApplicationUser();
            visitor.setUsername("Guest");
            m.addAttribute("visitor",visitor);
        }
        return"application-user";
    }

    @GetMapping("/application-user")
    public String showUserDetails(Principal p, Model m){
        if(p != null){
            System.out.println("p.getName() = " + p.getName());
        }
        return "application-user";
    }
}
