// Library Management System Web App Engine

const INITIAL_BOOKS = [
  {
    id: 1,
    title: "Clean Code",
    author: "Robert C. Martin",
    isbn: "9780132350884",
    category: "Software Engineering",
    status: "AVAILABLE",
    borrowedBy: null,
    gradient: "linear-gradient(135deg, #1e3c72 0%, #2a5298 100%)"
  },
  {
    id: 2,
    title: "Design Patterns",
    author: "Erich Gamma",
    isbn: "9780201633610",
    category: "Software Engineering",
    status: "AVAILABLE",
    borrowedBy: null,
    gradient: "linear-gradient(135deg, #7b4397 0%, #dc2430 100%)"
  },
  {
    id: 3,
    title: "Effective Java",
    author: "Joshua Bloch",
    isbn: "9780134685991",
    category: "Computer Science",
    status: "AVAILABLE",
    borrowedBy: null,
    gradient: "linear-gradient(135deg, #11998e 0%, #38ef7d 100%)"
  },
  {
    id: 4,
    title: "The Pragmatic Programmer",
    author: "Andrew Hunt, David Thomas",
    isbn: "9780135957059",
    category: "Technology",
    status: "AVAILABLE",
    borrowedBy: null,
    gradient: "linear-gradient(135deg, #ff416c 0%, #ff4b2b 100%)"
  }
];

class LibraryApp {
  constructor() {
    this.users = JSON.parse(localStorage.getItem('lms_registered_users')) || [];
    this.currentUser = JSON.parse(localStorage.getItem('lms_current_user')) || null;
    this.books = JSON.parse(localStorage.getItem('lms_books')) || INITIAL_BOOKS;
    this.currentFilter = 'all';
    this.searchQuery = '';

    this.initElements();
    this.bindEvents();
    this.checkAuth();
  }

  initElements() {
    // Auth Elements
    this.authScreen = document.getElementById('auth-screen');
    this.authTabSignin = document.getElementById('auth-tab-signin');
    this.authTabSignup = document.getElementById('auth-tab-signup');
    this.signinForm = document.getElementById('signin-form');
    this.signupForm = document.getElementById('signup-form');

    // App Elements
    this.bookGrid = document.getElementById('book-grid');
    this.searchInput = document.getElementById('search-input');
    this.filterTabs = document.querySelectorAll('.tab-btn');
    this.myBorrowedTab = document.getElementById('my-borrowed-tab');
    
    this.currentUsername = document.getElementById('current-username');
    this.currentRoleBadge = document.getElementById('current-role-badge');
    this.addBookHeaderBtn = document.getElementById('add-book-header-btn');
    this.logoutBtn = document.getElementById('logout-btn');

    // Stats
    this.statTotalBooks = document.getElementById('stat-total-books');
    this.statAvailableBooks = document.getElementById('stat-available-books');
    this.statIssuedBooks = document.getElementById('stat-issued-books');
    this.statTotalUsers = document.getElementById('stat-total-users');

    // Modal
    this.bookModal = document.getElementById('book-modal');
    this.bookForm = document.getElementById('book-form');
    this.closeModalBtn = document.getElementById('close-modal-btn');
    this.cancelModalBtn = document.getElementById('cancel-modal-btn');
    this.modalTitle = document.getElementById('modal-title');
  }

  bindEvents() {
    // Auth Tab Switch
    this.authTabSignin.addEventListener('click', () => {
      this.authTabSignin.classList.add('active');
      this.authTabSignup.classList.remove('active');
      this.signinForm.classList.add('active');
      this.signupForm.classList.remove('active');
    });

    this.authTabSignup.addEventListener('click', () => {
      this.authTabSignup.classList.add('active');
      this.authTabSignin.classList.remove('active');
      this.signupForm.classList.add('active');
      this.signinForm.classList.remove('active');
    });

    // Sign In Submit
    this.signinForm.addEventListener('submit', (e) => {
      e.preventDefault();
      this.handleSignIn();
    });

    // Sign Up Submit
    this.signupForm.addEventListener('submit', (e) => {
      e.preventDefault();
      this.handleSignUp();
    });

    // Logout
    this.logoutBtn.addEventListener('click', () => {
      this.logout();
    });

    // Search input
    this.searchInput.addEventListener('input', (e) => {
      this.searchQuery = e.target.value.toLowerCase().trim();
      this.render();
    });

    // Category Filter tabs
    this.filterTabs.forEach(tab => {
      tab.addEventListener('click', () => {
        this.filterTabs.forEach(t => t.classList.remove('active'));
        tab.classList.add('active');
        this.currentFilter = tab.dataset.filter;
        this.render();
      });
    });

    // Open Add Book Modal
    this.addBookHeaderBtn.addEventListener('click', () => {
      this.openAddBookModal();
    });

    // Close Modal
    this.closeModalBtn.addEventListener('click', () => this.closeModal());
    this.cancelModalBtn.addEventListener('click', () => this.closeModal());

    // Submit Book Form
    this.bookForm.addEventListener('submit', (e) => {
      e.preventDefault();
      this.saveBook();
    });
  }

