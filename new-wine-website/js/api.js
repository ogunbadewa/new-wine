// NEW WINE CHURCH - API FUNCTIONS
// All backend communication functions

// Get API base URL from config
const API_BASE_URL = window.NewWineConfig?.API_BASE_URL || 'http://localhost:8080/api';

// Generic API request function
async function apiRequest(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
        },
    };
    
    // Add auth token if available
    const token = localStorage.getItem('adminToken');
    if (token) {
        defaultOptions.headers['Authorization'] = `Bearer ${token}`;
    }
    
    const finalOptions = {
        ...defaultOptions,
        ...options,
        headers: {
            ...defaultOptions.headers,
            ...options.headers,
        },
    };
    
    try {
        const response = await fetch(url, finalOptions);
        
        // Handle different response types
        if (finalOptions.responseType === 'blob') {
            return response.ok ? await response.blob() : null;
        }
        
        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.message || `HTTP ${response.status}: ${response.statusText}`);
        }
        
        return data;
    } catch (error) {
        console.error(`API Error (${endpoint}):`, error);
        throw error;
    }
}

// ========================================
// PUBLIC API FUNCTIONS (No Auth Required)
// ========================================

/**
 * Submit contact form
 * @param {Object} contactData - Contact form data
 * @param {string} contactData.name - Full name
 * @param {string} contactData.email - Email address
 * @param {string} [contactData.phone] - Phone number
 * @param {string} [contactData.subject] - Subject/category
 * @param {string} contactData.message - Message content
 * @param {string} [contactData.source] - Source page
 * @param {string} [contactData.requestType] - Type of request
 * @returns {Promise<Object>} API response
 */
async function submitContactForm(contactData) {
    // Validate required fields
    if (!contactData.name || !contactData.email) {
        throw new Error('Name and email are required');
    }
    
    if (!window.NewWineUtils.isValidEmail(contactData.email)) {
        throw new Error('Please enter a valid email address');
    }
    
    // Sanitize inputs
    const sanitizedData = {
        name: window.NewWineUtils.sanitizeInput(contactData.name),
        email: window.NewWineUtils.sanitizeInput(contactData.email),
        phone: contactData.phone ? window.NewWineUtils.sanitizeInput(contactData.phone) : null,
        subject: contactData.subject ? window.NewWineUtils.sanitizeInput(contactData.subject) : 'General Inquiry',
        message: window.NewWineUtils.sanitizeInput(contactData.message),
        source: contactData.source || 'Website',
        requestType: contactData.requestType || 'Contact Form',
        submissionDate: window.NewWineUtils.getCurrentTimestamp()
    };
    
    return await apiRequest('/contacts', {
        method: 'POST',
        body: JSON.stringify(sanitizedData)
    });
}

/**
 * Register for an event
 * @param {Object} registrationData - Event registration data
 * @param {string} registrationData.fullName - Full name
 * @param {string} registrationData.email - Email address
 * @param {string} [registrationData.phone] - Phone number
 * @param {string} [registrationData.emergencyContact] - Emergency contact
 * @param {string} registrationData.eventName - Event name
 * @param {string} [registrationData.specialRequests] - Special requests
 * @returns {Promise<Object>} API response
 */
async function registerForEvent(registrationData) {
    // Validate required fields
    if (!registrationData.fullName || !registrationData.email || !registrationData.eventName) {
        throw new Error('Name, email, and event name are required');
    }
    
    if (!window.NewWineUtils.isValidEmail(registrationData.email)) {
        throw new Error('Please enter a valid email address');
    }
    
    if (registrationData.phone && !window.NewWineUtils.isValidPhone(registrationData.phone)) {
        throw new Error('Please enter a valid phone number');
    }
    
    // Sanitize inputs
    const sanitizedData = {
        fullName: window.NewWineUtils.sanitizeInput(registrationData.fullName),
        email: window.NewWineUtils.sanitizeInput(registrationData.email),
        phone: registrationData.phone ? window.NewWineUtils.sanitizeInput(registrationData.phone) : null,
        emergencyContact: registrationData.emergencyContact ? window.NewWineUtils.sanitizeInput(registrationData.emergencyContact) : null,
        eventName: window.NewWineUtils.sanitizeInput(registrationData.eventName),
        specialRequests: registrationData.specialRequests ? window.NewWineUtils.sanitizeInput(registrationData.specialRequests) : null,
        registrationDate: window.NewWineUtils.getCurrentTimestamp()
    };
    
    return await apiRequest('/registrations', {
        method: 'POST',
        body: JSON.stringify(sanitizedData)
    });
}

/**
 * Get active events
 * @returns {Promise<Object>} API response with events data
 */
async function getActiveEvents() {
    return await apiRequest('/events/active');
}

// ========================================
// ADMIN API FUNCTIONS (Auth Required)
// ========================================

/**
 * Admin login
 * @param {Object} credentials - Login credentials
 * @param {string} credentials.username - Username
 * @param {string} credentials.password - Password
 * @returns {Promise<Object>} API response with token
 */
