package com.expense.reimbursement.controller;

import com.expense.reimbursement.model.Employee;
import com.expense.reimbursement.model.Expense;
import com.expense.reimbursement.service.ExpenseService;
import com.expense.reimbursement.service.FileUploadService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
    
    @Autowired
    private ExpenseService expenseService;
    
    @Autowired
    private FileUploadService fileUploadService;
    
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Employee employee = (Employee) session.getAttribute("loggedInUser");
        if (employee == null) {
            return "redirect:/login";
        }

        List<Expense> expenses = expenseService.getExpensesByEmployee(employee);

        long approvedCount = expenses.stream()
                .filter(e -> e.getStatus() == Expense.Status.APPROVED)
                .count();

        long rejectedCount = expenses.stream()
                .filter(e -> e.getStatus() == Expense.Status.REJECTED)
                .count();

        long pendingCount = expenses.stream()
                .filter(e -> e.getStatus() == Expense.Status.PENDING)
                .count();

        model.addAttribute("employee", employee);
        model.addAttribute("expenses", expenses);
        model.addAttribute("approvedCount", approvedCount);
        model.addAttribute("rejectedCount", rejectedCount);
        model.addAttribute("pendingCount", pendingCount);

        return "employee/dashboard";
    }

    
    @GetMapping("/expense/new")
    public String newExpenseForm(HttpSession session, Model model) {
        Employee employee = (Employee) session.getAttribute("loggedInUser");
        if (employee == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("expense", new Expense());
        model.addAttribute("categories", Expense.Category.values());
        return "employee/expense-form";
    }
    
    @PostMapping("/expense/new")
    public String submitExpense(@Valid @ModelAttribute Expense expense, BindingResult result,
                               @RequestParam("receiptFile") MultipartFile receiptFile,
                               HttpSession session, Model model) {
        Employee employee = (Employee) session.getAttribute("loggedInUser");
        if (employee == null) {
            return "redirect:/login";
        }
        
        if (result.hasErrors()) {
            model.addAttribute("categories", Expense.Category.values());
            return "employee/expense-form";
        }
        
        try {
            // Handle file upload
            if (!receiptFile.isEmpty()) {
                String filename = fileUploadService.uploadFile(receiptFile);
                expense.setReceiptFileName(receiptFile.getOriginalFilename());
                expense.setReceiptFilePath(filename);
            }
            
            expense.setEmployee(employee);
            expenseService.saveExpense(expense);
            
            return "redirect:/employee/dashboard?success=true";
        } catch (IOException e) {
            model.addAttribute("error", "Failed to upload receipt file");
            model.addAttribute("categories", Expense.Category.values());
            return "employee/expense-form";
        }
    }
    
    @GetMapping("/expense/{id}")
    public String viewExpense(@PathVariable Long id, HttpSession session, Model model) {
        Employee employee = (Employee) session.getAttribute("loggedInUser");
        if (employee == null) {
            return "redirect:/login";
        }
        
        Expense expense = expenseService.getExpenseById(id).orElse(null);
        if (expense == null || !expense.getEmployee().getId().equals(employee.getId())) {
            return "redirect:/employee/dashboard";
        }
        
        model.addAttribute("expense", expense);
        return "employee/expense-detail";
    }
}