  handleSignIn() {
    const role = document.querySelector('input[name="signin-role"]:checked').value;
    const username = document.getElementById('signin-username').value.trim();
    const password = document.getElementById('signin-password').value.trim();

    const matchedUser = this.users.find(u => 
      u.username.toLowerCase() === username.toLowerCase() && 
      u.password === password && 
      u.role === role
    );

    if (matchedUser) {
      this.currentUser = { username: matchedUser.username, role: matchedUser.role };
      localStorage.setItem('lms_current_user', JSON.stringify(this.currentUser));
      this.checkAuth();
      this.showToast(`Welcome back, ${matchedUser.username}!`, 'success');
    } else {
      this.showToast('Invalid credentials or role mismatch!', 'error');
    }
  }

  handleSignUp() {
    const role = document.querySelector('input[name="signup-role"]:checked').value;
    const username = document.getElementById('signup-username').value.trim();
    const password = document.getElementById('signup-password').value.trim();

    if (!username || !password) {
      this.showToast('Please fill in all fields', 'error');
      return;
    }

    const exists = this.users.some(u => u.username.toLowerCase() === username.toLowerCase());
    if (exists) {
      this.showToast('Username already exists! Please choose another.', 'error');
      return;
    }

    const newUser = { username, password, role };
    this.users.push(newUser);
    localStorage.setItem('lms_registered_users', JSON.stringify(this.users));

    this.currentUser = { username: newUser.username, role: newUser.role };
    localStorage.setItem('lms_current_user', JSON.stringify(this.currentUser));

    this.checkAuth();
    this.showToast(`Account created successfully! Welcome, ${newUser.username}!`, 'success');
  }

  logout() {
    this.currentUser = null;
    localStorage.removeItem('lms_current_user');
    this.checkAuth();
    this.showToast('Logged out successfully.', 'info');
  }

  checkAuth() {
    if (this.currentUser) {
      this.authScreen.classList.remove('active');
      this.render();
    } else {
      document.getElementById('signin-username').value = '';
      document.getElementById('signin-password').value = '';
      document.getElementById('signup-username').value = '';
      document.getElementById('signup-password').value = '';
      this.authScreen.classList.add('active');
    }
  }

  saveData() {
    localStorage.setItem('lms_books', JSON.stringify(this.books));
  }

  updateUserUI() {
    if (!this.currentUser) return;
    this.currentUsername.textContent = this.currentUser.username;
    this.currentRoleBadge.textContent = this.currentUser.role;
    this.currentRoleBadge.className = `role-pill ${this.currentUser.role.toLowerCase()}`;

    if (this.currentUser.role === 'ADMIN') {
      this.addBookHeaderBtn.style.display = 'inline-flex';
      this.myBorrowedTab.style.display = 'none';
    } else {
      this.addBookHeaderBtn.style.display = 'none';
      this.myBorrowedTab.style.display = 'inline-block';
    }
  }

  updateStats() {
    const total = this.books.length;
    const available = this.books.filter(b => b.status === 'AVAILABLE').length;
    const issued = this.books.filter(b => b.status === 'ISSUED').length;

    this.statTotalBooks.textContent = total;
    this.statAvailableBooks.textContent = available;
    this.statIssuedBooks.textContent = issued;
    this.statTotalUsers.textContent = this.users.length;
  }

  render() {
    if (!this.currentUser) return;
    this.updateUserUI();
    this.updateStats();

    let filtered = this.books.filter(book => {
      const matchesSearch = book.title.toLowerCase().includes(this.searchQuery) ||
                            book.author.toLowerCase().includes(this.searchQuery) ||
                            book.isbn.toLowerCase().includes(this.searchQuery);

      if (!matchesSearch) return false;

      if (this.currentFilter === 'AVAILABLE') return book.status === 'AVAILABLE';
      if (this.currentFilter === 'ISSUED') return book.status === 'ISSUED';
      if (this.currentFilter === 'my-borrowed') return book.borrowedBy === this.currentUser.username;
      
      return true;
    });

    this.bookGrid.innerHTML = '';

    if (filtered.length === 0) {
      this.bookGrid.innerHTML = `
        <div class="empty-state">
          <i class="fa-solid fa-folder-open empty-icon"></i>
          <h3>No books found</h3>
          <p>Try adjusting your search query or filter tab.</p>
        </div>
      `;
      return;
    }

    filtered.forEach(book => {
      const card = document.createElement('div');
      card.className = 'book-card glass-panel';

      const gradient = book.gradient || 'linear-gradient(135deg, #1e3c72 0%, #2a5298 100%)';
      const isAvailable = book.status === 'AVAILABLE';

      let actionButtons = '';
      if (this.currentUser.role === 'ADMIN') {
        actionButtons = `
          <button class="btn btn-secondary btn-sm" onclick="app.openEditModal(${book.id})">
            <i class="fa-solid fa-pen"></i> Edit
          </button>
          <button class="btn btn-danger btn-sm" onclick="app.deleteBook(${book.id})">
            <i class="fa-solid fa-trash"></i> Delete
          </button>
        `;
      } else {
        if (isAvailable) {
          actionButtons = `
            <button class="btn btn-primary btn-sm" onclick="app.borrowBook(${book.id})">
              <i class="fa-solid fa-bookmark"></i> Borrow
            </button>
          `;
        } else if (book.borrowedBy === this.currentUser.username) {
          actionButtons = `
            <button class="btn btn-secondary btn-sm" onclick="app.returnBook(${book.id})">
              <i class="fa-solid fa-rotate-left"></i> Return Book
            </button>
          `;
        } else {
          actionButtons = `
            <button class="btn btn-secondary btn-sm" disabled style="opacity: 0.5;">
              Issued to ${book.borrowedBy || 'Member'}
            </button>
          `;
        }
      }

      card.innerHTML = `
        <div class="book-cover" style="background: ${gradient}">
          <span class="status-badge ${book.status.toLowerCase()}">${book.status}</span>
          <div class="book-cover-title">${this.escapeHtml(book.title)}</div>
          <div class="book-cover-author">${this.escapeHtml(book.author)}</div>
        </div>
        <div class="book-details">
          <h4 class="book-title">${this.escapeHtml(book.title)}</h4>
          <div class="book-author">by ${this.escapeHtml(book.author)}</div>
          <div class="book-isbn">ISBN: ${this.escapeHtml(book.isbn)}</div>
        </div>
        <div class="book-actions">
          ${actionButtons}
        </div>
      `;

      this.bookGrid.appendChild(card);
    });
  }

