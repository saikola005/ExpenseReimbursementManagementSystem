package com.expense.reimbursement.controller;

import com.expense.reimbursement.model.Employee;
import com.expense.reimbursement.model.Expense;
import com.expense.reimbursement.service.ExpenseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/manager")
public class ManagerController {
    
    @Autowired
    private ExpenseService expenseService;
    
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Employee employee = (Employee) session.getAttribute("loggedInUser");
        if (employee == null || employee.getRole() != Employee.Role.MANAGER) {
            return "redirect:/login";
        }

        List<Expense> pendingExpenses = expenseService.getPendingExpenses();
        List<Expense> allExpenses = expenseService.getAllExpenses();

        long approvedCount = allExpenses.stream()
                .filter(e -> e.getStatus() == Expense.Status.APPROVED)
                .count();

        long rejectedCount = allExpenses.stream()
                .filter(e -> e.getStatus() == Expense.Status.REJECTED)
                .count();

        model.addAttribute("employee", employee);
        model.addAttribute("pendingExpenses", pendingExpenses);
        model.addAttribute("allExpenses", allExpenses);
        model.addAttribute("approvedCount", approvedCount);
        model.addAttribute("rejectedCount", rejectedCount);

        return "manager/dashboard";
    }

    
    @GetMapping("/expense/{id}")
    public String viewExpense(@PathVariable Long id, HttpSession session, Model model) {
        Employee employee = (Employee) session.getAttribute("loggedInUser");
        if (employee == null || employee.getRole() != Employee.Role.MANAGER) {
            return "redirect:/login";
        }
        
        Expense expense = expenseService.getExpenseById(id).orElse(null);
        if (expense == null) {
            return "redirect:/manager/dashboard";
        }
        
        model.addAttribute("expense", expense);
        return "manager/expense-detail";
    }
    
    @PostMapping("/expense/{id}/approve")
    public String approveExpense(@PathVariable Long id, HttpSession session) {
        Employee employee = (Employee) session.getAttribute("loggedInUser");
        if (employee == null || employee.getRole() != Employee.Role.MANAGER) {
            return "redirect:/login";
        }
        
        expenseService.approveExpense(id, employee);
        return "redirect:/manager/dashboard?approved=true";
    }
    
    @PostMapping("/expense/{id}/reject")
    public String rejectExpense(@PathVariable Long id, HttpSession session) {
        Employee employee = (Employee) session.getAttribute("loggedInUser");
        if (employee == null || employee.getRole() != Employee.Role.MANAGER) {
            return "redirect:/login";
        }
        
        expenseService.rejectExpense(id, employee);
        return "redirect:/manager/dashboard?rejected=true";
    }
}