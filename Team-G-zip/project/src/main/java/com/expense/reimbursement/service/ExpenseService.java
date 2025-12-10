package com.expense.reimbursement.service;

import com.expense.reimbursement.model.Employee;
import com.expense.reimbursement.model.Expense;
import com.expense.reimbursement.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    public Expense saveExpense(Expense expense) {
        return expenseRepository.save(expense);
    }
    
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAllByOrderBySubmittedAtDesc();
    }
    
    public List<Expense> getExpensesByEmployee(Employee employee) {
        return expenseRepository.findByEmployeeOrderBySubmittedAtDesc(employee);
    }
    
    public List<Expense> getPendingExpenses() {
        return expenseRepository.findByStatusOrderBySubmittedAtDesc(Expense.Status.PENDING);
    }
    
    public Optional<Expense> getExpenseById(Long id) {
        return expenseRepository.findById(id);
    }
    
    public Expense approveExpense(Long expenseId, Employee approver) {
        Optional<Expense> expenseOpt = expenseRepository.findById(expenseId);
        if (expenseOpt.isPresent()) {
            Expense expense = expenseOpt.get();
            expense.setStatus(Expense.Status.APPROVED);
            expense.setApprovedBy(approver);
            expense.setApprovedAt(LocalDateTime.now());
            return expenseRepository.save(expense);
        }
        return null;
    }
    
    public Expense rejectExpense(Long expenseId, Employee approver) {
        Optional<Expense> expenseOpt = expenseRepository.findById(expenseId);
        if (expenseOpt.isPresent()) {
            Expense expense = expenseOpt.get();
            expense.setStatus(Expense.Status.REJECTED);
            expense.setApprovedBy(approver);
            expense.setApprovedAt(LocalDateTime.now());
            return expenseRepository.save(expense);
        }
        return null;
    }
}