  borrowBook(id) {
    const book = this.books.find(b => b.id === id);
    if (!book || book.status !== 'AVAILABLE') return;

    book.status = 'ISSUED';
    book.borrowedBy = this.currentUser.username;
    this.saveData();
    this.render();
    this.showToast(`Successfully borrowed "${book.title}"!`, 'success');
  }

  returnBook(id) {
    const book = this.books.find(b => b.id === id);
    if (!book) return;

    book.status = 'AVAILABLE';
    book.borrowedBy = null;
    this.saveData();
    this.render();
    this.showToast(`Returned "${book.title}". Thank you!`, 'success');
  }

  deleteBook(id) {
    if (!confirm('Are you sure you want to delete this book?')) return;
    this.books = this.books.filter(b => b.id !== id);
    this.saveData();
    this.render();
    this.showToast('Book removed from library catalog.', 'info');
  }

  openAddBookModal() {
    this.modalTitle.textContent = 'Add New Book';
    document.getElementById('book-id').value = '';
    document.getElementById('book-title-input').value = '';
    document.getElementById('book-author-input').value = '';
    document.getElementById('book-isbn-input').value = '';
    this.bookModal.classList.add('active');
  }

  openEditModal(id) {
    const book = this.books.find(b => b.id === id);
    if (!book) return;

    this.modalTitle.textContent = 'Edit Book';
    document.getElementById('book-id').value = book.id;
    document.getElementById('book-title-input').value = book.title;
    document.getElementById('book-author-input').value = book.author;
    document.getElementById('book-isbn-input').value = book.isbn;
    this.bookModal.classList.add('active');
  }

  closeModal() {
    this.bookModal.classList.remove('active');
  }

  saveBook() {
    const id = document.getElementById('book-id').value;
    const title = document.getElementById('book-title-input').value.trim();
    const author = document.getElementById('book-author-input').value.trim();
    const isbn = document.getElementById('book-isbn-input').value.trim();
    const category = document.getElementById('book-category-input').value;

    if (!title || !author || !isbn) return;

    const gradients = [
      "linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)",
      "linear-gradient(135deg, #fa709a 0%, #fee140 100%)",
      "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
      "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)"
    ];

    if (id) {
      const book = this.books.find(b => b.id === Number(id));
      if (book) {
        book.title = title;
        book.author = author;
        book.isbn = isbn;
        book.category = category;
      }
      this.showToast('Book details updated.', 'success');
    } else {
      const newBook = {
        id: Date.now(),
        title,
        author,
        isbn,
        category,
        status: 'AVAILABLE',
        borrowedBy: null,
        gradient: gradients[Math.floor(Math.random() * gradients.length)]
      };
      this.books.unshift(newBook);
      this.showToast('New book added to library.', 'success');
    }

    this.saveData();
    this.closeModal();
    this.render();
  }

  showToast(message, type = 'info') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;

    let icon = 'fa-info-circle';
    if (type === 'success') icon = 'fa-circle-check';
    if (type === 'error') icon = 'fa-circle-exclamation';

    toast.innerHTML = `<i class="fa-solid ${icon}"></i> <span>${this.escapeHtml(message)}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
      toast.style.opacity = '0';
      setTimeout(() => toast.remove(), 300);
    }, 3000);
  }

  escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>"']/g, match => ({
      '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    }[match]));
  }
}

// Initialize Application Globally
window.app = new LibraryApp();
