package com.expense.reimbursement.controller;

import com.expense.reimbursement.model.Employee;
import com.expense.reimbursement.service.EmployeeService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class AuthController {
    
    @Autowired
    private EmployeeService employeeService;
    
    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("employee", new Employee());
        return "login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, 
                       HttpSession session, Model model) {
        Optional<Employee> employeeOpt = employeeService.getEmployeeByEmail(email);
        
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            if (employeeService.validatePassword(password, employee.getPassword())) {
                session.setAttribute("loggedInUser", employee);
                if (employee.getRole() == Employee.Role.MANAGER) {
                    return "redirect:/manager/dashboard";
                } else {
                    return "redirect:/employee/dashboard";
                }
            }
        }
        
        model.addAttribute("error", "Invalid email or password");
        model.addAttribute("employee", new Employee());
        return "login";
    }
    
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("employee", new Employee());
        return "register";
    }
    
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute Employee employee, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        
        if (employeeService.emailExists(employee.getEmail())) {
            model.addAttribute("error", "Email already exists");
            return "register";
        }
        
        employeeService.saveEmployee(employee);
        model.addAttribute("success", "Registration successful! Please login.");
        model.addAttribute("employee", new Employee());
        return "login";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}