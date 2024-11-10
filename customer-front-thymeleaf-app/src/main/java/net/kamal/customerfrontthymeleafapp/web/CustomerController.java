package net.kamal.customerfrontthymeleafapp.web;

import net.kamal.customerfrontthymeleafapp.entites.Customer;
import net.kamal.customerfrontthymeleafapp.repository.CustomerRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CustomerController {
    private CustomerRepository customerRepository;
    private ClientRegistrationRepository clientRegistrationRepository;

    public CustomerController(CustomerRepository customerRepository, ClientRegistrationRepository clientRegistrationRepository) {
        this.customerRepository = customerRepository;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/notAuthorized")
    public String notAuthorized() {
        return "notAuthorized";
    }

    @GetMapping("/loginpage")
    // Fetching all the client login types that we have and storing them in a HashMap to pass to the Thymeleaf view
    public String loginpage(Model model) {
        String authorizationRequestBaseUri = "oauth2/authorization";
        Map<String, String> oauth2AuthenticationUrls = new HashMap<>();
        Iterable<ClientRegistration> clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
        clientRegistrations.forEach(registration ->{
            oauth2AuthenticationUrls.put(registration.getClientName(),
                    authorizationRequestBaseUri + "/" + registration.getRegistrationId());
        });
        model.addAttribute("urls", oauth2AuthenticationUrls);
        return "loginpage";
    }

    @GetMapping("/customers")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
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
