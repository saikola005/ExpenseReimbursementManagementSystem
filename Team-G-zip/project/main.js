// Application state
let currentUser = null;
let expenses = [];

// Mock data for demo
const mockUsers = [
    {
        id: 1,
        name: "John Employee",
        email: "employee@demo.com",
        password: "password",
        role: "EMPLOYEE",
        department: "IT"
    },
    {
        id: 2,
        name: "Jane Manager",
        email: "manager@demo.com",
        password: "password",
        role: "MANAGER",
        department: "IT"
    }
];

let mockExpenses = [
    {
        id: 1,
        description: "Business lunch with client",
        amount: 85.50,
        expenseDate: "2024-01-15",
        category: "FOOD",
        status: "PENDING",
        comments: "Discussed new project requirements",
        submittedAt: "2024-01-15T14:30:00",
        employee: mockUsers[0],
        receiptFileName: "lunch_receipt.jpg"
    },
    {
        id: 2,
        description: "Flight to conference",
        amount: 450.00,
        expenseDate: "2024-01-10",
        category: "TRAVEL",
        status: "APPROVED",
        comments: "Annual tech conference attendance",
        submittedAt: "2024-01-10T09:15:00",
        approvedAt: "2024-01-11T10:30:00",
        employee: mockUsers[0],
        approvedBy: mockUsers[1],
        receiptFileName: "flight_ticket.pdf"
    }
];

