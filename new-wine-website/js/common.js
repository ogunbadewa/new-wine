// NEW WINE CHURCH - COMMON JAVASCRIPT
// Shared functionality across all pages

// Configuration
const CONFIG = {
    API_BASE_URL: 'http://localhost:8080/api', // Change this for production
    ENVIRONMENT: 'development' // 'development' or 'production'
};

// Slideshow functionality
let slideIndex = 1;
let slideInterval;
let isPlaying = true;
const slideImages = [
    '/media/images/IMMERSE_2024/IM_2024_1.jpg',
    '/media/images/IMMERSE_2024/IM_2024_2.jpg'
    // Add more images as needed
];

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

// Bi-Monthly Service Date Calculation - UPDATED FOR TIMEZONE ACCURACY
function calculateNextBimonthlyService() {
    // Create the last service date in Central Time (Dallas, TX)
    // July 6th, 2025 at 5:30 PM Central Time
    const lastServiceDate = new Date('2025-07-06T17:30:00');
    
    // Get current date/time in Central Time
    const now = new Date();
    const currentCentral = new Date(now.toLocaleString("en-US", {timeZone: "America/Chicago"}));
    
    // Start with the last service date
    let nextService = new Date(lastServiceDate);
    
    // Keep adding 14 days (2 weeks) until we find the next future date
    while (nextService <= currentCentral) {
        nextService.setDate(nextService.getDate() + 14);
    }
    
    return nextService;
}

// Update bi-monthly service display - UPDATED FOR TIMEZONE ACCURACY
function updateBimonthlyServiceDisplay() {
    const nextService = calculateNextBimonthlyService();
    
    // Format date using Central Time
    const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 
                      'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    const fullMonthNames = ['January', 'February', 'March', 'April', 'May', 'June',
                          'July', 'August', 'September', 'October', 'November', 'December'];
    
    // Get the date components in Central Time
    const centralDate = new Date(nextService.toLocaleString("en-US", {timeZone: "America/Chicago"}));
    const month = centralDate.getMonth();
    const day = centralDate.getDate();
    const year = centralDate.getFullYear();
    
    // Update date box elements if they exist
    const monthEl = document.getElementById('bimonthlyMonth');
    const dayEl = document.getElementById('bimonthlyDay');
    const dateTimeEl = document.getElementById('bimonthlyDateTime');
    
    if (monthEl && dayEl) {
        monthEl.textContent = monthNames[month];
        dayEl.textContent = day;
    }
    
    if (dateTimeEl) {
        const dateTimeText = `Next Service: ${fullMonthNames[month]} ${day}, ${year} • 5:30 PM`;
        dateTimeEl.textContent = dateTimeText;
    }

    return nextService;
}

// Slideshow initialization and management
function initializeSlideshow() {
    loadAdditionalImages();
    showSlide(slideIndex);
    autoSlide();
}

function loadAdditionalImages() {
    // This function would typically load images dynamically
    // For now, we'll work with the predefined images
    const slidesTrack = document.getElementById('slidesTrack');
    const indicators = document.getElementById('slideshowIndicators');
    
    if (!slidesTrack || !indicators) return;

    // Clear existing slides except the first two
    const existingSlides = slidesTrack.querySelectorAll('.slide');
    const existingIndicators = indicators.querySelectorAll('.indicator');

    // Add more images if available (this would be dynamic in a real implementation)
    const additionalImages = [
        // Add more image paths here as they become available
        // '/media/images/IMMERSE_2024/IM_2024_3.jpg',
        // '/media/images/IMMERSE_2024/IM_2024_4.jpg',
    ];

    additionalImages.forEach((imageSrc, index) => {
        // Create slide
        const slide = document.createElement('div');
        slide.className = 'slide';
        slide.innerHTML = `<img src="${imageSrc}" alt="IMMERSE 2024 - Moment ${index + 3}" loading="lazy">`;
        slidesTrack.appendChild(slide);

        // Create indicator
        const indicator = document.createElement('span');
        indicator.className = 'indicator';
        indicator.onclick = () => currentSlide(existingSlides.length + index + 1);
        indicators.appendChild(indicator);
    });
}

function changeSlide(direction) {
    const slides = document.querySelectorAll('.slide');
    slideIndex += direction;
    
    if (slideIndex > slides.length) {
        slideIndex = 1;
    }
    if (slideIndex < 1) {
        slideIndex = slides.length;
    }
    
    showSlide(slideIndex);
}

