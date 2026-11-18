import api from '../api/axios';

export const userService = {
    // Get current user profile (+5 pts)
    getCurrentUser: async () => {
        const res = await api.get('/me');
        return res.data;
    },

    // Update current user profile (+5 pts)
    updateCurrentUser: async (profileData) => {
        // profileData: { username, givenName, nickname, phoneNumber, locale }
        const res = await api.patch('/me', profileData);
        return res.data;
    },

    // Change password (+5 pts)
    changePassword: async (passwordData) => {
        // passwordData: { currentPassword, newPassword, confirmPassword }
        const res = await api.put('/me/password', passwordData);
        return res.data;
    },

    // Self-service Reset Password (for forgot password) (+5 pts)
    resetPassword: async (resetData) => {
        // resetData: { username, email, newPassword, confirmPassword }
        const res = await api.post('/auth/reset-password', resetData);
        return res.data;
    },

    // Linked accounts (+5 pts)
    getUserIdentities: async () => {
        const res = await api.get('/me/identities');
        return res.data;
    },

    unlinkIdentity: async (identityUuid) => {
        const res = await api.delete(`/me/identities/${identityUuid}`);
        return res.data;
    },

    // Active Sessions with Redis (+10 pts)
    getActiveSessions: async () => {
        const res = await api.get('/me/sessions');
        return res.data;
    },

    terminateSession: async (sessionId) => {
        const res = await api.delete(`/me/sessions/${sessionId}`);
        return res.data;
    },

    terminateAllOtherSessions: async () => {
        const res = await api.delete('/me/sessions');
        return res.data;
    },

    // Login History & Security Activity (+5 pts)
    getLoginHistory: async (status = 'all') => {
        const res = await api.get('/me/login-history', {
            params: { status: status === 'all' ? undefined : status }
        });
        return res.data;
    },

    getSecurityActivity: async () => {
        const res = await api.get('/me/security-activity');
        return res.data;
    },

    // Account Deactivation Request
    requestDeactivation: async (reason = 'User requested account deactivation') => {
        const res = await api.post('/me/deactivate-request', { reason });
        return res.data;
    },

    // Enterprise Password Policy
    getPasswordPolicy: async () => {
        const res = await api.get('/admin/system/config/password-policy');
        return res.data;
    }
};
