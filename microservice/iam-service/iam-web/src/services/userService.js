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
    }
};
