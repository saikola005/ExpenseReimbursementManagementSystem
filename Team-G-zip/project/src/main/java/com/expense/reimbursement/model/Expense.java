package com.expense.reimbursement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "expenses")
public class Expense {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Description is required")
    @Column(nullable = false)
    private String description;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @NotNull(message = "Expense date is required")
    @Column(nullable = false)
    private LocalDate expenseDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;
    
    @Column
    private String receiptFileName;
    
    @Column
    private String receiptFilePath;
    
    @Column(columnDefinition = "TEXT")
    private String comments;
    
    @Column(nullable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();
    
    @Column
    private LocalDateTime approvedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private Employee approvedBy;
    
    // Constructors
    public Expense() {}
    
    public Expense(String description, BigDecimal amount, LocalDate expenseDate, Category category, Employee employee) {
        this.description = description;
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.category = category;
        this.employee = employee;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    
    public String getReceiptFileName() { return receiptFileName; }
    public void setReceiptFileName(String receiptFileName) { this.receiptFileName = receiptFileName; }
    
    public String getReceiptFilePath() { return receiptFilePath; }
    public void setReceiptFilePath(String receiptFilePath) { this.receiptFilePath = receiptFilePath; }
    
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    
    public Employee getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Employee approvedBy) { this.approvedBy = approvedBy; }
    
    public enum Category {
        TRAVEL, FOOD, ACCOMMODATION, TRANSPORTATION, OFFICE_SUPPLIES, TRAINING, OTHER
    }
    
    public enum Status {
        PENDING, APPROVED, REJECTED
    }
}