import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { adminService } from '../../services/adminService';
import {
    Users, Plus, Search, Filter, MoreVertical,
    Shield, Key, Lock, Unlock, CheckCircle2, XCircle,
    Trash2, Edit, RefreshCw, KeyRound, AlertTriangle,
    Eye, X, Check, ShieldAlert, ChevronLeft, ChevronRight,
    Globe, User, Mail, Phone, Calendar, MapPin, Clock,
    Smartphone, Laptop, Link2, Copy, ExternalLink, Loader2
} from 'lucide-react';
import CustomSelect from '../../components/CustomSelect';

export default function UsersPage() {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
    const [searchQuery, setSearchQuery] = useState('');
    const [statusFilter, setStatusFilter] = useState('ALL');
    const [deactivationUsernames, setDeactivationUsernames] = useState(new Set());

    // System roles & permissions list for assignment modals
    const [availableRoles, setAvailableRoles] = useState([]);
    const [availablePerms, setAvailablePerms] = useState([]);

    // Modals state
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [showEditModal, setShowEditModal] = useState(false);
    const [showRoleModal, setShowRoleModal] = useState(false);
    const [showPermModal, setShowPermModal] = useState(false);
    const [showPasswordModal, setShowPasswordModal] = useState(false);
    const [showDeleteModal, setShowDeleteModal] = useState(false);

    // Three-dot action menu state
    const [actionMenuUserId, setActionMenuUserId] = useState(null);

    useEffect(() => {
        const handleClickOutside = (e) => {
            if (!e.target.closest('.user-action-dropdown')) {
                setActionMenuUserId(null);
            }
        };
        document.addEventListener('click', handleClickOutside);
        return () => document.removeEventListener('click', handleClickOutside);
    }, []);

    // Selected user for action
    const [selectedUser, setSelectedUser] = useState(null);
    const [userAuthorities, setUserAuthorities] = useState({ roles: [], permissions: [] });

    // Notification toast
    const [toast, setToast] = useState({ show: false, message: '', type: 'success' });

    // Forms
    const [createForm, setCreateForm] = useState({
        username: '',
        email: '',
        password: '',
        givenName: '',
        familyName: '',
        gender: 'Other',
        locale: 'en-US'
    });

    const [editForm, setEditForm] = useState({
        givenName: '',
        familyName: '',
        nickname: '',
        phoneNumber: '',
        gender: 'Other',
        locale: 'en-US'
    });

    const [passwordForm, setPasswordForm] = useState({
        newPassword: '',
        confirmPassword: ''
    });

    const [selectedRoleUuids, setSelectedRoleUuids] = useState([]);
    const [selectedPermUuids, setSelectedPermUuids] = useState([]);

    const genderOptions = [
        { value: 'Male', label: 'Male' },
        { value: 'Female', label: 'Female' },
        { value: 'Other', label: 'Other' }
    ];

    const localeOptions = [
        { value: 'en-US', label: 'English (United States)', badge: 'US', icon: <Globe size={18} className="text-[#253C95]" /> },
        { value: 'km-KH', label: 'ភាសាខ្មែរ (Cambodia)', badge: 'KH', icon: <Globe size={18} className="text-[#253C95]" /> }
    ];

    const statusOptions = [
        { value: 'ALL', label: t('admin.users.statusAll') },
        { value: 'ACTIVE', label: t('admin.users.statusActive') },
        { value: 'DEACTIVATION_REQUESTED', label: 'Deactivation Requested' },
        { value: 'LOCKED', label: t('admin.users.statusLocked') },
        { value: 'DISABLED', label: t('admin.users.statusDisabled') }
    ];

    const showToast = (message, type = 'success') => {
        setToast({ show: true, message, type });
        setTimeout(() => setToast({ show: false, message: '', type: 'success' }), 4000);
    };

    const fetchUsers = async () => {
        setLoading(true);
        try {
            const [data, deacts] = await Promise.all([
                adminService.getUsers(page, 20),
                adminService.getDeactivationRequests().catch(() => [])
            ]);
            setUsers(data.content || []);
            setTotalPages(data.totalPages || 1);

            const userNames = new Set();
            if (Array.isArray(deacts)) {
                deacts.forEach(d => {
                    if (d.description) {
                        const match = d.description.match(/@([a-zA-Z0-9._-]+)/);
                        if (match) userNames.add(match[1].toLowerCase());
                    }
                });
            }
            setDeactivationUsernames(userNames);
        } catch (err) {
            console.error('Error fetching users', err);
            showToast('Failed to load user directory.', 'error');
        } finally {
            setLoading(false);
        }
    };

    const fetchSystemMeta = async () => {
        try {
            const [rolesRes, permsRes] = await Promise.all([
                adminService.getRoles(0, 100),
                adminService.getPermissions(0, 100)
            ]);
            setAvailableRoles(rolesRes.content || []);
            setAvailablePerms(permsRes.content || []);
        } catch (err) {
            console.error('Error fetching system roles/permissions', err);
        }
    };

    useEffect(() => {
        fetchUsers();
    }, [page]);

    useEffect(() => {
        fetchSystemMeta();
    }, []);

    // Helper to accurately determine locked state from any property
    const isUserLocked = (u) => {
        if (!u) return false;
        if (u.accountLocked === true || u.accountLocked === 'true') return true;
        if (u.accountNonLocked === false || u.accountNonLocked === 'false') return true;
        return false;
    };

    // Filter users locally by search & status
    const filteredUsers = users.filter(user => {
        const matchesSearch =
            (user.username && user.username.toLowerCase().includes(searchQuery.toLowerCase())) ||
            (user.email && user.email.toLowerCase().includes(searchQuery.toLowerCase())) ||
            (user.name && user.name.toLowerCase().includes(searchQuery.toLowerCase()));

        if (!matchesSearch) return false;

        const locked = isUserLocked(user);
        if (statusFilter === 'ACTIVE') return user.enabled && !locked;
        if (statusFilter === 'LOCKED') return locked;
        if (statusFilter === 'DISABLED') return !user.enabled;
        if (statusFilter === 'DEACTIVATION_REQUESTED') return user.enabled && deactivationUsernames.has((user.username || '').toLowerCase());
        return true;
    });

    // Handle Create User
    const handleCreateUser = async (e) => {
        e.preventDefault();
        try {
            await adminService.createUser(createForm);
            showToast(t('admin.users.userCreated'));
            setShowCreateModal(false);
            setCreateForm({
                username: '',
                email: '',
                password: '',
                givenName: '',
                familyName: '',
                gender: 'Other',
                locale: 'en-US'
            });
            fetchUsers();
        } catch (err) {
            showToast(err.response?.data?.message || 'Failed to create user.', 'error');
        }
    };

    // Open Edit Modal
    const openEditModal = (user) => {
        setSelectedUser(user);
        setEditForm({
            givenName: user.givenName || user.name || '',
            familyName: user.familyName || '',
            nickname: user.nickname || '',
            phoneNumber: user.phoneNumber || '',
            gender: user.gender || 'Other',
            locale: user.locale || 'en-US'
        });
        setShowEditModal(true);
    };

    // Handle Edit User
    const handleEditUser = async (e) => {
        e.preventDefault();
        try {
            await adminService.updateUser(selectedUser.uuid, editForm);
            showToast(t('admin.users.userUpdated'));
            setShowEditModal(false);
            fetchUsers();
        } catch (err) {
            showToast(err.response?.data?.message || 'Failed to update user.', 'error');
        }
    };

    // Toggle Lock / Unlock
    const handleToggleLock = async (user) => {
        try {
            const currentlyLocked = isUserLocked(user);
            if (currentlyLocked) {
                await adminService.unlockUser(user.uuid);
                showToast(`Account @${user.username} has been UNLOCKED.`, 'success');
            } else {
                await adminService.lockUser(user.uuid);
                showToast(`Account @${user.username} has been LOCKED.`, 'error');
            }
            await fetchUsers();
        } catch (err) {
            showToast('Failed to update lock status.', 'error');
        }
    };

    // Toggle Enable / Disable
    const handleToggleEnable = async (user) => {
        try {
            if (user.enabled) {
                await adminService.disableUser(user.uuid);
                showToast(`Disabled @${user.username}`);
            } else {
                await adminService.enableUser(user.uuid);
                showToast(`Enabled @${user.username}`);
            }
            await fetchUsers();
        } catch (err) {
            showToast('Failed to update enable status.', 'error');
        }
    };

    // Open Roles Modal
    const openRoleModal = async (user) => {
        setSelectedUser(user);
        try {
            // Freshly fetch roles so statuses (Active/Inactive) are 100% in sync
            const [rolesRes, auths] = await Promise.all([
                adminService.getRoles(0, 100),
                adminService.getUserAuthorities(user.uuid)
            ]);
            const freshRoles = rolesRes.content || [];
            setAvailableRoles(freshRoles);
            setUserAuthorities(auths);
            let matchedUuids = [];
            if (auths.roleDetails && auths.roleDetails.length > 0) {
                matchedUuids = auths.roleDetails.map(r => r.uuid);
            } else if (auths.roles) {
                const roleNameSet = new Set(Array.from(auths.roles).map(rn => rn.replace(/^ROLE_/, '')));
                matchedUuids = freshRoles
                    .filter(r => roleNameSet.has(r.name) || roleNameSet.has(r.name.replace(/^ROLE_/, '')))
                    .map(r => r.uuid);
            }
            setSelectedRoleUuids(matchedUuids);
            setShowRoleModal(true);
        } catch (err) {
            showToast('Failed to load user roles.', 'error');
        }
    };

    // Assign / Revoke Roles
    const handleSaveRoles = async () => {
        try {
            await adminService.assignRolesToUser(selectedUser.uuid, selectedRoleUuids);
            showToast('Roles updated successfully.');
            setShowRoleModal(false);
            await fetchUsers();
        } catch (err) {
            showToast('Failed to update roles.', 'error');
        }
    };

    // Open Permissions Modal
    const openPermModal = async (user) => {
        setSelectedUser(user);
        try {
            const auths = await adminService.getUserAuthorities(user.uuid);
            setUserAuthorities(auths);
            const permSet = new Set(auths.effectivePermissions || auths.permissions || []);
            const matchedUuids = availablePerms
                .filter(p => permSet.has(p.name))
                .map(p => p.uuid);
            setSelectedPermUuids(matchedUuids);
            setShowPermModal(true);
        } catch (err) {
            showToast('Failed to load user permissions.', 'error');
        }
    };

    // Save Permissions
    const handleSavePermissions = async () => {
        try {
            await adminService.grantPermissionsToUser(selectedUser.uuid, selectedPermUuids);
            showToast('Direct permissions updated successfully.');
            setShowPermModal(false);
            await fetchUsers();
        } catch (err) {
            showToast('Failed to update permissions.', 'error');
        }
    };

    // Open Password Reset Modal
    const openPasswordModal = (user) => {
        setSelectedUser(user);
        setPasswordForm({ newPassword: '', confirmPassword: '' });
        setShowPasswordModal(true);
    };

    // Handle Reset Password
    const handleResetPassword = async (e) => {
        e.preventDefault();
        if (passwordForm.newPassword !== passwordForm.confirmPassword) {
            showToast('Passwords do not match.', 'error');
            return;
        }
        try {
            await adminService.resetUserPassword(selectedUser.uuid, passwordForm);
            showToast(t('admin.users.passwordUpdated'));
            setShowPasswordModal(false);
        } catch (err) {
            showToast(err.response?.data?.message || 'Failed to reset password.', 'error');
        }
    };

    // Delete User
    const handleDeleteUser = async () => {
        try {
            await adminService.deleteUser(selectedUser.uuid);
            showToast('User deleted successfully.');
            setShowDeleteModal(false);
            fetchUsers();
        } catch (err) {
            showToast('Failed to delete user.', 'error');
        }
    };

    return (
        <div className="space-y-6">
            {/* Toast Notification */}
            {toast.show && (
                <div className={`fixed bottom-6 right-6 z-50 flex items-center gap-3 px-5 py-3.5 rounded-xl shadow-xl text-white font-medium text-sm transition-all duration-300 ${
                    toast.type === 'error' ? 'bg-[#DF1F28]' : 'bg-emerald-600'
                }`}>
                    {toast.type === 'error' ? <AlertTriangle size={18} /> : <CheckCircle2 size={18} />}
                    <span>{toast.message}</span>
                </div>
            )}

            {/* Header Toolbar */}
            <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div>
                    <h2 className="text-xl sm:text-2xl font-bold text-slate-800">{t('admin.users.title')}</h2>
                    <p className="text-sm sm:text-base text-slate-500 mt-1">{t('admin.users.subtitle')}</p>
                </div>
                <div className="flex items-center gap-3">
                    <button
                        onClick={fetchUsers}
                        className="p-2.5 rounded-xl border border-slate-200 hover:bg-slate-50 text-slate-600 transition-all cursor-pointer active:scale-95"
                        title="Reload"
                    >
                        <RefreshCw size={18} className={loading ? 'animate-spin text-[#253C95]' : ''} />
                    </button>
                    <button
                        onClick={() => setShowCreateModal(true)}
                        className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-[#253C95] hover:bg-[#1d2f77] text-white font-semibold text-sm shadow-md shadow-[#253C95]/20 transition-all cursor-pointer active:scale-95"
                    >
                        <Plus size={18} />
                        <span>{t('admin.users.createUser')}</span>
                    </button>
                </div>
            </div>

            {/* Filters Bar */}
            <div className="bg-white p-4 rounded-2xl border border-slate-200 flex flex-col sm:flex-row items-center justify-between gap-3 shadow-xs">
                {/* Search Input */}
                <div className="relative w-full sm:w-96">
                    <Search size={18} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
                    <input
                        type="text"
                        placeholder={t('admin.users.searchPlaceholder')}
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        className="w-full pl-10 pr-4 py-2 rounded-xl border border-slate-200 text-sm sm:text-base focus:border-[#253C95] focus:ring-2 focus:ring-[#253C95]/10 outline-none transition-all"
                    />
                </div>

                {/* Status Filter */}
                <div className="flex items-center gap-2 w-full sm:w-auto">
                    <Filter size={16} className="text-slate-400 shrink-0" />
                    <CustomSelect
                        value={statusFilter}
                        onChange={(val) => setStatusFilter(val)}
                        options={statusOptions}
                        className="w-full sm:w-48"
                    />
                </div>
            </div>

            {/* Users Table */}
            <div className="bg-white rounded-2xl border border-slate-200 shadow-xs">
                <div className="overflow-x-auto min-h-[340px] pb-8 rounded-2xl">
                    <table className="w-full text-left border-collapse">
                        <thead>
                            <tr className="bg-slate-50/80 border-b border-slate-200 text-xs sm:text-sm font-bold uppercase tracking-wider text-slate-500 whitespace-nowrap">
                                <th className="px-5 py-3.5 whitespace-nowrap">User</th>
                                <th className="px-5 py-3.5 whitespace-nowrap">Status</th>
                                <th className="px-5 py-3.5 whitespace-nowrap">Roles</th>
                                <th className="px-5 py-3.5 whitespace-nowrap">Contact</th>
                                <th className="px-5 py-3.5 whitespace-nowrap text-right">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-100 text-sm sm:text-base">
                            {loading ? (
                                <tr>
                                    <td colSpan="5" className="text-center py-12 text-slate-400">
                                        <RefreshCw size={24} className="animate-spin inline-block text-[#253C95] mb-2" />
                                        <p>{t('common.loading')}</p>
                                    </td>
                                </tr>
                            ) : filteredUsers.length === 0 ? (
                                <tr>
                                    <td colSpan="5" className="text-center py-12 text-slate-400">
                                        No users found matching current filters.
                                    </td>
                                </tr>
                            ) : (
                                filteredUsers.map((user, rowIndex) => {
                                    const isLocked = isUserLocked(user);
                                    const isNearBottom = rowIndex >= filteredUsers.length - 2 && filteredUsers.length > 2;
                                    return (
                                        <tr
                                            key={user.uuid}
                                            className={`transition-colors ${
                                                isLocked
                                                    ? 'bg-red-50/50 hover:bg-red-50/80 border-l-4 border-l-[#DF1F28]'
                                                    : 'hover:bg-slate-50/70'
                                            }`}
                                        >
                                            {/* User Identity */}
                                            <td className="px-5 py-4 whitespace-nowrap">
                                                <div
                                                    onClick={() => navigate(`/admin/users/${user.uuid}`)}
                                                    className="flex items-center gap-3 cursor-pointer group"
                                                    title="Click to view full user profile & security details"
                                                >
                                                    <div className={`w-10 h-10 rounded-full font-bold text-base flex items-center justify-center shrink-0 transition-transform group-hover:scale-105 ${
                                                        isLocked
                                                            ? 'bg-red-100 text-[#DF1F28] border border-red-200'
                                                            : 'bg-[#253C95]/10 text-[#253C95]'
                                                    }`}>
                                                        {user.username ? user.username.charAt(0).toUpperCase() : 'U'}
                                                    </div>
                                                    <div>
                                                        <div className="flex items-center gap-2">
                                                            <p className="font-bold text-slate-800 leading-snug group-hover:text-[#253C95] group-hover:underline">
                                                                {user.name || user.username}
                                                            </p>
                                                            {isLocked && (
                                                                <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded text-[11px] font-bold uppercase tracking-wider bg-red-100 text-[#DF1F28] border border-red-200">
                                                                    <Lock size={10} /> Locked
                                                                </span>
                                                            )}
                                                        </div>
                                                        <p className="text-xs sm:text-sm text-slate-400 font-normal">@{user.username} &bull; {user.email || 'No email'}</p>
                                                    </div>
                                                </div>
                                            </td>

                                            {/* Status Badges */}
                                            <td className="px-5 py-4 whitespace-nowrap">
                                                <div className="flex flex-col gap-1 items-start">
                                                    {isLocked ? (
                                                        <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold bg-red-100 text-[#DF1F28] border border-red-300 shadow-2xs">
                                                            <Lock size={12} className="text-[#DF1F28]" />
                                                            Locked
                                                        </span>
                                                    ) : user.enabled ? (
                                                        <span className="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
                                                            <span className="w-1.5 h-1.5 rounded-full bg-emerald-500"></span> Active
                                                        </span>
                                                    ) : (
                                                        <span className="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-slate-100 text-slate-600 border border-slate-200">
                                                            <span className="w-1.5 h-1.5 rounded-full bg-slate-400"></span> Disabled
                                                        </span>
                                                    )}
                                                    {user.enabled && deactivationUsernames.has((user.username || '').toLowerCase()) && (
                                                        <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[11px] font-bold bg-amber-50 text-amber-700 border border-amber-200">
                                                            <AlertTriangle size={11} className="text-amber-500" /> Deact. Requested
                                                        </span>
                                                    )}
                                                </div>
                                            </td>

                                            {/* Roles Column (ONLY renders roles) */}
                                            <td className="px-5 py-4 whitespace-nowrap">
                                                <div className="flex flex-wrap items-center gap-1.5">
                                                    {user.roles && user.roles.length > 0 ? (
                                                        user.roles.map((r, i) => {
                                                            const clean = r.replace(/^ROLE_/, '');
                                                            const isAdm = clean === 'ADMIN' || clean === 'SYSTEM_ADMIN';
                                                            return (
                                                                <span
                                                                    key={i}
                                                                    className={`inline-flex items-center px-2.5 py-0.5 rounded-lg text-xs font-bold uppercase tracking-wider border ${
                                                                        isAdm
                                                                            ? 'bg-blue-50 text-[#253C95] border-blue-200'
                                                                            : 'bg-indigo-50 text-indigo-700 border-indigo-200'
                                                                    }`}
                                                                >
                                                                    {clean}
                                                                </span>
                                                            );
                                                        })
                                                    ) : (
                                                        <span className="text-xs text-slate-400 font-medium italic">No roles</span>
                                                    )}
                                                </div>
                                            </td>

                                            {/* Contact Info */}
                                            <td className="px-5 py-4 text-xs sm:text-sm text-slate-600 whitespace-nowrap">
                                                <p>{user.phoneNumber || '—'}</p>
                                                <p className="text-slate-400">{user.locale || 'en-US'}</p>
                                            </td>

                                            {/* Actions Column */}
                                            <td className="px-5 py-4 text-right whitespace-nowrap">
                                                <div className="flex items-center justify-end gap-1.5">
                                                    {/* Primary 1: Assign Roles */}
                                                    <button
                                                        type="button"
                                                        onClick={() => openRoleModal(user)}
                                                        className="p-1.5 rounded-lg border border-indigo-200 bg-indigo-50/70 hover:bg-indigo-100 text-indigo-700 transition-colors cursor-pointer"
                                                        title={t('admin.users.assignRoles')}
                                                    >
                                                        <Shield size={16} />
                                                    </button>

                                                    {/* Primary 2: Edit Profile */}
                                                    <button
                                                        type="button"
                                                        onClick={() => openEditModal(user)}
                                                        className="p-1.5 rounded-lg border border-slate-200 hover:bg-slate-100 text-slate-600 transition-colors cursor-pointer"
                                                        title={t('admin.users.editUser')}
                                                    >
                                                        <Edit size={16} />
                                                    </button>

                                                    {/* Primary 3: Lock / Unlock Security Toggle */}
                                                    <button
                                                        type="button"
                                                        onClick={() => handleToggleLock(user)}
                                                        className={`p-1.5 rounded-lg border transition-all cursor-pointer ${
                                                            isLocked
                                                                ? 'border-emerald-300 bg-emerald-100 text-emerald-800 hover:bg-emerald-200 shadow-xs ring-1 ring-emerald-400/50'
                                                                : 'border-slate-200 text-slate-600 hover:bg-amber-50 hover:text-amber-700 hover:border-amber-200'
                                                        }`}
                                                        title={isLocked ? 'Account is LOCKED. Click to unlock.' : 'Click to lock user account.'}
                                                    >
                                                        {isLocked ? <Unlock size={16} className="text-emerald-700" /> : <Lock size={16} />}
                                                    </button>

                                                    {/* Secondary Actions: Collapsed in Three-Dot Menu */}
                                                    <div className="relative inline-block user-action-dropdown">
                                                        <button
                                                            type="button"
                                                            onClick={(e) => {
                                                                e.stopPropagation();
                                                                setActionMenuUserId(prev => prev === user.uuid ? null : user.uuid);
                                                            }}
                                                            className={`p-1.5 rounded-lg border transition-colors cursor-pointer ${
                                                                actionMenuUserId === user.uuid
                                                                    ? 'bg-slate-200 border-slate-300 text-slate-900 shadow-2xs'
                                                                    : 'border-slate-200 hover:bg-slate-100 text-slate-600'
                                                            }`}
                                                            title="More actions"
                                                        >
                                                            <MoreVertical size={16} />
                                                        </button>

                                                        {actionMenuUserId === user.uuid && (
                                                            <div
                                                                className={`absolute right-0 z-40 w-52 bg-white rounded-xl shadow-xl border border-slate-200 py-1.5 text-left animate-in fade-in zoom-in-95 duration-100 ${
                                                                    isNearBottom ? 'bottom-full mb-1.5' : 'top-full mt-1.5'
                                                                }`}
                                                                onClick={(e) => e.stopPropagation()}
                                                            >
                                                                {/* View Full Details */}
                                                                <button
                                                                    type="button"
                                                                    onClick={() => {
                                                                        setActionMenuUserId(null);
                                                                        navigate(`/admin/users/${user.uuid}`);
                                                                    }}
                                                                    className="w-full flex items-center gap-2.5 px-3.5 py-2 text-sm text-slate-700 hover:bg-blue-50 hover:text-[#253C95] transition-colors text-left font-medium cursor-pointer"
                                                                >
                                                                    <Eye size={15} className="text-[#253C95] shrink-0" />
                                                                    <span>View User Details</span>
                                                                </button>

                                                                <div className="my-1 border-t border-slate-100"></div>

                                                                {/* Direct Permissions */}
                                                                <button
                                                                    type="button"
                                                                    onClick={() => {
                                                                        setActionMenuUserId(null);
                                                                        openPermModal(user);
                                                                    }}
                                                                    className="w-full flex items-center gap-2.5 px-3.5 py-2 text-sm text-slate-700 hover:bg-purple-50 hover:text-purple-700 transition-colors text-left font-medium cursor-pointer"
                                                                >
                                                                    <Key size={15} className="text-purple-600 shrink-0" />
                                                                    <span>{t('admin.users.assignPerms')}</span>
                                                                </button>

                                                                {/* Reset Password */}
                                                                <button
                                                                    type="button"
                                                                    onClick={() => {
                                                                        setActionMenuUserId(null);
                                                                        openPasswordModal(user);
                                                                    }}
                                                                    className="w-full flex items-center gap-2.5 px-3.5 py-2 text-sm text-slate-700 hover:bg-amber-50 hover:text-amber-800 transition-colors text-left font-medium cursor-pointer"
                                                                >
                                                                    <KeyRound size={15} className="text-[#F9A825] shrink-0" />
                                                                    <span>{t('admin.users.resetPassword')}</span>
                                                                </button>

                                                                {/* Enable / Disable */}
                                                                <button
                                                                    type="button"
                                                                    onClick={() => {
                                                                        setActionMenuUserId(null);
                                                                        handleToggleEnable(user);
                                                                    }}
                                                                    className={`w-full flex items-center gap-2.5 px-3.5 py-2 text-sm transition-colors text-left font-medium cursor-pointer ${
                                                                        user.enabled
                                                                            ? 'text-slate-700 hover:bg-slate-50 hover:text-slate-900'
                                                                            : 'text-emerald-700 hover:bg-emerald-50'
                                                                    }`}
                                                                >
                                                                    {user.enabled ? (
                                                                        <>
                                                                            <XCircle size={15} className="text-slate-500 shrink-0" />
                                                                            <span>{t('admin.users.disableUser')}</span>
                                                                        </>
                                                                    ) : (
                                                                        <>
                                                                            <CheckCircle2 size={15} className="text-emerald-600 shrink-0" />
                                                                            <span>{t('admin.users.enableUser')}</span>
                                                                        </>
                                                                    )}
                                                                </button>

                                                                <div className="my-1 border-t border-slate-100"></div>

                                                                {/* Delete Account */}
                                                                <button
                                                                    type="button"
                                                                    onClick={() => {
                                                                        setActionMenuUserId(null);
                                                                        setSelectedUser(user);
                                                                        setShowDeleteModal(true);
                                                                    }}
                                                                    className="w-full flex items-center gap-2.5 px-3.5 py-2 text-sm text-[#DF1F28] hover:bg-red-50 transition-colors text-left font-medium cursor-pointer"
                                                                >
                                                                    <Trash2 size={15} className="text-[#DF1F28] shrink-0" />
                                                                    <span>{t('admin.users.deleteUser')}</span>
                                                                </button>
                                                            </div>
                                                        )}
                                                    </div>
                                                </div>
                                            </td>
                                        </tr>
                                    );
                                })
                            )}
                        </tbody>
                    </table>
                </div>

                {/* Pagination footer */}
                <div className="p-4 border-t border-slate-200 flex items-center justify-between text-xs sm:text-sm text-slate-500">
                    <p>Page {page + 1} of {totalPages}</p>
                    <div className="flex items-center gap-2">
                        <button
                            disabled={page === 0}
                            onClick={() => setPage(p => Math.max(0, p - 1))}
                            className="p-1.5 rounded-lg border border-slate-200 hover:bg-slate-50 disabled:opacity-40 cursor-pointer"
                        >
                            <ChevronLeft size={16} />
                        </button>
                        <button
                            disabled={page + 1 >= totalPages}
                            onClick={() => setPage(p => p + 1)}
                            className="p-1.5 rounded-lg border border-slate-200 hover:bg-slate-50 disabled:opacity-40 cursor-pointer"
                        >
                            <ChevronRight size={16} />
                        </button>
                    </div>
                </div>
            </div>

            {/* --- MODAL: CREATE USER --- */}
            {showCreateModal && (
                <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
                    <div className="bg-white rounded-2xl max-w-lg w-full p-6 shadow-2xl border border-slate-200 space-y-5 animate-in fade-in zoom-in duration-150">
                        <div className="flex items-center justify-between border-b border-slate-100 pb-3">
                            <h3 className="text-lg font-bold text-slate-800">{t('admin.users.createUser')}</h3>
                            <button onClick={() => setShowCreateModal(false)} className="text-slate-400 hover:text-slate-600">
                                <X size={20} />
                            </button>
                        </div>
                        <form onSubmit={handleCreateUser} className="space-y-4">
                            <div className="grid grid-cols-2 gap-3">
                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">Username *</label>
                                    <input
                                        required
                                        type="text"
                                        value={createForm.username}
                                        onChange={e => setCreateForm({ ...createForm, username: e.target.value })}
                                        className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none"
                                        placeholder="e.g. john_doe"
                                    />
                                </div>
                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">Email *</label>
                                    <input
                                        required
                                        type="email"
                                        value={createForm.email}
                                        onChange={e => setCreateForm({ ...createForm, email: e.target.value })}
                                        className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none"
                                        placeholder="john@example.com"
                                    />
                                </div>
                            </div>

                            <div>
                                <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">Initial Password *</label>
                                <input
                                    required
                                    type="password"
                                    value={createForm.password}
                                    onChange={e => setCreateForm({ ...createForm, password: e.target.value })}
                                    className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none"
                                    placeholder="At least 8 characters"
                                />
                            </div>

                            <div className="grid grid-cols-2 gap-3">
                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">Given Name</label>
                                    <input
                                        type="text"
                                        value={createForm.givenName}
                                        onChange={e => setCreateForm({ ...createForm, givenName: e.target.value })}
                                        className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none"
                                    />
                                </div>
                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">Family Name</label>
                                    <input
                                        type="text"
                                        value={createForm.familyName}
                                        onChange={e => setCreateForm({ ...createForm, familyName: e.target.value })}
                                        className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none"
                                    />
                                </div>
                            </div>

                            <div className="grid grid-cols-2 gap-3">
                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">Gender</label>
                                    <CustomSelect
                                        value={createForm.gender}
                                        onChange={val => setCreateForm({ ...createForm, gender: val })}
                                        options={genderOptions}
                                    />
                                </div>
                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">Preferred Locale</label>
                                    <CustomSelect
                                        value={createForm.locale}
                                        onChange={val => setCreateForm({ ...createForm, locale: val })}
                                        options={localeOptions}
                                    />
                                </div>
                            </div>

                            <div className="flex justify-end gap-3 pt-3 border-t border-slate-100">
                                <button
                                    type="button"
                                    onClick={() => setShowCreateModal(false)}
                                    className="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 text-sm font-semibold hover:bg-slate-50"
                                >
                                    {t('common.cancel')}
                                </button>
                                <button
                                    type="submit"
                                    className="px-5 py-2 rounded-xl bg-[#253C95] text-white text-sm font-semibold hover:bg-[#1d2f77] shadow-md shadow-[#253C95]/20"
                                >
                                    {t('common.save')}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* --- MODAL: EDIT USER --- */}
            {showEditModal && selectedUser && (
                <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
                    <div className="bg-white rounded-2xl max-w-lg w-full p-6 shadow-2xl border border-slate-200 space-y-5 animate-in fade-in zoom-in duration-150">
                        <div className="flex items-center justify-between border-b border-slate-100 pb-3">
                            <h3 className="text-lg font-bold text-slate-800">{t('admin.users.editUser')}: {selectedUser.username}</h3>
                            <button onClick={() => setShowEditModal(false)} className="text-slate-400 hover:text-slate-600">
                                <X size={20} />
                            </button>
                        </div>
                        <form onSubmit={handleEditUser} className="space-y-4">
                            <div className="grid grid-cols-2 gap-3">
                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">Given Name</label>
                                    <input
                                        type="text"
                                        value={editForm.givenName}
                                        onChange={e => setEditForm({ ...editForm, givenName: e.target.value })}
                                        className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none"
                                    />
                                </div>
                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">Family Name</label>
                                    <input
                                        type="text"
                                        value={editForm.familyName}
                                        onChange={e => setEditForm({ ...editForm, familyName: e.target.value })}
                                        className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none"
                                    />
                                </div>
                            </div>

                            <div className="grid grid-cols-2 gap-3">
                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">Nickname</label>
                                    <input
                                        type="text"
                                        value={editForm.nickname}
                                        onChange={e => setEditForm({ ...editForm, nickname: e.target.value })}
                                        className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none"
                                    />
                                </div>
                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">Phone Number</label>
                                    <input
                                        type="text"
                                        value={editForm.phoneNumber}
                                        onChange={e => setEditForm({ ...editForm, phoneNumber: e.target.value })}
                                        className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none"
                                    />
                                </div>
                            </div>

                            <div className="grid grid-cols-2 gap-3">
                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">Gender</label>
                                    <CustomSelect
                                        value={editForm.gender}
                                        onChange={val => setEditForm({ ...editForm, gender: val })}
                                        options={genderOptions}
                                    />
                                </div>
                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">Preferred Locale</label>
                                    <CustomSelect
                                        value={editForm.locale}
                                        onChange={val => setEditForm({ ...editForm, locale: val })}
                                        options={localeOptions}
                                    />
                                </div>
                            </div>

                            <div className="flex justify-end gap-3 pt-3 border-t border-slate-100">
                                <button
                                    type="button"
                                    onClick={() => setShowEditModal(false)}
                                    className="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 text-sm font-semibold hover:bg-slate-50"
                                >
                                    {t('common.cancel')}
                                </button>
                                <button
                                    type="submit"
                                    className="px-5 py-2 rounded-xl bg-[#253C95] text-white text-sm font-semibold hover:bg-[#1d2f77] shadow-md shadow-[#253C95]/20"
                                >
                                    {t('common.save')}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* --- MODAL: ASSIGN ROLES --- */}
            {showRoleModal && selectedUser && (
                <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
                    <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-slate-200 space-y-5">
                        <div className="flex items-center justify-between border-b border-slate-100 pb-3">
                            <div>
                                <h3 className="text-lg font-bold text-slate-800">{t('admin.users.assignRoles')}</h3>
                                <p className="text-xs text-slate-400">User: {selectedUser.username}</p>
                            </div>
                            <button onClick={() => setShowRoleModal(false)} className="text-slate-400 hover:text-slate-600">
                                <X size={20} />
                            </button>
                        </div>
                        <div className="space-y-2.5 max-h-72 overflow-y-auto pr-1">
                            {availableRoles.map(role => {
                                const isChecked = selectedRoleUuids.includes(role.uuid);
                                const isSystem = role.isSystem ?? role.system ?? (role.name === 'ADMIN' || role.name === 'USER');
                                const isActive = role.isActive ?? role.active ?? true;
                                const canToggle = isActive || isChecked; // If inactive and not currently assigned, disable selection

                                return (
                                    <label
                                        key={role.uuid}
                                        className={`flex items-center justify-between p-3.5 rounded-xl border transition-all ${
                                            !isActive && !isChecked
                                                ? 'bg-slate-50/70 border-slate-200 opacity-60 cursor-not-allowed'
                                                : isChecked
                                                ? 'bg-indigo-50/70 border-indigo-200 text-indigo-900 cursor-pointer'
                                                : 'border-slate-200 hover:bg-slate-50 text-slate-700 cursor-pointer'
                                        }`}
                                    >
                                        <div className="flex items-center gap-3 min-w-0">
                                            <input
                                                type="checkbox"
                                                disabled={!canToggle}
                                                checked={isChecked}
                                                onChange={(e) => {
                                                    if (e.target.checked) {
                                                        setSelectedRoleUuids([...selectedRoleUuids, role.uuid]);
                                                    } else {
                                                        setSelectedRoleUuids(selectedRoleUuids.filter(id => id !== role.uuid));
                                                    }
                                                }}
                                                className="w-4 h-4 rounded text-indigo-600 focus:ring-indigo-500 cursor-pointer disabled:cursor-not-allowed"
                                            />
                                            <div className="min-w-0">
                                                <div className="flex items-center gap-2 flex-wrap">
                                                    <p className="text-sm font-bold text-slate-900">{role.name}</p>
                                                    {isSystem ? (
                                                        <span className="px-2 py-0.5 rounded text-[11px] font-bold bg-amber-50 text-[#F9A825] border border-amber-200">
                                                            System Role
                                                        </span>
                                                    ) : (
                                                        <span className="px-2 py-0.5 rounded text-[11px] font-bold bg-slate-100 text-slate-600 border border-slate-200">
                                                            Custom
                                                        </span>
                                                    )}
                                                    {!isActive && (
                                                        <span className="px-2 py-0.5 rounded text-[11px] font-bold bg-red-50 text-[#DF1F28] border border-red-200">
                                                            Inactive
                                                        </span>
                                                    )}
                                                </div>
                                                <p className="text-xs text-slate-500 mt-0.5 truncate">
                                                    {!isActive && !isChecked
                                                        ? 'Inactive role cannot be assigned. Activate it in Roles & Permissions.'
                                                        : role.description || (isSystem ? 'System authority role' : 'Custom application role')}
                                                </p>
                                            </div>
                                        </div>
                                    </label>
                                );
                            })}
                        </div>
                        <div className="flex justify-end gap-3 pt-3 border-t border-slate-100">
                            <button
                                type="button"
                                onClick={() => setShowRoleModal(false)}
                                className="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 text-sm font-semibold hover:bg-slate-50"
                            >
                                {t('common.cancel')}
                            </button>
                            <button
                                type="button"
                                onClick={handleSaveRoles}
                                className="px-5 py-2 rounded-xl bg-indigo-600 text-white text-sm font-semibold hover:bg-indigo-700 shadow-md shadow-indigo-600/20"
                            >
                                {t('common.save')}
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* --- MODAL: DIRECT PERMISSIONS --- */}
            {showPermModal && selectedUser && (
                <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
                    <div className="bg-white rounded-2xl max-w-lg w-full p-6 shadow-2xl border border-slate-200 space-y-5">
                        <div className="flex items-center justify-between border-b border-slate-100 pb-3">
                            <div>
                                <h3 className="text-lg font-bold text-slate-800">{t('admin.users.assignPerms')}</h3>
                                <p className="text-xs text-slate-400">User: {selectedUser.username}</p>
                            </div>
                            <button onClick={() => setShowPermModal(false)} className="text-slate-400 hover:text-slate-600">
                                <X size={20} />
                            </button>
                        </div>
                        <div className="space-y-2 max-h-72 overflow-y-auto pr-1">
                            {availablePerms.map(perm => {
                                const isChecked = selectedPermUuids.includes(perm.uuid);
                                return (
                                    <label
                                        key={perm.uuid}
                                        className={`flex items-center justify-between p-3 rounded-xl border cursor-pointer transition-all ${
                                            isChecked
                                                ? 'bg-emerald-50/70 border-emerald-200 text-emerald-900'
                                                : 'border-slate-200 hover:bg-slate-50 text-slate-700'
                                        }`}
                                    >
                                        <div className="flex items-center gap-3">
                                            <input
                                                type="checkbox"
                                                checked={isChecked}
                                                onChange={(e) => {
                                                    if (e.target.checked) {
                                                        setSelectedPermUuids([...selectedPermUuids, perm.uuid]);
                                                    } else {
                                                        setSelectedPermUuids(selectedPermUuids.filter(id => id !== perm.uuid));
                                                    }
                                                }}
                                                className="w-4 h-4 rounded text-emerald-600 focus:ring-emerald-500"
                                            />
                                            <div>
                                                <p className="text-sm font-bold font-mono">{perm.name}</p>
                                                <p className="text-xs text-slate-400">{perm.description || `${perm.resource}:${perm.action}`}</p>
                                            </div>
                                        </div>
                                    </label>
                                );
                            })}
                        </div>
                        <div className="flex justify-end gap-3 pt-3 border-t border-slate-100">
                            <button
                                type="button"
                                onClick={() => setShowPermModal(false)}
                                className="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 text-sm font-semibold hover:bg-slate-50"
                            >
                                {t('common.cancel')}
                            </button>
                            <button
                                type="button"
                                onClick={handleSavePermissions}
                                className="px-5 py-2 rounded-xl bg-emerald-600 text-white text-sm font-semibold hover:bg-emerald-700 shadow-md shadow-emerald-600/20"
                            >
                                {t('common.save')}
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* --- MODAL: RESET PASSWORD --- */}
            {showPasswordModal && selectedUser && (
                <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
                    <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-slate-200 space-y-5">
                        <div className="flex items-center justify-between border-b border-slate-100 pb-3">
                            <div>
                                <h3 className="text-lg font-bold text-slate-800">{t('admin.users.resetPassword')}</h3>
                                <p className="text-xs text-slate-400">User: {selectedUser.username}</p>
                            </div>
                            <button onClick={() => setShowPasswordModal(false)} className="text-slate-400 hover:text-slate-600">
                                <X size={20} />
                            </button>
                        </div>
                        <form onSubmit={handleResetPassword} className="space-y-4">
                            <div>
                                <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">New Password *</label>
                                <input
                                    required
                                    type="password"
                                    value={passwordForm.newPassword}
                                    onChange={e => setPasswordForm({ ...passwordForm, newPassword: e.target.value })}
                                    className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none"
                                    placeholder="Enter new password"
                                />
                            </div>
                            <div>
                                <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">Confirm Password *</label>
                                <input
                                    required
                                    type="password"
                                    value={passwordForm.confirmPassword}
                                    onChange={e => setPasswordForm({ ...passwordForm, confirmPassword: e.target.value })}
                                    className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none"
                                    placeholder="Confirm new password"
                                />
                            </div>
                            <div className="flex justify-end gap-3 pt-3 border-t border-slate-100">
                                <button
                                    type="button"
                                    onClick={() => setShowPasswordModal(false)}
                                    className="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 text-sm font-semibold hover:bg-slate-50"
                                >
                                    {t('common.cancel')}
                                </button>
                                <button
                                    type="submit"
                                    className="px-5 py-2 rounded-xl bg-[#253C95] text-white text-sm font-semibold hover:bg-[#1d2f77] shadow-md shadow-[#253C95]/20"
                                >
                                    {t('common.save')}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* --- MODAL: CONFIRM DELETE --- */}
            {showDeleteModal && selectedUser && (
                <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
                    <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-slate-200 space-y-4">
                        <div className="flex items-center gap-3 text-[#DF1F28]">
                            <div className="p-3 rounded-full bg-red-50 border border-red-100">
                                <Trash2 size={24} />
                            </div>
                            <div>
                                <h3 className="text-lg font-bold text-slate-800">{t('admin.users.deleteUser')}</h3>
                                <p className="text-xs text-slate-400">{selectedUser.username}</p>
                            </div>
                        </div>
                        <p className="text-sm text-slate-600">{t('admin.users.confirmDelete')}</p>
                        <p className="text-xs text-slate-400 bg-slate-50 p-3 rounded-xl border border-slate-100">
                            {t('admin.users.deleteWarning')}
                        </p>
                        <div className="flex justify-end gap-3 pt-2">
                            <button
                                onClick={() => setShowDeleteModal(false)}
                                className="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 text-sm font-semibold hover:bg-slate-50"
                            >
                                {t('common.cancel')}
                            </button>
                            <button
                                onClick={handleDeleteUser}
                                className="px-5 py-2 rounded-xl bg-[#DF1F28] text-white text-sm font-semibold hover:bg-[#b81820] shadow-md shadow-red-500/20"
                            >
                                {t('common.delete')}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

