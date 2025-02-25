package kr.ac.dankook.VettAdminServer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @RequestMapping("")
    public String index() {
        return "redirect:/admin/main";
    }

    @RequestMapping("/main")
    public String mainPage() {
        return "page/main.html";
    }

    @RequestMapping("/change-password")
    public String changePasswordPage(){
        return "page/auth/changePassword.html";
    }
}
