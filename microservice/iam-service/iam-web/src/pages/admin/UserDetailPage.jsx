import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { adminService } from '../../services/adminService';
import {
    ArrowLeft, User, Shield, Globe, Smartphone, Laptop,
    Key, Lock, Unlock, CheckCircle2, XCircle, Edit,
    RefreshCw, KeyRound, AlertTriangle, X, Check, MoreVertical,
    Clock, Copy, Mail, Phone, Calendar, MapPin,
    Trash2, ExternalLink, Loader2, ShieldCheck, Search
} from 'lucide-react';
import CustomSelect from '../../components/CustomSelect';

export default function UserDetailPage() {
    const { uuid } = useParams();
    const navigate = useNavigate();
    const { t } = useTranslation();

    const [user, setUser] = useState(null);
    const [authorities, setAuthorities] = useState(null);
    const [identities, setIdentities] = useState([]);
    const [sessions, setSessions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [activeTab, setActiveTab] = useState('OVERVIEW'); // 'OVERVIEW' | 'ROLES' | 'IDENTITIES' | 'SESSIONS'
    const [permFilterQuery, setPermFilterQuery] = useState('');

    // System roles & permissions list for modals
    const [availableRoles, setAvailableRoles] = useState([]);
    const [availablePerms, setAvailablePerms] = useState([]);

    // Action Modals State
    const [showEditModal, setShowEditModal] = useState(false);
    const [showRoleModal, setShowRoleModal] = useState(false);
    const [showPermModal, setShowPermModal] = useState(false);
    const [showPasswordModal, setShowPasswordModal] = useState(false);

    // Form States
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
    const [showMoreActions, setShowMoreActions] = useState(false);

    useEffect(() => {
        const handleClickOutside = (e) => {
            if (!e.target.closest('.user-detail-more-dropdown')) {
                setShowMoreActions(false);
            }
        };
        document.addEventListener('click', handleClickOutside);
        return () => document.removeEventListener('click', handleClickOutside);
    }, []);

    // Toast State
    const [toast, setToast] = useState({ show: false, message: '', type: 'success' });

    const showToast = (message, type = 'success') => {
        setToast({ show: true, message, type });
        setTimeout(() => setToast({ show: false, message: '', type: 'success' }), 4000);
    };

    const isUserLocked = (u) => {
        if (!u) return false;
        if (u.accountLocked === true || u.accountLocked === 'true') return true;
        if (u.accountNonLocked === false || u.accountNonLocked === 'false') return true;
        return false;
    };

    const formatTimestamp = (ts) => {
        if (!ts) return '—';
        try {
            return new Date(ts).toLocaleString();
        } catch {
            return String(ts);
        }
    };

    const copyToClipboard = (text) => {
        if (!text) return;
        navigator.clipboard?.writeText(text);
        showToast('Copied to clipboard!');
    };

    // Load full user details
    const loadUserDetails = async () => {
        if (!uuid) return;
        setLoading(true);
        try {
            const userData = await adminService.getUserByUuid(uuid);
            setUser(userData);

            // Fetch authorities, identities, sessions and system meta concurrently
            const [authsRes, idsRes, rolesRes, permsRes] = await Promise.all([
                adminService.getUserAuthorities(uuid).catch(() => ({ roles: [], permissions: [], roleDetails: [] })),
                adminService.getUserIdentities(uuid).catch(() => []),
                adminService.getRoles(0, 100).catch(() => ({ content: [] })),
                adminService.getPermissions(0, 100).catch(() => ({ content: [] }))
            ]);

            setAuthorities(authsRes || { roles: [], permissions: [], roleDetails: [] });
            setIdentities(Array.isArray(idsRes) ? idsRes : []);
            setAvailableRoles(rolesRes.content || []);
            setAvailablePerms(permsRes.content || []);

            // Sessions fetch by username
            if (userData.username) {
                const userSessions = await adminService.getUserSessions(userData.username).catch(() => []);
                setSessions(Array.isArray(userSessions) ? userSessions : []);
            }
        } catch (err) {
            console.error('Failed to load user details:', err);
            showToast('Failed to load user information.', 'error');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadUserDetails();
    }, [uuid]);

    // Handle Lock / Unlock
    const handleToggleLock = async () => {
        if (!user) return;
        try {
            const currentlyLocked = isUserLocked(user);
            if (currentlyLocked) {
                await adminService.unlockUser(user.uuid);
                showToast(`Account @${user.username} has been UNLOCKED.`, 'success');
                setUser(prev => ({ ...prev, accountLocked: false, accountNonLocked: true }));
            } else {
                await adminService.lockUser(user.uuid);
                showToast(`Account @${user.username} has been LOCKED.`, 'error');
                setUser(prev => ({ ...prev, accountLocked: true, accountNonLocked: false }));
            }
        } catch (err) {
            showToast('Failed to update account lock status.', 'error');
        }
    };

    // Handle Enable / Disable
    const handleToggleEnable = async () => {
        if (!user) return;
        try {
            if (user.enabled) {
                await adminService.disableUser(user.uuid);
                showToast(`Account @${user.username} has been disabled.`);
                setUser(prev => ({ ...prev, enabled: false }));
            } else {
                await adminService.enableUser(user.uuid);
                showToast(`Account @${user.username} has been enabled.`);
                setUser(prev => ({ ...prev, enabled: true }));
            }
        } catch (err) {
            showToast('Failed to update account status.', 'error');
        }
    };

    // Open Edit Modal
    const openEditModal = () => {
        if (!user) return;
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

    // Save Edit Profile
    const handleSaveEdit = async (e) => {
        e.preventDefault();
        try {
            await adminService.updateUser(user.uuid, editForm);
            showToast(t('admin.users.userUpdated'));
            setShowEditModal(false);
            const fresh = await adminService.getUserByUuid(user.uuid);
            setUser(fresh);
        } catch (err) {
            showToast(err.response?.data?.message || 'Failed to update user profile.', 'error');
        }
    };

    // Open Roles Modal
    const openRoleModal = async () => {
        if (!user) return;
        try {
            const [rolesRes, auths] = await Promise.all([
                adminService.getRoles(0, 100),
                adminService.getUserAuthorities(user.uuid)
            ]);
            const freshRoles = rolesRes.content || [];
            setAvailableRoles(freshRoles);
            setAuthorities(auths);

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
            showToast('Failed to prepare role assignments.', 'error');
        }
    };

    // Save Roles
    const handleSaveRoles = async () => {
        try {
            await adminService.assignRolesToUser(user.uuid, selectedRoleUuids);
            showToast('Roles assigned successfully.');
            setShowRoleModal(false);
            const [freshAuths, freshUser] = await Promise.all([
                adminService.getUserAuthorities(user.uuid),
                adminService.getUserByUuid(user.uuid)
            ]);
            setAuthorities(freshAuths);
            setUser(freshUser);
        } catch (err) {
            showToast('Failed to update roles.', 'error');
        }
    };

    // Open Permissions Modal
    const openPermModal = async () => {
        if (!user) return;
        try {
            const auths = await adminService.getUserAuthorities(user.uuid);
            setAuthorities(auths);
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
            await adminService.grantPermissionsToUser(user.uuid, selectedPermUuids);
            showToast('Direct permissions updated successfully.');
            setShowPermModal(false);
            const freshAuths = await adminService.getUserAuthorities(user.uuid);
            setAuthorities(freshAuths);
        } catch (err) {
            showToast('Failed to update direct permissions.', 'error');
        }
    };

    // Save Password
    const handleSavePassword = async (e) => {
        e.preventDefault();
        if (passwordForm.newPassword !== passwordForm.confirmPassword) {
            showToast('Passwords do not match.', 'error');
            return;
        }
        if (passwordForm.newPassword.length < 6) {
            showToast('Password must be at least 6 characters long.', 'error');
            return;
        }
        try {
            await adminService.resetUserPassword(user.uuid, passwordForm);
            showToast(t('admin.users.passwordResetSuccess'));
            setShowPasswordModal(false);
            setPasswordForm({ newPassword: '', confirmPassword: '' });
        } catch (err) {
            showToast(err.response?.data?.message || 'Failed to reset password.', 'error');
        }
    };

    // Unlink external identity
    const handleUnlinkIdentity = async (identityUuid) => {
        if (!user) return;
        try {
            await adminService.unlinkUserIdentity(user.uuid, identityUuid);
            showToast('External account unlinked successfully.');
            setIdentities(prev => prev.filter(id => id.uuid !== identityUuid));
        } catch (err) {
            showToast('Failed to unlink external account.', 'error');
        }
    };

    // Terminate session
    const handleTerminateSession = async (sessionId) => {
        if (!user?.username) return;
        try {
            await adminService.terminateUserSession(user.username, sessionId);
            showToast('Session terminated successfully.');
            setSessions(prev => prev.filter(s => s.sessionId !== sessionId));
        } catch (err) {
            showToast('Failed to terminate session.', 'error');
        }
    };

    const genderOptions = [
        { value: 'Male', label: 'Male' },
        { value: 'Female', label: 'Female' },
        { value: 'Other', label: 'Other' }
    ];

    const localeOptions = [
        { value: 'en-US', label: 'English (United States)', badge: 'US', icon: <Globe size={18} className="text-[#253C95]" /> },
        { value: 'km-KH', label: 'ភាសាខ្មែរ (Cambodia)', badge: 'KH', icon: <Globe size={18} className="text-[#253C95]" /> }
    ];

    if (loading) {
        return (
            <div className="space-y-6">
                <div className="bg-white p-12 rounded-2xl border border-slate-200 shadow-xs text-center">
                    <RefreshCw size={24} className="animate-spin inline-block text-[#253C95] mb-2" />
                    <p className="text-slate-400">{t('common.loading')}</p>
                </div>
            </div>
        );
    }

    if (!user) {
        return (
            <div className="space-y-6">
                <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs">
                    <button
                        onClick={() => navigate('/admin/users')}
                        className="inline-flex items-center gap-2 text-sm font-semibold text-slate-600 hover:text-[#253C95]"
                    >
                        <ArrowLeft size={16} /> Back to Users
                    </button>
                    <div className="py-12 text-center">
                        <p className="text-lg font-bold text-slate-700">User Not Found</p>
                        <p className="text-sm text-slate-400 mt-1">The requested user profile does not exist or has been removed.</p>
                    </div>
                </div>
            </div>
        );
    }

    const locked = isUserLocked(user);

    return (
        <div className="space-y-6">
            {/* Toast Feedback */}
            {toast.show && (
                <div className={`fixed bottom-6 right-6 z-50 flex items-center gap-3 px-5 py-3.5 rounded-xl shadow-xl text-white font-medium text-sm transition-all duration-300 ${
                    toast.type === 'error' ? 'bg-[#DF1F28]' : 'bg-emerald-600'
                }`}>
                    {toast.type === 'error' ? <AlertTriangle size={18} /> : <CheckCircle2 size={18} />}
                    <span>{toast.message}</span>
                </div>
            )}

            {/* Header Toolbar Card */}
            <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div>
                    <div className="flex items-center gap-2 mb-2">
                        <button
                            onClick={() => navigate('/admin/users')}
                            className="inline-flex items-center gap-1.5 px-3 py-1 rounded-lg border border-slate-200 bg-slate-50 hover:bg-slate-100 text-slate-700 text-xs font-semibold transition-all cursor-pointer shadow-2xs"
                        >
                            <ArrowLeft size={14} />
                            <span>Back to Users</span>
                        </button>
                        <span className="text-xs text-slate-400 font-medium">/ User Management</span>
                    </div>
                    <h2 className="text-xl sm:text-2xl font-bold text-slate-800">
                        {user.name || `${user.givenName || ''} ${user.familyName || ''}`.trim() || user.username}
                    </h2>
                    <p className="text-sm sm:text-base text-slate-500 mt-1">
                        Manage user profile, authority assignments, linked accounts, and active web sessions.
                    </p>
                </div>
                <div className="flex items-center gap-3">
                    <button
                        onClick={loadUserDetails}
                        className="p-2.5 rounded-xl border border-slate-200 hover:bg-slate-50 text-slate-600 transition-all cursor-pointer active:scale-95"
                        title="Reload"
                    >
                        <RefreshCw size={18} className={loading ? 'animate-spin text-[#253C95]' : ''} />
                    </button>
                </div>
            </div>

            {/* Hero Profile Banner Card */}
            <div className={`bg-white rounded-2xl border p-6 shadow-xs flex flex-col lg:flex-row lg:items-center justify-between gap-6 ${
                locked ? 'border-red-300 bg-red-50/20' : 'border-slate-200'
            }`}>
                <div className="flex items-center gap-5">
                    <div className={`w-16 h-16 sm:w-20 sm:h-20 rounded-2xl font-bold text-2xl sm:text-3xl flex items-center justify-center shrink-0 shadow-sm ${
                        locked
                            ? 'bg-red-100 text-[#DF1F28] border-2 border-red-200'
                            : 'bg-[#253C95]/10 text-[#253C95] border border-[#253C95]/20'
                    }`}>
                        {user.username ? user.username.charAt(0).toUpperCase() : 'U'}
                    </div>

                    <div className="space-y-1.5">
                        <div className="flex flex-wrap items-center gap-2.5">
                            <h2 className="text-2xl font-bold text-slate-800 leading-tight">
                                {user.name || `${user.givenName || ''} ${user.familyName || ''}`.trim() || user.username}
                            </h2>
                            {locked ? (
                                <span className="inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-bold bg-red-100 text-[#DF1F28] border border-red-300">
                                    <Lock size={12} /> Account Locked
                                </span>
                            ) : user.enabled ? (
                                <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
                                    <span className="w-1.5 h-1.5 rounded-full bg-emerald-500"></span> Active
                                </span>
                            ) : (
                                <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-slate-100 text-slate-600 border border-slate-200">
                                    <span className="w-1.5 h-1.5 rounded-full bg-slate-400"></span> Disabled
                                </span>
                            )}
                            {user.emailVerified && (
                                <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-blue-50 text-blue-700 border border-blue-200">
                                    <CheckCircle2 size={12} /> Verified Email
                                </span>
                            )}
                        </div>

                        <div className="flex flex-wrap items-center gap-3 text-sm text-slate-500">
                            <span className="font-mono text-slate-700">@{user.username}</span>
                            <span>&bull;</span>
                            <span>{user.email || 'No email provided'}</span>
                            {user.phoneNumber && (
                                <>
                                    <span>&bull;</span>
                                    <span>{user.phoneNumber}</span>
                                </>
                            )}
                        </div>
                    </div>
                </div>

                {/* Management Action Bar */}
                <div className="flex items-center gap-2.5 pt-4 lg:pt-0 border-t lg:border-t-0 border-slate-100">
                    <button
                        onClick={openRoleModal}
                        className="inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-[#253C95] hover:bg-[#1d2f77] text-white text-sm font-semibold shadow-sm transition-all cursor-pointer active:scale-95"
                    >
                        <Shield size={15} />
                        <span>Assign Roles</span>
                    </button>

                    <button
                        onClick={openEditModal}
                        className="inline-flex items-center gap-1.5 px-3.5 py-2 rounded-xl border border-slate-200 bg-white hover:bg-slate-50 text-slate-700 text-sm font-semibold shadow-2xs transition-all cursor-pointer active:scale-95"
                    >
                        <Edit size={14} className="text-[#253C95]" />
                        <span>Edit Profile</span>
                    </button>

                    {/* Three-Dot More Actions Menu */}
                    <div className="relative user-detail-more-dropdown">
                        <button
                            type="button"
                            onClick={() => setShowMoreActions(prev => !prev)}
                            className={`p-2 rounded-xl border transition-all cursor-pointer shadow-2xs ${
                                showMoreActions
                                    ? 'border-[#253C95] bg-[#253C95]/5 text-[#253C95]'
                                    : 'border-slate-200 bg-white hover:bg-slate-50 text-slate-600'
                            }`}
                            title="More Actions"
                        >
                            <MoreVertical size={18} />
                        </button>

                        {showMoreActions && (
                            <div className="absolute right-0 top-full mt-2 z-40 w-64 bg-white rounded-2xl shadow-xl border border-slate-200 py-1.5 text-left animate-in fade-in zoom-in-95 duration-100">
                                {/* Lock / Unlock Security Action */}
                                <button
                                    type="button"
                                    onClick={() => {
                                        setShowMoreActions(false);
                                        handleToggleLock();
                                    }}
                                    className={`w-full flex items-center justify-between px-4 py-2.5 text-sm transition-colors text-left font-medium cursor-pointer whitespace-nowrap ${
                                        locked
                                            ? 'text-emerald-700 hover:bg-emerald-50'
                                            : 'text-[#DF1F28] hover:bg-red-50'
                                    }`}
                                >
                                    <div className="flex items-center gap-2.5 whitespace-nowrap">
                                        {locked ? <Unlock size={15} /> : <Lock size={15} />}
                                        <span className="whitespace-nowrap">{locked ? 'Unlock Account' : 'Lock Account'}</span>
                                    </div>
                                    <span className={`text-[10px] font-bold px-1.5 py-0.5 rounded uppercase whitespace-nowrap shrink-0 ml-2 ${
                                        locked ? 'bg-emerald-100 text-emerald-800' : 'bg-red-100 text-[#DF1F28]'
                                    }`}>
                                        {locked ? 'Locked' : 'Active'}
                                    </span>
                                </button>

                                {/* Direct Permissions */}
                                <button
                                    type="button"
                                    onClick={() => {
                                        setShowMoreActions(false);
                                        openPermModal();
                                    }}
                                    className="w-full flex items-center gap-2.5 px-4 py-2.5 text-sm text-slate-700 hover:bg-purple-50 hover:text-purple-700 transition-colors text-left font-medium cursor-pointer whitespace-nowrap"
                                >
                                    <Key size={15} className="text-purple-600 shrink-0" />
                                    <span className="whitespace-nowrap">Direct Permissions</span>
                                </button>

                                {/* Reset Password */}
                                <button
                                    type="button"
                                    onClick={() => {
                                        setShowMoreActions(false);
                                        setShowPasswordModal(true);
                                    }}
                                    className="w-full flex items-center gap-2.5 px-4 py-2.5 text-sm text-slate-700 hover:bg-amber-50 hover:text-amber-800 transition-colors text-left font-medium cursor-pointer whitespace-nowrap"
                                >
                                    <KeyRound size={15} className="text-amber-600 shrink-0" />
                                    <span className="whitespace-nowrap">Reset Password</span>
                                </button>

                                <div className="my-1 border-t border-slate-100"></div>

                                {/* Enable / Disable State */}
                                <button
                                    type="button"
                                    onClick={() => {
                                        setShowMoreActions(false);
                                        handleToggleEnable();
                                    }}
                                    className="w-full flex items-center justify-between px-4 py-2.5 text-sm text-slate-700 hover:bg-slate-50 transition-colors text-left font-medium cursor-pointer whitespace-nowrap"
                                >
                                    <div className="flex items-center gap-2.5 whitespace-nowrap">
                                        {user.enabled ? <XCircle size={15} className="text-slate-400 shrink-0" /> : <CheckCircle2 size={15} className="text-emerald-600 shrink-0" />}
                                        <span className="whitespace-nowrap">{user.enabled ? 'Disable Account' : 'Enable Account'}</span>
                                    </div>
                                    <span className={`text-[10px] font-bold px-1.5 py-0.5 rounded whitespace-nowrap shrink-0 ml-2 ${
                                        user.enabled ? 'bg-emerald-50 text-emerald-700' : 'bg-slate-100 text-slate-600'
                                    }`}>
                                        {user.enabled ? 'Enabled' : 'Disabled'}
                                    </span>
                                </button>
                            </div>
                        )}
                    </div>
                </div>
            </div>

            {/* Navigation Tabs Bar */}
            <div className="bg-white p-3 rounded-2xl border border-slate-200 shadow-xs flex items-center justify-between overflow-x-auto">
                <div className="flex items-center gap-2 p-1 bg-slate-100 rounded-xl overflow-x-auto">
                    <button
                        onClick={() => setActiveTab('OVERVIEW')}
                        className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-bold transition-all cursor-pointer whitespace-nowrap ${
                            activeTab === 'OVERVIEW'
                                ? 'bg-white text-[#253C95] shadow-xs'
                                : 'text-slate-600 hover:text-slate-900'
                        }`}
                    >
                        <User size={16} />
                        <span>Overview & Profile</span>
                    </button>

                    <button
                        onClick={() => setActiveTab('ROLES')}
                        className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-bold transition-all cursor-pointer whitespace-nowrap ${
                            activeTab === 'ROLES'
                                ? 'bg-white text-[#253C95] shadow-xs'
                                : 'text-slate-600 hover:text-slate-900'
                        }`}
                    >
                        <Shield size={16} />
                        <span>Roles & Security ({authorities?.roles?.length || 0})</span>
                    </button>

                    <button
                        onClick={() => setActiveTab('IDENTITIES')}
                        className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-bold transition-all cursor-pointer whitespace-nowrap ${
                            activeTab === 'IDENTITIES'
                                ? 'bg-white text-[#253C95] shadow-xs'
                                : 'text-slate-600 hover:text-slate-900'
                        }`}
                    >
                        <Globe size={16} />
                        <span>Linked Accounts ({identities.length})</span>
                    </button>

                    <button
                        onClick={() => setActiveTab('SESSIONS')}
                        className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-bold transition-all cursor-pointer whitespace-nowrap ${
                            activeTab === 'SESSIONS'
                                ? 'bg-white text-[#253C95] shadow-xs'
                                : 'text-slate-600 hover:text-slate-900'
                        }`}
                    >
                        <Smartphone size={16} />
                        <span>Active Sessions ({sessions.length})</span>
                    </button>
                </div>
            </div>

            {/* TAB CONTENT */}

            {/* TAB 1: OVERVIEW & PROFILE */}
            {activeTab === 'OVERVIEW' && (
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                    {/* Personal & Contact Information */}
                    <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-2xs space-y-5">
                        <div className="flex items-center justify-between pb-3 border-b border-slate-100">
                            <h3 className="text-base font-bold text-slate-800 flex items-center gap-2">
                                <User size={18} className="text-[#253C95]" />
                                Personal & Contact Details
                            </h3>
                            <button
                                onClick={openEditModal}
                                className="text-xs text-[#253C95] font-semibold hover:underline flex items-center gap-1"
                            >
                                <Edit size={12} /> Edit
                            </button>
                        </div>

                        <div className="space-y-4 text-sm">
                            <div className="flex items-center justify-between py-1 border-b border-slate-50">
                                <span className="text-slate-500">Full Name</span>
                                <span className="font-semibold text-slate-800">
                                    {user.name || `${user.givenName || ''} ${user.familyName || ''}`.trim() || '—'}
                                </span>
                            </div>
                            <div className="flex items-center justify-between py-1 border-b border-slate-50">
                                <span className="text-slate-500">Given / Family Name</span>
                                <span className="text-slate-700">
                                    {user.givenName || '—'} / {user.familyName || '—'}
                                </span>
                            </div>
                            <div className="flex items-center justify-between py-1 border-b border-slate-50">
                                <span className="text-slate-500">Nickname</span>
                                <span className="text-slate-700">{user.nickname || '—'}</span>
                            </div>
                            <div className="flex items-center justify-between py-1 border-b border-slate-50">
                                <span className="text-slate-500">Email Address</span>
                                <div className="flex items-center gap-2">
                                    <span className="font-semibold text-slate-800">{user.email || '—'}</span>
                                    {user.emailVerified ? (
                                        <span className="px-2 py-0.5 rounded text-[11px] font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
                                            Verified
                                        </span>
                                    ) : (
                                        <span className="px-2 py-0.5 rounded text-[11px] font-bold bg-amber-50 text-amber-700 border border-amber-200">
                                            Unverified
                                        </span>
                                    )}
                                </div>
                            </div>
                            <div className="flex items-center justify-between py-1 border-b border-slate-50">
                                <span className="text-slate-500">Phone Number</span>
                                <span className="text-slate-700">{user.phoneNumber || 'Not provided'}</span>
                            </div>
                            <div className="flex items-center justify-between py-1 border-b border-slate-50">
                                <span className="text-slate-500">Gender</span>
                                <span className="text-slate-700">{user.gender || '—'}</span>
                            </div>
                            <div className="flex items-center justify-between py-1 border-b border-slate-50">
                                <span className="text-slate-500">Locale & Language</span>
                                <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg bg-slate-50 border border-slate-200 text-xs font-semibold text-slate-700">
                                    <Globe size={14} className="text-[#253C95]" />
                                    {user.locale || 'en-US'}
                                </span>
                            </div>
                        </div>
                    </div>

                    {/* Security Lifecycle & Account State */}
                    <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-2xs space-y-5">
                        <div className="flex items-center justify-between pb-3 border-b border-slate-100">
                            <h3 className="text-base font-bold text-slate-800 flex items-center gap-2">
                                <Clock size={18} className="text-[#253C95]" />
                                Security Lifecycle & State
                            </h3>
                        </div>

                        <div className="space-y-4 text-sm">
                            <div>
                                <span className="text-slate-500 block text-xs mb-1">User Identifier (UUID)</span>
                                <div className="flex items-center justify-between gap-2 p-2.5 rounded-xl bg-slate-50 border border-slate-200 font-mono text-xs text-slate-700">
                                    <span className="truncate">{user.uuid}</span>
                                    <button
                                        onClick={() => copyToClipboard(user.uuid)}
                                        className="text-slate-400 hover:text-slate-700 p-1 shrink-0"
                                        title="Copy UUID"
                                    >
                                        <Copy size={14} />
                                    </button>
                                </div>
                            </div>
                            <div className="flex items-center justify-between py-1 border-b border-slate-50">
                                <span className="text-slate-500">Account Enabled</span>
                                <div className="flex items-center gap-2">
                                    <span className={`text-xs font-bold ${user.enabled ? 'text-emerald-600' : 'text-slate-400'}`}>
                                        {user.enabled ? 'Enabled' : 'Disabled'}
                                    </span>
                                    <button
                                        onClick={handleToggleEnable}
                                        className="text-xs text-[#253C95] hover:underline font-semibold"
                                    >
                                        {user.enabled ? 'Disable' : 'Enable'}
                                    </button>
                                </div>
                            </div>
                            <div className="flex items-center justify-between py-1 border-b border-slate-50">
                                <span className="text-slate-500">Account Lock State</span>
                                <div className="flex items-center gap-2">
                                    <span className={`text-xs font-bold ${locked ? 'text-[#DF1F28]' : 'text-emerald-600'}`}>
                                        {locked ? 'Locked' : 'Unlocked'}
                                    </span>
                                    <button
                                        onClick={handleToggleLock}
                                        className="text-xs text-[#253C95] hover:underline font-semibold"
                                    >
                                        {locked ? 'Unlock' : 'Lock'}
                                    </button>
                                </div>
                            </div>
                            <div className="flex items-center justify-between py-1 border-b border-slate-50">
                                <span className="text-slate-500">Account Created</span>
                                <span className="text-slate-700">{formatTimestamp(user.createdAt)}</span>
                            </div>
                            <div className="flex items-center justify-between py-1 border-b border-slate-50">
                                <span className="text-slate-500">Last Profile Update</span>
                                <span className="text-slate-700">{formatTimestamp(user.updatedAt)}</span>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {/* TAB 2: ROLES & SECURITY */}
            {activeTab === 'ROLES' && (
                <div className="space-y-6">
                    {/* Assigned Roles Card */}
                    <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-2xs space-y-5">
                        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pb-3 border-b border-slate-100">
                            <div>
                                <h3 className="text-base font-bold text-slate-800 flex items-center gap-2">
                                    <Shield size={18} className="text-[#253C95]" />
                                    Assigned Roles
                                </h3>
                                <p className="text-xs text-slate-400 mt-0.5">
                                    Roles grant predefined security authorities and access control matrices.
                                </p>
                            </div>
                            <button
                                onClick={openRoleModal}
                                className="px-4 py-2 rounded-xl bg-[#253C95] text-white text-sm font-semibold hover:bg-[#1d2f77] shadow-sm flex items-center gap-1.5 self-start sm:self-auto"
                            >
                                <Shield size={14} /> Assign / Modify Roles
                            </button>
                        </div>

                        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
                            {authorities?.roleDetails && authorities.roleDetails.length > 0 ? (
                                authorities.roleDetails.map(role => {
                                    const clean = role.name.replace(/^ROLE_/, '');
                                    const isAdm = clean === 'ADMIN' || clean === 'SYSTEM_ADMIN';
                                    return (
                                        <div
                                            key={role.uuid}
                                            className="p-4 rounded-xl border border-slate-200 bg-slate-50/60 flex flex-col justify-between space-y-3"
                                        >
                                            <div className="flex items-center justify-between">
                                                <span className={`px-3 py-1 rounded-lg text-xs font-bold uppercase tracking-wider ${
                                                    isAdm
                                                        ? 'bg-blue-100 text-[#253C95] border border-blue-200'
                                                        : 'bg-indigo-100 text-indigo-700 border border-indigo-200'
                                                }`}>
                                                    {clean}
                                                </span>
                                                <span className="text-[11px] font-bold uppercase tracking-wider text-slate-400">
                                                    Role
                                                </span>
                                            </div>
                                            <p className="text-xs text-slate-500 font-mono truncate">
                                                ID: {role.uuid || '—'}
                                            </p>
                                        </div>
                                    );
                                })
                            ) : authorities?.roles && authorities.roles.length > 0 ? (
                                authorities.roles.map((r, i) => {
                                    const clean = r.replace(/^ROLE_/, '');
                                    return (
                                        <div
                                            key={i}
                                            className="p-4 rounded-xl border border-slate-200 bg-slate-50/60 flex items-center justify-between"
                                        >
                                            <span className="px-3 py-1 rounded-lg text-xs font-bold uppercase tracking-wider bg-blue-100 text-[#253C95] border border-blue-200">
                                                {clean}
                                            </span>
                                            <span className="text-xs text-slate-400 font-medium">Assigned</span>
                                        </div>
                                    );
                                })
                            ) : (
                                <div className="col-span-full py-12 text-center text-slate-400 bg-slate-50 rounded-xl border border-dashed border-slate-200">
                                    <Shield size={32} className="mx-auto text-slate-300 mb-2" />
                                    <p className="text-sm font-semibold text-slate-700">No Roles Assigned</p>
                                    <p className="text-xs text-slate-400 mt-1">This user currently holds no role-based privileges.</p>
                                </div>
                            )}
                        </div>
                    </div>

                    {/* Effective Permissions Matrix */}
                    <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-2xs space-y-5">
                        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pb-3 border-b border-slate-100">
                            <div>
                                <h3 className="text-base font-bold text-slate-800 flex items-center gap-2">
                                    <Key size={18} className="text-[#253C95]" />
                                    Effective Security Authorities Matrix
                                </h3>
                                <p className="text-xs text-slate-400 mt-0.5">
                                    All fine-grained permissions computed across assigned roles and direct grants.
                                </p>
                            </div>
                            <div className="relative">
                                <Search size={15} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                                <input
                                    type="text"
                                    value={permFilterQuery}
                                    onChange={e => setPermFilterQuery(e.target.value)}
                                    placeholder="Filter authorities..."
                                    className="pl-8 pr-3 py-1.5 rounded-xl border border-slate-200 text-xs sm:text-sm w-56 focus:border-[#253C95] outline-none"
                                />
                            </div>
                        </div>

                        {(() => {
                            const allPerms = authorities?.effectivePermissions || authorities?.permissions || [];
                            const filtered = allPerms.filter(p =>
                                !permFilterQuery || p.toLowerCase().includes(permFilterQuery.toLowerCase())
                            );

                            if (allPerms.length === 0) {
                                return (
                                    <div className="py-12 text-center text-slate-400 bg-slate-50 rounded-xl border border-dashed border-slate-200">
                                        <p className="text-sm font-semibold">No permissions active for this user.</p>
                                    </div>
                                );
                            }

                            return (
                                <div className="space-y-3">
                                    <div className="text-xs text-slate-400 font-medium">
                                        Showing {filtered.length} of {allPerms.length} effective authorities
                                    </div>
                                    <div className="flex flex-wrap gap-2.5 p-2 bg-slate-50 rounded-xl border border-slate-100 max-h-72 overflow-y-auto">
                                        {filtered.map((perm, idx) => {
                                            const [resource, action] = perm.split(':');
                                            return (
                                                <span
                                                    key={idx}
                                                    className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs sm:text-sm font-mono font-medium bg-white hover:bg-slate-100 text-slate-700 border border-slate-200 shadow-2xs transition-colors"
                                                >
                                                    <span className="font-bold text-[#253C95]">{resource}</span>
                                                    {action && (
                                                        <>
                                                            <span className="text-slate-400">:</span>
                                                            <span>{action}</span>
                                                        </>
                                                    )}
                                                </span>
                                            );
                                        })}
                                    </div>
                                </div>
                            );
                        })()}
                    </div>
                </div>
            )}

            {/* TAB 3: LINKED ACCOUNTS */}
            {activeTab === 'IDENTITIES' && (
                <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-2xs space-y-5">
                    <div className="pb-3 border-b border-slate-100">
                        <h3 className="text-base font-bold text-slate-800 flex items-center gap-2">
                            <Globe size={18} className="text-[#253C95]" />
                            Federated OAuth2 & Social Identities
                        </h3>
                        <p className="text-xs text-slate-400 mt-0.5">
                            Third-party SSO providers (Google, GitHub, Microsoft) linked to this account for authentication.
                        </p>
                    </div>

                    {identities && identities.length > 0 ? (
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            {identities.map(identity => (
                                <div
                                    key={identity.uuid}
                                    className="p-5 rounded-xl border border-slate-200 bg-white shadow-2xs flex items-center justify-between gap-4"
                                >
                                    <div className="flex items-center gap-3.5">
                                        <div className="w-12 h-12 rounded-xl bg-slate-100 flex items-center justify-center font-bold text-base text-[#253C95] border border-slate-200 uppercase">
                                            {identity.provider ? identity.provider.charAt(0) : 'O'}
                                        </div>
                                        <div>
                                            <div className="flex items-center gap-2">
                                                <span className="font-bold text-slate-800 capitalize text-sm sm:text-base">
                                                    {identity.provider}
                                                </span>
                                                <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
                                                    Linked
                                                </span>
                                            </div>
                                            <p className="text-xs sm:text-sm text-slate-500 font-medium">
                                                {identity.displayName || identity.email || 'Connected Identity'}
                                            </p>
                                            {identity.linkedAt && (
                                                <p className="text-xs text-slate-400 mt-0.5">
                                                    Linked on {formatTimestamp(identity.linkedAt)}
                                                </p>
                                            )}
                                        </div>
                                    </div>

                                    <button
                                        onClick={() => {
                                            if (window.confirm(`Unlink ${identity.provider} from user @${user.username}?`)) {
                                                handleUnlinkIdentity(identity.uuid);
                                            }
                                        }}
                                        className="px-3.5 py-2 rounded-xl border border-red-200 text-[#DF1F28] hover:bg-red-50 text-xs sm:text-sm font-semibold shrink-0 transition-colors"
                                    >
                                        Unlink Account
                                    </button>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <div className="py-16 text-center text-slate-400 bg-slate-50 rounded-xl border border-dashed border-slate-200 space-y-2">
                            <Globe size={36} className="mx-auto text-slate-300" />
                            <p className="text-base font-semibold text-slate-700">No Linked Accounts</p>
                            <p className="text-xs sm:text-sm text-slate-400 max-w-sm mx-auto">
                                No federated single sign-on identities (Google, GitHub) are currently connected to this profile.
                            </p>
                        </div>
                    )}
                </div>
            )}

            {/* TAB 4: ACTIVE SESSIONS */}
            {activeTab === 'SESSIONS' && (
                <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-2xs space-y-5">
                    <div className="pb-3 border-b border-slate-100">
                        <h3 className="text-base font-bold text-slate-800 flex items-center gap-2">
                            <Smartphone size={18} className="text-[#253C95]" />
                            Active Distributed Redis Web Sessions
                        </h3>
                        <p className="text-xs text-slate-400 mt-0.5">
                            Real-time session states stored in Redis for active user devices and web browsers.
                        </p>
                    </div>

                    {sessions && sessions.length > 0 ? (
                        <div className="space-y-3.5">
                            {sessions.map((sess, idx) => (
                                <div
                                    key={sess.sessionId || idx}
                                    className="p-5 rounded-xl border border-slate-200 bg-white shadow-2xs flex flex-col sm:flex-row sm:items-center justify-between gap-4"
                                >
                                    <div className="flex items-start gap-3.5">
                                        <div className="w-12 h-12 rounded-xl bg-slate-100 flex items-center justify-center text-slate-600 border border-slate-200 shrink-0 mt-0.5">
                                            {sess.device && sess.device.toLowerCase().includes('mobile') ? (
                                                <Smartphone size={20} className="text-[#253C95]" />
                                            ) : (
                                                <Laptop size={20} className="text-[#253C95]" />
                                            )}
                                        </div>
                                        <div>
                                            <div className="flex items-center gap-2">
                                                <p className="font-bold text-slate-800 text-sm sm:text-base">
                                                    {sess.device || 'Web Browser'}
                                                </p>
                                                {sess.isCurrent && (
                                                    <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
                                                        Current Session
                                                    </span>
                                                )}
                                            </div>
                                            <div className="flex flex-wrap items-center gap-3 text-xs sm:text-sm text-slate-500 mt-1">
                                                <span>IP: <strong className="text-slate-700">{sess.ipAddress || '127.0.0.1'}</strong></span>
                                                <span>&bull;</span>
                                                <span>Last active: {formatTimestamp(sess.lastAccessedAt)}</span>
                                            </div>
                                            <p className="text-xs font-mono text-slate-400 mt-1 truncate max-w-lg">
                                                Session ID: {sess.sessionId}
                                            </p>
                                        </div>
                                    </div>

                                    <button
                                        onClick={() => {
                                            if (window.confirm(`Terminate this active session for @${user.username}?`)) {
                                                handleTerminateSession(sess.sessionId);
                                            }
                                        }}
                                        className="px-4 py-2 rounded-xl border border-red-200 text-[#DF1F28] hover:bg-red-50 text-xs sm:text-sm font-semibold shrink-0 transition-colors self-start sm:self-center"
                                    >
                                        Terminate Session
                                    </button>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <div className="py-16 text-center text-slate-400 bg-slate-50 rounded-xl border border-dashed border-slate-200 space-y-2">
                            <Smartphone size={36} className="mx-auto text-slate-300" />
                            <p className="text-base font-semibold text-slate-700">No Active Sessions</p>
                            <p className="text-xs sm:text-sm text-slate-400 max-w-sm mx-auto">
                                There are no active web or device sessions recorded in Redis for this user account.
                            </p>
                        </div>
                    )}
                </div>
            )}

            {/* --- MODAL: EDIT PROFILE --- */}
            {showEditModal && (
                <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
                    <div className="bg-white rounded-2xl max-w-lg w-full p-6 shadow-2xl border border-slate-200 space-y-4">
                        <div className="flex items-center justify-between pb-3 border-b border-slate-100">
                            <h3 className="text-lg font-bold text-slate-800">{t('admin.users.editUser')}</h3>
                            <button onClick={() => setShowEditModal(false)} className="text-slate-400 hover:text-slate-600">
                                <X size={20} />
                            </button>
                        </div>
                        <form onSubmit={handleSaveEdit} className="space-y-4">
                            <div className="grid grid-cols-2 gap-3">
                                <div>
                                    <label className="block text-xs font-semibold text-slate-600 mb-1">{t('admin.users.formGivenName')}</label>
                                    <input
                                        type="text"
                                        value={editForm.givenName}
                                        onChange={e => setEditForm({ ...editForm, givenName: e.target.value })}
                                        className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none"
                                        required
                                    />
                                </div>
                                <div>
                                    <label className="block text-xs font-semibold text-slate-600 mb-1">{t('admin.users.formFamilyName')}</label>
                                    <input
                                        type="text"
                                        value={editForm.familyName}
                                        onChange={e => setEditForm({ ...editForm, familyName: e.target.value })}
                                        className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none"
                                        required
                                    />
                                </div>
                            </div>
                            <div className="grid grid-cols-2 gap-3">
                                <div>
                                    <label className="block text-xs font-semibold text-slate-600 mb-1">Nickname</label>
                                    <input
                                        type="text"
                                        value={editForm.nickname}
                                        onChange={e => setEditForm({ ...editForm, nickname: e.target.value })}
                                        className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none"
                                    />
                                </div>
                                <div>
                                    <label className="block text-xs font-semibold text-slate-600 mb-1">{t('admin.users.formPhone')}</label>
                                    <input
                                        type="tel"
                                        value={editForm.phoneNumber}
                                        onChange={e => setEditForm({ ...editForm, phoneNumber: e.target.value })}
                                        className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none"
                                    />
                                </div>
                            </div>
                            <div className="grid grid-cols-2 gap-3">
                                <div>
                                    <label className="block text-xs font-semibold text-slate-600 mb-1">{t('admin.users.formGender')}</label>
                                    <CustomSelect
                                        options={genderOptions}
                                        value={editForm.gender}
                                        onChange={val => setEditForm({ ...editForm, gender: val })}
                                    />
                                </div>
                                <div>
                                    <label className="block text-xs font-semibold text-slate-600 mb-1">{t('admin.users.formLocale')}</label>
                                    <CustomSelect
                                        options={localeOptions}
                                        value={editForm.locale}
                                        onChange={val => setEditForm({ ...editForm, locale: val })}
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
            {showRoleModal && (
                <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
                    <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-slate-200 space-y-4">
                        <div className="flex items-center justify-between pb-3 border-b border-slate-100">
                            <div>
                                <h3 className="text-lg font-bold text-slate-800">{t('admin.users.assignRoles')}</h3>
                                <p className="text-xs text-slate-400">User: {user.username}</p>
                            </div>
                            <button onClick={() => setShowRoleModal(false)} className="text-slate-400 hover:text-slate-600">
                                <X size={20} />
                            </button>
                        </div>
                        <div className="space-y-2 max-h-64 overflow-y-auto pr-1">
                            {availableRoles.map(role => {
                                const isChecked = selectedRoleUuids.includes(role.uuid);
                                const isInactive = role.isActive === false;
                                return (
                                    <div
                                        key={role.uuid}
                                        onClick={() => {
                                            if (isInactive) return;
                                            setSelectedRoleUuids(prev =>
                                                isChecked ? prev.filter(id => id !== role.uuid) : [...prev, role.uuid]
                                            );
                                        }}
                                        className={`flex items-center justify-between p-3.5 rounded-xl border transition-all ${
                                            isInactive
                                                ? 'opacity-50 cursor-not-allowed bg-slate-50 border-slate-200'
                                                : isChecked
                                                    ? 'border-[#253C95] bg-[#253C95]/5 cursor-pointer'
                                                    : 'border-slate-200 hover:border-slate-300 cursor-pointer'
                                        }`}
                                    >
                                        <div className="flex items-center gap-3">
                                            <input
                                                type="checkbox"
                                                checked={isChecked}
                                                disabled={isInactive}
                                                readOnly
                                                className="w-4 h-4 text-[#253C95] rounded border-slate-300"
                                            />
                                            <div>
                                                <div className="flex items-center gap-2">
                                                    <span className="font-bold text-slate-800 text-sm">{role.name.replace(/^ROLE_/, '')}</span>
                                                    {isInactive && (
                                                        <span className="px-1.5 py-0.5 rounded text-[10px] font-bold bg-amber-50 text-amber-700 border border-amber-200">
                                                            Inactive
                                                        </span>
                                                    )}
                                                </div>
                                                <p className="text-xs text-slate-400">
                                                    {isInactive ? 'Cannot be assigned while inactive' : (role.isSystem ? 'System Role' : 'Custom Role')}
                                                </p>
                                            </div>
                                        </div>
                                    </div>
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
                                className="px-5 py-2 rounded-xl bg-[#253C95] text-white text-sm font-semibold hover:bg-[#1d2f77] shadow-md shadow-[#253C95]/20"
                            >
                                {t('common.save')}
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* --- MODAL: DIRECT PERMISSIONS --- */}
            {showPermModal && (
                <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
                    <div className="bg-white rounded-2xl max-w-lg w-full p-6 shadow-2xl border border-slate-200 space-y-4">
                        <div className="flex items-center justify-between pb-3 border-b border-slate-100">
                            <div>
                                <h3 className="text-lg font-bold text-slate-800">{t('admin.users.assignPerms')}</h3>
                                <p className="text-xs text-slate-400">User: {user.username}</p>
                            </div>
                            <button onClick={() => setShowPermModal(false)} className="text-slate-400 hover:text-slate-600">
                                <X size={20} />
                            </button>
                        </div>
                        <div className="space-y-2 max-h-72 overflow-y-auto pr-1">
                            {availablePerms.map(perm => {
                                const isChecked = selectedPermUuids.includes(perm.uuid);
                                return (
                                    <div
                                        key={perm.uuid}
                                        onClick={() => {
                                            setSelectedPermUuids(prev =>
                                                isChecked ? prev.filter(id => id !== perm.uuid) : [...prev, perm.uuid]
                                            );
                                        }}
                                        className={`flex items-center justify-between p-3.5 rounded-xl border cursor-pointer transition-all ${
                                            isChecked
                                                ? 'border-[#253C95] bg-[#253C95]/5'
                                                : 'border-slate-200 hover:border-slate-300'
                                        }`}
                                    >
                                        <div className="flex items-center gap-3">
                                            <input
                                                type="checkbox"
                                                checked={isChecked}
                                                readOnly
                                                className="w-4 h-4 text-[#253C95] rounded border-slate-300"
                                            />
                                            <div>
                                                <span className="font-mono font-semibold text-slate-800 text-xs sm:text-sm">{perm.name}</span>
                                                <p className="text-xs text-slate-400">{perm.description || 'System permission'}</p>
                                            </div>
                                        </div>
                                    </div>
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
                                className="px-5 py-2 rounded-xl bg-[#253C95] text-white text-sm font-semibold hover:bg-[#1d2f77] shadow-md shadow-[#253C95]/20"
                            >
                                {t('common.save')}
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* --- MODAL: RESET PASSWORD --- */}
            {showPasswordModal && (
                <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
                    <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-slate-200 space-y-4">
                        <div className="flex items-center justify-between pb-3 border-b border-slate-100">
                            <div className="flex items-center gap-2 text-amber-600">
                                <KeyRound size={20} />
                                <h3 className="text-lg font-bold text-slate-800">{t('admin.users.resetPassword')}</h3>
                            </div>
                            <button onClick={() => setShowPasswordModal(false)} className="text-slate-400 hover:text-slate-600">
                                <X size={20} />
                            </button>
                        </div>
                        <p className="text-xs text-slate-500">
                            Resetting password for <strong className="text-slate-700">@{user.username}</strong>.
                        </p>
                        <form onSubmit={handleSavePassword} className="space-y-4">
                            <div>
                                <label className="block text-xs font-semibold text-slate-600 mb-1">{t('admin.users.newPassword')}</label>
                                <input
                                    type="password"
                                    value={passwordForm.newPassword}
                                    onChange={e => setPasswordForm({ ...passwordForm, newPassword: e.target.value })}
                                    className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none"
                                    placeholder="Enter new password"
                                    required
                                />
                            </div>
                            <div>
                                <label className="block text-xs font-semibold text-slate-600 mb-1">{t('admin.users.confirmNewPassword')}</label>
                                <input
                                    type="password"
                                    value={passwordForm.confirmPassword}
                                    onChange={e => setPasswordForm({ ...passwordForm, confirmPassword: e.target.value })}
                                    className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none"
                                    placeholder="Confirm new password"
                                    required
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
        </div>
    );
}
