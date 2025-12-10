package com.expense.reimbursement.repository;

import com.expense.reimbursement.model.Employee;
import com.expense.reimbursement.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByEmployeeOrderBySubmittedAtDesc(Employee employee);
    List<Expense> findByStatusOrderBySubmittedAtDesc(Expense.Status status);
    List<Expense> findAllByOrderBySubmittedAtDesc();
}