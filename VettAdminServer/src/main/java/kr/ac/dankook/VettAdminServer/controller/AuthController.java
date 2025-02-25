package kr.ac.dankook.VettAdminServer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/auth")
public class AuthController {

    @RequestMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false, defaultValue = "false") boolean error,
                            Model model){
        if (error) {
            model.addAttribute("errorMessage", "로그인하신 회원정보가 없습니다.");
        }
        return "page/auth/login.html";
    }
    @RequestMapping("/except")
    public String exceptPage(RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("errorMessage","접근 권한이 없습니다.");
        return "redirect:/admin/auth/login";
    }

}
