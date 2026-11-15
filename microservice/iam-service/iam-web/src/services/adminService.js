import api from '../api/axios';

export const adminService = {
    // --- Users Management ---
    getUsers: async (page = 0, size = 20, sort = 'createdAt,desc') => {
        const res = await api.get('/users', { params: { page, size, sort } });
        return res.data;
    },

    getUserByUuid: async (uuid) => {
        const res = await api.get(`/users/${uuid}`);
        return res.data;
    },

    createUser: async (userData) => {
        // userData: { username, email, password, givenName, familyName, gender, locale }
        const res = await api.post('/users', userData);
        return res.data;
    },

    updateUser: async (uuid, userData) => {
        // userData: { username, email, givenName, familyName, nickname, phoneNumber, gender, locale, birthdate }
        const res = await api.patch(`/users/${uuid}`, userData);
        return res.data;
    },

    deleteUser: async (uuid) => {
        const res = await api.delete(`/users/${uuid}`);
        return res.data;
    },

    enableUser: async (uuid) => {
        const res = await api.post(`/users/${uuid}/enable`);
        return res.data;
    },

    disableUser: async (uuid) => {
        const res = await api.post(`/users/${uuid}/disable`);
        return res.data;
    },

    lockUser: async (uuid) => {
        const res = await api.post(`/users/${uuid}/lock`);
        return res.data;
    },

    unlockUser: async (uuid) => {
        const res = await api.post(`/users/${uuid}/unlock`);
        return res.data;
    },

    resetUserPassword: async (uuid, passwordData) => {
        // passwordData: { newPassword, confirmPassword }
        const res = await api.put(`/users/${uuid}/password`, passwordData);
        return res.data;
    },

    getUserAuthorities: async (uuid) => {
        const res = await api.get(`/users/${uuid}/authorities`);
        return res.data;
    },

    assignRolesToUser: async (uuid, roleUuids) => {
        // roleUuids: string[]
        const res = await api.post(`/users/${uuid}/roles`, { roleUuids });
        return res.data;
    },

    revokeRoleFromUser: async (uuid, roleUuid, reason = 'Revoked by admin') => {
        const res = await api.delete(`/users/${uuid}/roles/${roleUuid}`, { params: { reason } });
        return res.data;
    },

    grantPermissionsToUser: async (uuid, permissionUuids) => {
        const res = await api.post(`/users/${uuid}/permissions`, { permissionUuids });
        return res.data;
    },

    revokePermissionFromUser: async (uuid, permissionUuid, reason = 'Revoked by admin') => {
        const res = await api.delete(`/users/${uuid}/permissions/${permissionUuid}`, { params: { reason } });
        return res.data;
    },

    // --- Roles Management ---
    getRoles: async (page = 0, size = 50, sort = 'name,asc') => {
        const res = await api.get('/roles', { params: { page, size, sort } });
        return res.data;
    },

    getRoleByUuid: async (uuid) => {
        const res = await api.get(`/roles/${uuid}`);
        return res.data;
    },

    createRole: async (roleData) => {
        // roleData: { name, description }
        const res = await api.post('/roles', roleData);
        return res.data;
    },

    updateRole: async (uuid, roleData) => {
        // roleData: { name, description }
        const res = await api.put(`/roles/${uuid}`, roleData);
        return res.data;
    },

    deleteRole: async (uuid) => {
        const res = await api.delete(`/roles/${uuid}`);
        return res.data;
    },

    activateRole: async (uuid) => {
        const res = await api.post(`/roles/${uuid}/activate`);
        return res.data;
    },

    deactivateRole: async (uuid) => {
        const res = await api.post(`/roles/${uuid}/deactivate`);
        return res.data;
    },

    assignPermissionsToRole: async (roleUuid, permissionUuids) => {
        const res = await api.post(`/roles/${roleUuid}/permissions`, { permissionUuids });
        return res.data;
    },

    revokePermissionFromRole: async (roleUuid, permUuid, reason = 'Revoked by admin') => {
        const res = await api.delete(`/roles/${roleUuid}/permissions/${permUuid}`, { params: { reason } });
        return res.data;
    },

    // --- Permissions Management ---
    getPermissions: async (page = 0, size = 100, sort = 'name,asc') => {
        const res = await api.get('/permissions', { params: { page, size, sort } });
        return res.data;
    },

    getPermissionByUuid: async (uuid) => {
        const res = await api.get(`/permissions/${uuid}`);
        return res.data;
    },

    createPermission: async (permData) => {
        // permData: { name, resource, action, description }
        const res = await api.post('/permissions', permData);
        return res.data;
    },

    updatePermission: async (uuid, permData) => {
        // permData: { description }
        const res = await api.patch(`/permissions/${uuid}`, permData);
        return res.data;
    },

    deletePermission: async (uuid) => {
        const res = await api.delete(`/permissions/${uuid}`);
        return res.data;
    },

    activatePermission: async (uuid) => {
        const res = await api.post(`/permissions/${uuid}/activate`);
        return res.data;
    },

    deactivatePermission: async (uuid) => {
        const res = await api.post(`/permissions/${uuid}/deactivate`);
        return res.data;
    },

    // --- OAuth2 Registered Clients Management ---
    getClients: async () => {
        const res = await api.get('/admin/clients');
        return res.data;
    },

    // --- Enterprise Audit & Compliance ---
    getEnterpriseLoginHistory: async (status = 'ALL') => {
        const res = await api.get('/admin/audit/login-history', {
            params: { status: status === 'ALL' ? undefined : status }
        });
        return res.data;
    },

    getEnterpriseSecurityActivity: async () => {
        const res = await api.get('/admin/audit/security-activity');
        return res.data;
    },

    getEnterpriseSessions: async () => {
        const res = await api.get('/admin/audit/sessions');
        return res.data;
    },

    terminateEnterpriseSession: async (sessionId) => {
        const res = await api.delete(`/admin/audit/sessions/${sessionId}`);
        return res.data;
    }
};