function currentSlide(index) {
    slideIndex = index;
    showSlide(slideIndex);
    resetAutoSlide();
}

function showSlide(index) {
    const slides = document.querySelectorAll('.slide');
    const indicators = document.querySelectorAll('.indicator');
    
    if (slides.length === 0) return;
    
    // Hide all slides
    slides.forEach((slide, i) => {
        slide.classList.remove('active');
        if (indicators[i]) {
            indicators[i].classList.remove('active');
        }
    });
    
    // Show current slide
    if (slides[index - 1]) {
        slides[index - 1].classList.add('active');
    }
    if (indicators[index - 1]) {
        indicators[index - 1].classList.add('active');
    }
}

function autoSlide() {
    if (!isPlaying) return;
    
    slideInterval = setInterval(() => {
        const slides = document.querySelectorAll('.slide');
        if (slides.length > 1) {
            changeSlide(1);
        }
    }, 4000); // Change slide every 4 seconds
}

function toggleSlideshow() {
    const playPauseBtn = document.getElementById('playPauseBtn');
    const playPauseText = document.getElementById('playPauseText');
    
    if (!playPauseBtn || !playPauseText) return;
    
    if (isPlaying) {
        clearInterval(slideInterval);
        isPlaying = false;
        playPauseText.textContent = 'Play';
    } else {
        autoSlide();
        isPlaying = true;
        playPauseText.textContent = 'Pause';
    }
}

function resetAutoSlide() {
    if (isPlaying) {
        clearInterval(slideInterval);
        autoSlide();
    }
}

// Enhanced image loading with error handling
function loadImageWithFallback(img, src, fallbackSrc = '/media/images/placeholder.jpg') {
    return new Promise((resolve, reject) => {
        const image = new Image();
        image.onload = () => {
            img.src = src;
            resolve();
        };
        image.onerror = () => {
            console.warn(`Failed to load image: ${src}`);
            if (fallbackSrc && fallbackSrc !== src) {
                loadImageWithFallback(img, fallbackSrc, null)
                    .then(resolve)
                    .catch(reject);
            } else {
                reject(new Error(`Failed to load image: ${src}`));
            }
        };
        image.src = src;
    });
}

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
                
                // Add staggered animation for cards
                if (entry.target.classList.contains('card')) {
                    const cards = entry.target.parentElement.querySelectorAll('.card');
                    const cardIndex = Array.from(cards).indexOf(entry.target);
                    entry.target.style.animationDelay = `${cardIndex * 0.1}s`;
                }
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

// Enhanced IMMERSE event interactions
function initializeImmerseInteractions() {
    // Add smooth scrolling to IMMERSE details
    const learnMoreBtn = document.querySelector('button[onclick*="immerse-details"]');
    if (learnMoreBtn) {
        learnMoreBtn.addEventListener('click', function() {
            setTimeout(() => {
                const detailsSection = document.getElementById('immerse-details');
                if (detailsSection && detailsSection.style.display === 'block') {
                    detailsSection.scrollIntoView({ 
                        behavior: 'smooth', 
                        block: 'start' 
                    });
                }
            }, 100);
        });
    }
    
    // Add click tracking for registration buttons
    const registrationButtons = document.querySelectorAll('button[onclick*="IMMERSE"], button[onclick*="Bi-Monthly"]');
    registrationButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            // Track registration button clicks if analytics are implemented
            if (typeof gtag !== 'undefined') {
                const eventName = btn.onclick.toString().includes('IMMERSE') ? 'IMMERSE 2025' : 'Bi-Monthly Service';
                gtag('event', 'click', {
                    event_category: 'Event Registration',
                    event_label: eventName
                });
            }
        });
    });
}

// Enhanced form validation
function validateEventRegistrationForm(formData) {
    const errors = [];
    
    if (!formData.get('fullName') || formData.get('fullName').trim().length < 2) {
        errors.push('Please enter a valid full name');
    }
    
    if (!Utils.isValidEmail(formData.get('email'))) {
        errors.push('Please enter a valid email address');
    }
    
    const phone = formData.get('phone');
    if (phone && !Utils.isValidPhone(phone)) {
        errors.push('Please enter a valid phone number');
    }
    
    return errors;
}

