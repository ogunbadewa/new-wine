// NEW WINE CHURCH - COMMON JAVASCRIPT
// Shared functionality across all pages

// Configuration
const CONFIG = {
    API_BASE_URL: 'http://localhost:8080/api', // Change this for production
    ENVIRONMENT: 'development' // 'development' or 'production'
};

// Initialize common functionality when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    initializeNavigation();
    initializeScrollEffects();
    initializeAnimations();
    initializeSmoothScrolling();
});

// Navigation functionality
function initializeNavigation() {
    const navbar = document.getElementById('navbar');
    
    if (!navbar) return;
    
    // Navbar scroll effect
    window.addEventListener('scroll', () => {
        if (window.scrollY > 100) {
            navbar.classList.add('scrolled');
        } else {
            navbar.classList.remove('scrolled');
        }
    });
    
    // Set active nav link based on current page
    setActiveNavLink();
    
    // Mobile menu toggle (if needed in future)
    initializeMobileMenu();
}

// Set active navigation link
function setActiveNavLink() {
    const currentPage = window.location.pathname.split('/').pop() || 'index.html';
    const navLinks = document.querySelectorAll('.nav-links a');
    
    navLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (href === currentPage || (currentPage === '' && href === 'index.html')) {
            link.classList.add('active');
        } else {
            link.classList.remove('active');
        }
    });
}

// Mobile menu functionality
function initializeMobileMenu() {
    // Add mobile menu button if viewport is small
    const navContent = document.querySelector('.nav-content');
    if (!navContent) return;
    
    // Create mobile menu button
    const mobileMenuBtn = document.createElement('button');
    mobileMenuBtn.className = 'mobile-menu-btn';
    mobileMenuBtn.innerHTML = '☰';
    mobileMenuBtn.style.cssText = `
        display: none;
        background: none;
        border: none;
        font-size: 1.5rem;
        color: var(--dark);
        cursor: pointer;
        @media (max-width: 768px) {
            display: block;
        }
    `;
    
    // Toggle mobile menu
    mobileMenuBtn.addEventListener('click', () => {
        const navLinks = document.querySelector('.nav-links');
        if (navLinks) {
            navLinks.style.display = navLinks.style.display === 'flex' ? 'none' : 'flex';
        }
    });
    
    // Add to navigation (commented out for now, can be enabled if needed)
    // navContent.appendChild(mobileMenuBtn);
}

// Smooth scrolling for anchor links
// Smooth scrolling for anchor links
function initializeSmoothScrolling() {
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();

            const targetId = this.getAttribute('href');

            // Ignore invalid or empty '#' links
            if (!targetId || targetId === '#' || targetId.trim().length === 0) return;

            let targetElement;
            try {
                targetElement = document.querySelector(targetId);
            } catch (error) {
                console.warn(`Invalid selector: ${targetId}`);
                return;
            }

            if (targetElement) {
                const navHeight = document.getElementById('navbar')?.offsetHeight || 80;
                const targetPosition = targetElement.getBoundingClientRect().top + window.pageYOffset - navHeight;

                window.scrollTo({
                    top: targetPosition,
                    behavior: 'smooth'
                });
            }
        });
    });
}

// Scroll effects and animations
function initializeScrollEffects() {
    // Intersection Observer for scroll animations
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('visible');
            }
        });
    }, observerOptions);

    // Observe all animated elements
    document.querySelectorAll('.animate-on-scroll').forEach(el => {
        observer.observe(el);
    });
}

// Initialize animations
function initializeAnimations() {
    // Add animation classes to elements that need them
    const animatedElements = document.querySelectorAll('.card, .section, .feature-card, .testimonial-card, .step-card, .expect-item, .faq-item, .event-item, .contact-card');
    
    animatedElements.forEach(el => {
        if (!el.classList.contains('animate-on-scroll')) {
            el.classList.add('animate-on-scroll');
        }
    });
}

// Utility functions
const Utils = {
    // Format date for display
    formatDate: function(dateString) {
        if (!dateString) return 'N/A';
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    },

    // Format date and time for display
    formatDateTime: function(dateString) {
        if (!dateString) return 'N/A';
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: 'numeric',
            minute: '2-digit'
        });
    },

    // Show loading state on button
    setButtonLoading: function(button, isLoading, originalText = null) {
        if (isLoading) {
            if (!button.dataset.originalText) {
                button.dataset.originalText = button.textContent;
            }
            button.textContent = 'Loading...';
            button.disabled = true;
        } else {
            button.textContent = originalText || button.dataset.originalText || 'Submit';
            button.disabled = false;
            delete button.dataset.originalText;
        }
    },

    // Show success/error messages
    showMessage: function(message, type = 'info', duration = 5000) {
        // Create message element
        const messageEl = document.createElement('div');
        messageEl.className = `message message-${type}`;
        messageEl.textContent = message;
        messageEl.style.cssText = `
            position: fixed;
            top: 100px;
            right: 2rem;
            background: ${type === 'success' ? '#10b981' : type === 'error' ? '#ef4444' : '#3b82f6'};
            color: white;
            padding: 1rem 2rem;
            border-radius: 8px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
            z-index: 10000;
            animation: slideIn 0.3s ease;
        `;
        
        // Add to page
        document.body.appendChild(messageEl);
        
        // Remove after duration
        setTimeout(() => {
            messageEl.style.animation = 'slideOut 0.3s ease';
            setTimeout(() => {
                if (messageEl.parentNode) {
                    messageEl.parentNode.removeChild(messageEl);
                }
            }, 300);
        }, duration);
    },

    // Validate email
    isValidEmail: function(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    },

    // Validate phone number
    isValidPhone: function(phone) {
        if (!phone) return true; // Phone is optional
        const phoneRegex = /^[\+]?[1-9][\d]{0,15}$/;
        return phoneRegex.test(phone.replace(/[\s\-\(\)]/g, ''));
    },

    // Sanitize input
    sanitizeInput: function(input) {
        if (typeof input !== 'string') return input;
        return input.trim().replace(/[<>]/g, '');
    },

    // Get current timestamp
    getCurrentTimestamp: function() {
        return new Date().toISOString();
    },

    // Debounce function
    debounce: function(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }
};

// Error handling
window.addEventListener('error', function(e) {
    console.error('JavaScript Error:', e.error);
    
    // Don't show error messages in production
    if (CONFIG.ENVIRONMENT === 'development') {
        Utils.showMessage('An error occurred. Check the console for details.', 'error');
    }
});

// Unhandled promise rejection handling
window.addEventListener('unhandledrejection', function(e) {
    console.error('Unhandled Promise Rejection:', e.reason);
    
    if (CONFIG.ENVIRONMENT === 'development') {
        Utils.showMessage('A network error occurred. Please try again.', 'error');
    }
});

// Export utilities for use in other files
window.NewWineUtils = Utils;
window.NewWineConfig = CONFIG;

// Add CSS animations
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    @keyframes slideOut {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(100%);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);