async function adminLogin(credentials) {
    if (!credentials.username || !credentials.password) {
        throw new Error('Username and password are required');
    }
    
    const response = await apiRequest('/auth/login', {
        method: 'POST',
        body: JSON.stringify(credentials)
    });
    
    // Store token if login successful
    if (response.success && response.token) {
        localStorage.setItem('adminToken', response.token);
        localStorage.setItem('adminUser', JSON.stringify(response.user));
    }
    
    return response;
}

/**
 * Admin logout
 */
function adminLogout() {
    localStorage.removeItem('adminToken');
    localStorage.removeItem('adminUser');
    window.location.href = 'admin.html';
}

/**
 * Verify admin token
 * @returns {Promise<Object>} API response
 */
async function verifyAdminToken() {
    const token = localStorage.getItem('adminToken');
    if (!token) {
        throw new Error('No token found');
    }
    
    return await apiRequest('/auth/verify');
}

/**
 * Get all registrations (admin only)
 * @returns {Promise<Object>} API response with registrations
 */
async function getAllRegistrations() {
    return await apiRequest('/admin/registrations');
}

/**
 * Get all contact forms (admin only)
 * @returns {Promise<Object>} API response with contact forms
 */
async function getAllContacts() {
    return await apiRequest('/admin/contacts');
}

/**
 * Get registrations by event (admin only)
 * @param {string} eventName - Event name
 * @returns {Promise<Object>} API response with filtered registrations
 */
async function getRegistrationsByEvent(eventName) {
    const encodedEventName = encodeURIComponent(eventName);
    return await apiRequest(`/admin/registrations/event/${encodedEventName}`);
}

/**
 * Delete registration (admin only)
 * @param {number} registrationId - Registration ID
 * @returns {Promise<Object>} API response
 */
async function deleteRegistration(registrationId) {
    return await apiRequest(`/admin/registrations/${registrationId}`, {
        method: 'DELETE'
    });
}

/**
 * Delete contact form (admin only)
 * @param {number} contactId - Contact form ID
 * @returns {Promise<Object>} API response
 */
async function deleteContact(contactId) {
    return await apiRequest(`/admin/contacts/${contactId}`, {
        method: 'DELETE'
    });
}

/**
 * Export data to CSV (admin only)
 * @param {string} [eventName] - Optional event name filter
 * @returns {Promise<Blob>} CSV blob
 */
async function exportDataToCsv(eventName = null) {
    const params = eventName ? `?event=${encodeURIComponent(eventName)}` : '';
    return await apiRequest(`/admin/export${params}`, {
        responseType: 'blob'
    });
}

/**
 * Get admin statistics (admin only)
 * @returns {Promise<Object>} API response with stats
 */
async function getAdminStats() {
    return await apiRequest('/admin/stats');
}

// ========================================
// UTILITY FUNCTIONS
// ========================================

/**
 * Check if user is logged in as admin
 * @returns {boolean} True if logged in
 */
function isAdminLoggedIn() {
    const token = localStorage.getItem('adminToken');
    return !!token;
}

/**
 * Get current admin user
 * @returns {Object|null} Admin user object or null
 */
function getCurrentAdminUser() {
    const userStr = localStorage.getItem('adminUser');
    return userStr ? JSON.parse(userStr) : null;
}

/**
 * Handle API errors with user-friendly messages
 * @param {Error} error - Error object
 * @param {string} [defaultMessage] - Default error message
 */
function handleApiError(error, defaultMessage = 'An error occurred. Please try again.') {
    console.error('API Error:', error);
    
    let message = defaultMessage;
    
    if (error.message) {
        // Check for common error types
        if (error.message.includes('network') || error.message.includes('fetch')) {
            message = 'Network error. Please check your connection and try again.';
        } else if (error.message.includes('401') || error.message.includes('Unauthorized')) {
            message = 'Session expired. Please log in again.';
            if (isAdminLoggedIn()) {
                adminLogout();
            }
        } else if (error.message.includes('400') || error.message.includes('validation')) {
            message = 'Please check your information and try again.';
        } else if (error.message.includes('404')) {
            message = 'The requested information was not found.';
        } else if (error.message.includes('500')) {
            message = 'Server error. Please try again later.';
        } else {
            message = error.message;
        }
    }
    
    // Show user-friendly error message
    if (window.NewWineUtils) {
        window.NewWineUtils.showMessage(message, 'error');
    } else {
        alert(message);
    }
}

/**
 * Download file from blob
 * @param {Blob} blob - File blob
 * @param {string} filename - Filename
 */
function downloadFile(blob, filename) {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
}

// Export functions for global use
window.NewWineAPI = {
    // Public functions
    submitContactForm,
    registerForEvent,
    getActiveEvents,
    
    // Admin functions
    adminLogin,
    adminLogout,
    verifyAdminToken,
    getAllRegistrations,
    getAllContacts,
    getRegistrationsByEvent,
    deleteRegistration,
    deleteContact,
    exportDataToCsv,
    getAdminStats,
    
    // Utility functions
    isAdminLoggedIn,
    getCurrentAdminUser,
    handleApiError,
    downloadFile
};