// Utility functions
function showAlert(containerId, type, message) {
    const container = document.getElementById(containerId);
    container.innerHTML = `
        <div class="alert alert-${type} alert-dismissible fade show" role="alert">
            <i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-triangle'} me-2"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
}

function hideElement(id) {
    document.getElementById(id).classList.add('hidden');
}

function showElement(id) {
    document.getElementById(id).classList.remove('hidden');
}

function formatCurrency(amount) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(amount);
}

function formatDate(dateString) {
    return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

function getStatusBadgeClass(status) {
    switch (status) {
        case 'PENDING': return 'bg-warning';
        case 'APPROVED': return 'bg-success';
        case 'REJECTED': return 'bg-danger';
        default: return 'bg-secondary';
    }
}

// Authentication functions
function login(email, password) {
    const user = mockUsers.find(u => u.email === email && u.password === password);
    if (user) {
        currentUser = user;
        localStorage.setItem('currentUser', JSON.stringify(user));
        return { success: true, user };
    }
    return { success: false, message: 'Invalid email or password' };
}

function register(userData) {
    // Check if email already exists
    if (mockUsers.find(u => u.email === userData.email)) {
        return { success: false, message: 'Email already exists' };
    }
    
    const newUser = {
        id: mockUsers.length + 1,
        ...userData
    };
    
    mockUsers.push(newUser);
    return { success: true, message: 'Registration successful! Please login.' };
}

function logout() {
    currentUser = null;
    localStorage.removeItem('currentUser');
    showLoginPage();
}

// Page navigation functions
function showLoginPage() {
    hideElement('registerPage');
    hideElement('employeeDashboard');
    hideElement('managerDashboard');
    showElement('loginPage');
}

function showRegisterPage() {
    hideElement('loginPage');
    hideElement('employeeDashboard');
    hideElement('managerDashboard');
    showElement('registerPage');
}

function showDashboard() {
    hideElement('loginPage');
    hideElement('registerPage');
    
    if (currentUser.role === 'MANAGER') {
        hideElement('employeeDashboard');
        showElement('managerDashboard');
        loadManagerDashboard();
    } else {
        hideElement('managerDashboard');
        showElement('employeeDashboard');
        loadEmployeeDashboard();
    }
}

// Employee dashboard functions
function loadEmployeeDashboard() {
    document.getElementById('employeeWelcome').textContent = `Welcome, ${currentUser.name}`;
    
    const userExpenses = mockExpenses.filter(e => e.employee.id === currentUser.id);
    expenses = userExpenses;
    
    updateEmployeeStats();
    renderEmployeeExpenses();
}

function updateEmployeeStats() {
    const total = expenses.length;
    const pending = expenses.filter(e => e.status === 'PENDING').length;
    const approved = expenses.filter(e => e.status === 'APPROVED').length;
    
    document.getElementById('totalExpenses').textContent = total;
    document.getElementById('pendingExpenses').textContent = pending;
    document.getElementById('approvedExpenses').textContent = approved;
}

function renderEmployeeExpenses() {
    const tbody = document.getElementById('expensesTableBody');
    
    if (expenses.length === 0) {
        showElement('noExpenses');
        hideElement('expensesTable');
        return;
    }
    
    hideElement('noExpenses');
    showElement('expensesTable');
    
    tbody.innerHTML = expenses.map(expense => `
        <tr>
            <td>${formatDate(expense.expenseDate)}</td>
            <td>${expense.description}</td>
            <td><span class="badge bg-secondary">${expense.category}</span></td>
            <td>${formatCurrency(expense.amount)}</td>
            <td><span class="badge ${getStatusBadgeClass(expense.status)}">${expense.status}</span></td>
            <td>
                <button class="btn btn-sm btn-outline-primary" onclick="viewExpenseDetails(${expense.id})">
                    <i class="fas fa-eye"></i> View
                </button>
            </td>
        </tr>
    `).join('');
}

// Manager dashboard functions
function loadManagerDashboard() {
    document.getElementById('managerWelcome').textContent = `Welcome, ${currentUser.name} (Manager)`;
    
    updateManagerStats();
    renderManagerExpenses();
}

function updateManagerStats() {
    const total = mockExpenses.length;
    const pending = mockExpenses.filter(e => e.status === 'PENDING').length;
    const approved = mockExpenses.filter(e => e.status === 'APPROVED').length;
    const rejected = mockExpenses.filter(e => e.status === 'REJECTED').length;
    
    document.getElementById('managerTotalExpenses').textContent = total;
    document.getElementById('managerPendingExpenses').textContent = pending;
    document.getElementById('managerApprovedExpenses').textContent = approved;
    document.getElementById('managerRejectedExpenses').textContent = rejected;
    document.getElementById('pendingCount').textContent = pending;
}

function renderManagerExpenses() {
    renderPendingExpenses();
    renderAllExpenses();
}

function renderPendingExpenses() {
    const pendingExpenses = mockExpenses.filter(e => e.status === 'PENDING');
    const tbody = document.getElementById('pendingExpensesTableBody');
    
    if (pendingExpenses.length === 0) {
        showElement('noPendingExpenses');
        hideElement('pendingExpensesTable');
        return;
    }
    
    hideElement('noPendingExpenses');
    showElement('pendingExpensesTable');
    
    tbody.innerHTML = pendingExpenses.map(expense => `
        <tr>
            <td>${expense.employee.name}</td>
            <td>${formatDate(expense.expenseDate)}</td>
            <td>${expense.description}</td>
            <td><span class="badge bg-secondary">${expense.category}</span></td>
            <td>${formatCurrency(expense.amount)}</td>
            <td>
                <div class="btn-group" role="group">
                    <button class="btn btn-sm btn-outline-primary" onclick="viewExpenseDetails(${expense.id})">
                        <i class="fas fa-eye"></i> View
                    </button>
                    <button class="btn btn-sm btn-success" onclick="approveExpense(${expense.id})">
                        <i class="fas fa-check"></i> Approve
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="rejectExpense(${expense.id})">
                        <i class="fas fa-times"></i> Reject
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

function renderAllExpenses() {
    const tbody = document.getElementById('allExpensesTableBody');
    
    if (mockExpenses.length === 0) {
        showElement('noAllExpenses');
        hideElement('allExpensesTable');
        return;
    }
    
    hideElement('noAllExpenses');
    showElement('allExpensesTable');
    
    tbody.innerHTML = mockExpenses.map(expense => `
        <tr>
            <td>${expense.employee.name}</td>
            <td>${formatDate(expense.expenseDate)}</td>
            <td>${expense.description}</td>
            <td><span class="badge bg-secondary">${expense.category}</span></td>
            <td>${formatCurrency(expense.amount)}</td>
            <td><span class="badge ${getStatusBadgeClass(expense.status)}">${expense.status}</span></td>
            <td>
                <button class="btn btn-sm btn-outline-primary" onclick="viewExpenseDetails(${expense.id})">
                    <i class="fas fa-eye"></i> View
                </button>
            </td>
        </tr>
    `).join('');
}

// Expense management functions
function submitExpense(expenseData) {
    const newExpense = {
        id: mockExpenses.length + 1,
        ...expenseData,
        status: 'PENDING',
        submittedAt: new Date().toISOString(),
        employee: currentUser
    };
    
    mockExpenses.push(newExpense);
    
    if (currentUser.role === 'EMPLOYEE') {
        loadEmployeeDashboard();
        showAlert('successAlert', 'success', 'Expense submitted successfully!');
        showElement('successAlert');
    }
    
    return { success: true };
}

function approveExpense(expenseId) {
    if (!confirm('Are you sure you want to approve this expense?')) return;
    
    const expense = mockExpenses.find(e => e.id === expenseId);
    if (expense) {
        expense.status = 'APPROVED';
        expense.approvedBy = currentUser;
        expense.approvedAt = new Date().toISOString();
        
        loadManagerDashboard();
        showAlert('managerSuccessAlert', 'success', 'Expense approved successfully!');
        showElement('managerSuccessAlert');
    }
}

function rejectExpense(expenseId) {
    if (!confirm('Are you sure you want to reject this expense?')) return;
    
    const expense = mockExpenses.find(e => e.id === expenseId);
    if (expense) {
        expense.status = 'REJECTED';
        expense.approvedBy = currentUser;
        expense.approvedAt = new Date().toISOString();
        
        loadManagerDashboard();
        showAlert('managerSuccessAlert', 'info', 'Expense rejected successfully!');
        showElement('managerSuccessAlert');
    }
}

function viewExpenseDetails(expenseId) {
    const expense = mockExpenses.find(e => e.id === expenseId);
    if (expense) {
        alert(`Expense Details:
        
Description: ${expense.description}
Amount: ${formatCurrency(expense.amount)}
Date: ${formatDate(expense.expenseDate)}
Category: ${expense.category}
Status: ${expense.status}
Employee: ${expense.employee.name}
${expense.comments ? `Comments: ${expense.comments}` : ''}
${expense.receiptFileName ? `Receipt: ${expense.receiptFileName}` : ''}`);
    }
}

// Event listeners
document.addEventListener('DOMContentLoaded', function() {
    // Check for existing session
    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) {
        currentUser = JSON.parse(savedUser);
        showDashboard();
    } else {
        showLoginPage();
    }
    
    // Login form
    document.getElementById('loginForm').addEventListener('submit', function(e) {
        e.preventDefault();
        
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        
        const result = login(email, password);
        
        if (result.success) {
            showDashboard();
        } else {
            showAlert('alertContainer', 'danger', result.message);
        }
    });
    
    // Register form
    document.getElementById('registerForm').addEventListener('submit', function(e) {
        e.preventDefault();
        
        const formData = new FormData(e.target);
        const userData = Object.fromEntries(formData);
        
        const result = register(userData);
        
        if (result.success) {
            showAlert('alertContainer', 'success', result.message);
            showLoginPage();
        } else {
            showAlert('registerAlertContainer', 'danger', result.message);
        }
    });
    
    // Expense form
    document.getElementById('submitExpense').addEventListener('click', function() {
        const form = document.getElementById('expenseForm');
        const formData = new FormData(form);
        const expenseData = Object.fromEntries(formData);
        
        // Basic validation
        if (!expenseData.description || !expenseData.amount || !expenseData.expenseDate || !expenseData.category) {
            showAlert('expenseFormAlert', 'danger', 'Please fill in all required fields.');
            return;
        }
        
        expenseData.amount = parseFloat(expenseData.amount);
        
        const result = submitExpense(expenseData);
        
        if (result.success) {
            // Close modal
            const modal = bootstrap.Modal.getInstance(document.getElementById('expenseModal'));
            modal.hide();
            
            // Reset form
            form.reset();
        }
    });
    
    // Navigation event listeners
    document.getElementById('showRegister').addEventListener('click', function(e) {
        e.preventDefault();
        showRegisterPage();
    });
    
    document.getElementById('showLogin').addEventListener('click', function(e) {
        e.preventDefault();
        showLoginPage();
    });
    
    document.getElementById('employeeLogout').addEventListener('click', function(e) {
        e.preventDefault();
        logout();
    });
    
    document.getElementById('managerLogout').addEventListener('click', function(e) {
        e.preventDefault();
        logout();
    });
    
    // Show expense form buttons
    document.getElementById('showExpenseForm').addEventListener('click', function() {
        const modal = new bootstrap.Modal(document.getElementById('expenseModal'));
        modal.show();
    });
    
    document.getElementById('firstExpenseBtn').addEventListener('click', function() {
        const modal = new bootstrap.Modal(document.getElementById('expenseModal'));
        modal.show();
    });
    
    // Set default date to today
    document.getElementById('expDate').valueAsDate = new Date();
});

// Make functions globally available
window.approveExpense = approveExpense;
window.rejectExpense = rejectExpense;
window.viewExpenseDetails = viewExpenseDetails;