// Event-specific utilities
const EventUtils = {
    // Get next bi-monthly service info
    getNextBimonthlyService: function() {
        return calculateNextBimonthlyService();
    },
    
    // Check if a date is a bi-monthly service date
    isBimonthlyServiceDate: function(date) {
        const lastServiceDate = new Date('2025-07-06T17:30:00');
        const targetDate = new Date(date);
        const diffTime = targetDate.getTime() - lastServiceDate.getTime();
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        
        // Check if the difference is a multiple of 14 days
        return diffDays % 14 === 0 && diffDays >= 0;
    },
    
    // Format service information
    formatServiceInfo: function(date) {
        const options = { 
            weekday: 'long', 
            year: 'numeric', 
            month: 'long', 
            day: 'numeric',
            timeZone: 'America/Chicago'
        };
        return {
            date: date.toLocaleDateString('en-US', options),
            time: '5:30 PM',
            location: '13509 Lyndon B Johnson Fwy, Garland, TX 75041',
            phone: '(972) 940-2605'
        };
    }
};

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
            button.classList.add('loading');
        } else {
            button.textContent = originalText || button.dataset.originalText || 'Submit';
            button.disabled = false;
            button.classList.remove('loading');
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
            max-width: 400px;
            word-wrap: break-word;
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
        const cleanPhone = phone.replace(/[\s\-\(\)]/g, '');
        const phoneRegex = /^[\+]?[1-9][\d]{0,15}$/;
        return phoneRegex.test(cleanPhone);
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
    },

    throttle: function(func, wait) {
        let lastTime = 0;
        return function executedFunction(...args) {
            const now = Date.now();
            if (now - lastTime >= wait) {
                func(...args);
                lastTime = now;
            }
        };
    },

    // Intersection Observer utility for lazy loading
    createLazyLoadObserver: function(callback, options = {}) {
        const defaultOptions = {
            threshold: 0.1,
            rootMargin: '50px 0px'
        };
        
        return new IntersectionObserver(callback, { ...defaultOptions, ...options });
    }
};

// Lazy loading for images
function initializeLazyLoading() {
    const lazyImages = document.querySelectorAll('img[loading="lazy"]');
    
    if (lazyImages.length > 0) {
        const imageObserver = Utils.createLazyLoadObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const img = entry.target;
                    loadImageWithFallback(img, img.src)
                        .then(() => {
                            img.classList.add('loaded');
                        })
                        .catch(() => {
                            img.classList.add('error');
                        });
                    imageObserver.unobserve(img);
                }
            });
        });
        
        lazyImages.forEach(img => imageObserver.observe(img));
    }
}

// Performance monitoring
function initializePerformanceMonitoring() {
    // Monitor Core Web Vitals if available
    if ('web-vital' in window) {
        // Implementation would go here for performance tracking
    }
    
    // Simple load time tracking
    window.addEventListener('load', () => {
        const loadTime = performance.now();
        if (CONFIG.ENVIRONMENT === 'development') {
            console.log(`Page loaded in ${Math.round(loadTime)}ms`);
        }
    });
}

// Initialize additional features when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    initializeImmerseInteractions();
    initializeLazyLoading();
    initializePerformanceMonitoring();
    
    // Auto-update bi-monthly service info if on events page
    if (window.location.pathname.includes('events') || window.location.pathname === '/') {
        updateBimonthlyServiceDisplay();
    }
});

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

// Export utilities and functions
window.NewWineUtils = Utils;
window.NewWineConfig = CONFIG;
window.NewWineEventUtils = EventUtils;
window.NewWineSlideshow = {
    changeSlide,
    currentSlide,
    toggleSlideshow,
    initializeSlideshow
};

// Add CSS animations
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        0% { 
            transform: translateX(100%); 
            opacity: 0; 
        }
        100% { 
            transform: translateX(0); 
            opacity: 1; 
        }
    }
    
    @keyframes slideOut {
        0% { 
            transform: translateX(0); 
            opacity: 1; 
        }
        100% { 
            transform: translateX(100%); 
            opacity: 0; 
        }
    }
    
    .loading {
        position: relative;
        overflow: hidden;
    }
    
    .loading::after {
        content: '';
        position: absolute;
        top: 0;
        left: -100%;
        width: 100%;
        height: 100%;
        background: linear-gradient(90deg, transparent, rgba(255,255,255,0.4), transparent);
        animation: loading-shimmer 1.5s infinite;
    }
    
    @keyframes loading-shimmer {
        0% { 
            left: -100%; 
        }
        100% { 
            left: 100%; 
        }
    }
    
    img.loaded {
        opacity: 1;
        transition: opacity 0.3s ease;
    }
    
    img.error {
        opacity: 0.5;
        filter: grayscale(100%);
    }
`;
document.head.appendChild(style);