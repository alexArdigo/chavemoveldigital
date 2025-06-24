package pt.gov.chavemoveldigital.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.gov.chavemoveldigital.entities.TempCode;
import pt.gov.chavemoveldigital.models.UserDTO;
import pt.gov.chavemoveldigital.services.AuthService;
import org.springframework.ui.Model;


@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/authentication")
    public String authenticate(UserDTO userDTO, RedirectAttributes redirectAttributes) {
        TempCode tempCode = authService.authenticate(userDTO);
        redirectAttributes.addFlashAttribute("message", "Hello, world!");
        redirectAttributes.addFlashAttribute("code", tempCode.getCode());
        return "redirect:/view";
    }

    @PostMapping("/authentication/code")
    public ResponseEntity<?> insertCode(Long code) {
        return ResponseEntity.ok().body(authService.insertCode(code));
    }
}
