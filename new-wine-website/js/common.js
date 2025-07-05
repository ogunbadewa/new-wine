// NEW WINE CHURCH - COMMON JAVASCRIPT
// Shared functionality across all pages

// Configuration
const CONFIG = {
    API_BASE_URL: 'http://localhost:8080/api', // Change this for production
    ENVIRONMENT: 'development' // 'development' or 'production'
};

// Initialize common functionality when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Initialize animations first
    initializeAnimations();
    initializeScrollEffects();
    
    // Remove no-js class after animations are set up
    setTimeout(() => {
        document.documentElement.classList.remove('no-js');
    }, 50);

    initializeNavigation();
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
    
    setActiveNavLink();
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

// Mobile menu functionality (placeholder)
function initializeMobileMenu() {
    const navContent = document.querySelector('.nav-content');
    if (!navContent) return;

    // Optional: future mobile menu button setup
}

// Smooth scrolling for anchor links
function initializeSmoothScrolling() {
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();

            const targetId = this.getAttribute('href');
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

// Scroll effects and animations - Fixed to prevent flash
function initializeScrollEffects() {
    // Create intersection observer for smooth scroll animations
    const observerOptions = {
        threshold: 0.15,
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
    document.querySelectorAll('.animate-on-scroll, .section-title, .section-subtitle').forEach(el => {
        observer.observe(el);
    });
}

// Initialize animations (adds animate-on-scroll if missing)
function initializeAnimations() {
    // Add animation classes to elements that should animate
    const animatedElements = document.querySelectorAll('.card, .section, .feature-card, .testimonial-card, .step-card, .expect-item, .faq-item, .event-item, .contact-card');
    
    animatedElements.forEach(el => {
        if (!el.classList.contains('animate-on-scroll')) {
            el.classList.add('animate-on-scroll');
        }
    });

    // Add animation classes to section titles and subtitles
    document.querySelectorAll('.section-title, .section-subtitle').forEach(el => {
        if (!el.classList.contains('animate-on-scroll')) {
            el.classList.add('animate-on-scroll');
        }
    });

    // For elements above the fold (like on homepage), make them visible immediately
    const heroSection = document.querySelector('.hero-section');
    if (heroSection) {
        const heroNextSection = heroSection.nextElementSibling;
        if (heroNextSection) {
            const immediateElements = heroNextSection.querySelectorAll('.animate-on-scroll');
            immediateElements.forEach(el => {
                // Small delay to allow CSS to be applied
                setTimeout(() => {
                    el.classList.add('visible');
                }, 100);
            });
        }
    }
}

// Utility functions
const Utils = {
    formatDate: function(dateString) {
        if (!dateString) return 'N/A';
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    },

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

    showMessage: function(message, type = 'info', duration = 5000) {
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
        document.body.appendChild(messageEl);

        setTimeout(() => {
            messageEl.style.animation = 'slideOut 0.3s ease';
            setTimeout(() => {
                if (messageEl.parentNode) {
                    messageEl.parentNode.removeChild(messageEl);
                }
            }, 300);
        }, duration);
    },

    isValidEmail: function(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    },

    isValidPhone: function(phone) {
        if (!phone) return true;
        const phoneRegex = /^[\+]?[1-9][\d]{0,15}$/;
        return phone.replace(/[\s\-\(\)]/g, '').match(phoneRegex);
    },

    sanitizeInput: function(input) {
        if (typeof input !== 'string') return input;
        return input.trim().replace(/[<>]/g, '');
    },

    getCurrentTimestamp: function() {
        return new Date().toISOString();
    },

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
    if (CONFIG.ENVIRONMENT === 'development') {
        Utils.showMessage('An error occurred. Check the console for details.', 'error');
    }
});

window.addEventListener('unhandledrejection', function(e) {
    console.error('Unhandled Promise Rejection:', e.reason);
    if (CONFIG.ENVIRONMENT === 'development') {
        Utils.showMessage('A network error occurred. Please try again.', 'error');
    }
});

// Export utilities
window.NewWineUtils = Utils;
window.NewWineConfig = CONFIG;

// Add CSS animations
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from { transform: translateX(100%); opacity: 0; }
        to { transform: translateX(0); opacity: 1; }
    }
    @keyframes slideOut {
        from { transform: translateX(0); opacity: 1; }
        to { transform: translateX(100%); opacity: 0; }
    }
`;
document.head.appendChild(style);