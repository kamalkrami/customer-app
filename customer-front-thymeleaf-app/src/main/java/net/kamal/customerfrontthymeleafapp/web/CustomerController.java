package net.kamal.customerfrontthymeleafapp.web;

import net.kamal.customerfrontthymeleafapp.entites.Customer;
import net.kamal.customerfrontthymeleafapp.repository.CustomerRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Console;
import java.util.List;

@Controller
public class CustomerController {
    private CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/customers")
    public String customers(Model model) {
        List<Customer> customerList = customerRepository.findAll();
        model.addAttribute("customers", customerList);
        return "customers";
    }

    @GetMapping("/products")
    public String products(Model model) {
        return "products";
    }

    @GetMapping("/auth")
    @ResponseBody
    // N.B : import org.springframework.security.core.Authentication;
    public Authentication authentication(Authentication authentication){
        return authentication;
    }

}
