import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { adminService } from '../../services/adminService';
import {
    Shield, Key, Plus, Search, Edit, Trash2, CheckCircle2,
    XCircle, X, AlertTriangle, RefreshCw, ChevronLeft, ChevronRight,
    Lock, Check, Settings
} from 'lucide-react';

export default function RolesPage() {
    const { t } = useTranslation();
    const [activeTab, setActiveTab] = useState('ROLES'); // 'ROLES' or 'PERMISSIONS'
    const [loading, setLoading] = useState(true);

    // Roles state
    const [roles, setRoles] = useState([]);
    const [selectedRole, setSelectedRole] = useState(null);
    const [roleForm, setRoleForm] = useState({ name: '', description: '' });
    const [showRoleModal, setShowRoleModal] = useState(false);
    const [isEditingRole, setIsEditingRole] = useState(false);

    // Role-Permissions management modal
    const [showRolePermsModal, setShowRolePermsModal] = useState(false);
    const [rolePermUuids, setRolePermUuids] = useState([]);

    // Permissions state
    const [permissions, setPermissions] = useState([]);
    const [selectedPerm, setSelectedPerm] = useState(null);
    const [permForm, setPermForm] = useState({ name: '', resource: '', action: '', description: '' });
    const [showPermModal, setShowPermModal] = useState(false);
    const [isEditingPerm, setIsEditingPerm] = useState(false);

    // Delete modal
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [deleteTarget, setDeleteTarget] = useState(null); // { type: 'ROLE'|'PERM', item }

    // Search query
    const [searchQuery, setSearchQuery] = useState('');

    // Toast
    const [toast, setToast] = useState({ show: false, message: '', type: 'success' });

    const showToast = (message, type = 'success') => {
        setToast({ show: true, message, type });
        setTimeout(() => setToast({ show: false, message: '', type: 'success' }), 4000);
    };

    const fetchData = async () => {
        setLoading(true);
        try {
            const [rolesRes, permsRes] = await Promise.all([
                adminService.getRoles(0, 50).catch(() => ({ content: [] })),
                adminService.getPermissions(0, 100).catch(() => ({ content: [] }))
            ]);
            setRoles(rolesRes.content || []);
            setPermissions(permsRes.content || []);
        } catch (err) {
            console.error('Error fetching roles and permissions', err);
            showToast('Failed to load data.', 'error');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, []);

    // Filter roles
    const filteredRoles = roles.filter(r =>
        (r.name && r.name.toLowerCase().includes(searchQuery.toLowerCase())) ||
        (r.description && r.description.toLowerCase().includes(searchQuery.toLowerCase()))
    );

    // Filter permissions
    const filteredPerms = permissions.filter(p =>
        (p.name && p.name.toLowerCase().includes(searchQuery.toLowerCase())) ||
        (p.resource && p.resource.toLowerCase().includes(searchQuery.toLowerCase())) ||
        (p.action && p.action.toLowerCase().includes(searchQuery.toLowerCase()))
    );

    // --- Role Actions ---
    const handleSaveRole = async (e) => {
        e.preventDefault();
        try {
            if (isEditingRole) {
                await adminService.updateRole(selectedRole.uuid, roleForm);
                showToast('Role updated successfully.');
            } else {
                await adminService.createRole(roleForm);
                showToast('Role created successfully.');
            }
            setShowRoleModal(false);
            fetchData();
        } catch (err) {
            showToast(err.response?.data?.message || 'Failed to save role.', 'error');
        }
    };

    const handleToggleRoleActive = async (role) => {
        const isSystem = role.isSystem ?? role.system ?? (role.name === 'ADMIN' || role.name === 'USER');
        if (isSystem) {
            showToast('System roles cannot be deactivated.', 'error');
            return;
        }
        const isActive = role.isActive ?? role.active ?? true;
        try {
            if (isActive) {
                await adminService.deactivateRole(role.uuid);
                showToast(`Deactivated role ${role.name}`);
            } else {
                await adminService.activateRole(role.uuid);
                showToast(`Activated role ${role.name}`);
            }
            fetchData();
        } catch (err) {
            showToast(err.response?.data?.message || 'Failed to toggle role state.', 'error');
        }
    };

    const openRolePermsModal = async (role) => {
        setSelectedRole(role);
        try {
            const rolePerms = await adminService.getRolePermissions(role.uuid);
            const currentPermUuids = Array.isArray(rolePerms) ? rolePerms.map(p => p.uuid) : [];
            setRolePermUuids(currentPermUuids);
            setShowRolePermsModal(true);
        } catch (err) {
            console.error('Failed to fetch role permissions from dedicated endpoint:', err);
            try {
                const fullRole = await adminService.getRoleByUuid(role.uuid);
                const currentPermUuids = fullRole.permissions ? fullRole.permissions.map(p => p.uuid) : [];
                setRolePermUuids(currentPermUuids);
                setShowRolePermsModal(true);
            } catch (fallbackErr) {
                showToast('Failed to fetch role permissions details.', 'error');
            }
        }
    };

    const handleSaveRolePerms = async () => {
        try {
            await adminService.assignPermissionsToRole(selectedRole.uuid, rolePermUuids);
            showToast('Role permissions updated successfully.');
            setShowRolePermsModal(false);
            fetchData();
        } catch (err) {
            console.error('Failed to update role permissions:', err);
            const msg = err.response?.data?.message || err.response?.data?.error || err.message || 'Failed to update role permissions.';
            showToast(msg, 'error');
        }
    };

    // --- Permission Actions ---
    const handleSavePerm = async (e) => {
        e.preventDefault();
        try {
            if (isEditingPerm) {
                await adminService.updatePermission(selectedPerm.uuid, { description: permForm.description });
                showToast('Permission updated successfully.');
            } else {
                await adminService.createPermission(permForm);
                showToast('Permission created successfully.');
            }
            setShowPermModal(false);
            fetchData();
        } catch (err) {
            showToast(err.response?.data?.message || 'Failed to save permission.', 'error');
        }
    };

    const handleTogglePermActive = async (perm) => {
        try {
            if (perm.isActive) {
                await adminService.deactivatePermission(perm.uuid);
                showToast(`Deactivated ${perm.name}`);
            } else {
                await adminService.activatePermission(perm.uuid);
                showToast(`Activated ${perm.name}`);
            }
            fetchData();
        } catch (err) {
            showToast('Failed to toggle permission state.', 'error');
        }
    };

    // --- Delete Confirmation ---
    const handleConfirmDelete = async () => {
        try {
            if (deleteTarget.type === 'ROLE') {
                await adminService.deleteRole(deleteTarget.item.uuid);
                showToast('Role deleted successfully.');
            } else {
                await adminService.deletePermission(deleteTarget.item.uuid);
                showToast('Permission deleted successfully.');
            }
            setShowDeleteModal(false);
            fetchData();
        } catch (err) {
            showToast(err.response?.data?.message || 'Failed to delete.', 'error');
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

            {/* Header */}
            <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div>
                    <h2 className="text-xl sm:text-2xl font-bold text-slate-800">{t('admin.roles.title')}</h2>
                    <p className="text-sm sm:text-base text-slate-500 mt-1">{t('admin.roles.subtitle')}</p>
                </div>
                <div className="flex items-center gap-3">
                    <button
                        onClick={fetchData}
                        className="p-2.5 rounded-xl border border-slate-200 hover:bg-slate-50 text-slate-600 transition-all cursor-pointer active:scale-95"
                        title="Reload"
                    >
                        <RefreshCw size={18} className={loading ? 'animate-spin text-[#253C95]' : ''} />
                    </button>
                    {activeTab === 'ROLES' ? (
                        <button
                            onClick={() => {
                                setIsEditingRole(false);
                                setRoleForm({ name: '', description: '' });
                                setShowRoleModal(true);
                            }}
                            className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-[#253C95] hover:bg-[#1d2f77] text-white font-semibold text-sm shadow-md shadow-[#253C95]/20 transition-all cursor-pointer active:scale-95"
                        >
                            <Plus size={18} />
                            <span>{t('admin.roles.createRole')}</span>
                        </button>
                    ) : (
                        <button
                            onClick={() => {
                                setIsEditingPerm(false);
                                setPermForm({ name: '', resource: '', action: '', description: '' });
                                setShowPermModal(true);
                            }}
                            className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-[#253C95] hover:bg-[#1d2f77] text-white font-semibold text-sm shadow-md shadow-[#253C95]/20 transition-all cursor-pointer active:scale-95"
                        >
                            <Plus size={18} />
                            <span>{t('admin.roles.createPerm')}</span>
                        </button>
                    )}
                </div>
            </div>

            {/* Navigation Tabs & Search */}
            <div className="bg-white p-3 rounded-2xl border border-slate-200 shadow-xs flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                <div className="flex items-center gap-2 p-1 bg-slate-100 rounded-xl w-fit">
                    <button
                        onClick={() => setActiveTab('ROLES')}
                        className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-bold transition-all cursor-pointer ${
                            activeTab === 'ROLES'
                                ? 'bg-white text-[#253C95] shadow-xs'
                                : 'text-slate-600 hover:text-slate-900'
                        }`}
                    >
                        <Shield size={16} />
                        <span>{t('admin.roles.rolesTab')} ({roles.length})</span>
                    </button>
                    <button
                        onClick={() => setActiveTab('PERMISSIONS')}
                        className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-bold transition-all cursor-pointer ${
                            activeTab === 'PERMISSIONS'
                                ? 'bg-white text-[#253C95] shadow-xs'
                                : 'text-slate-600 hover:text-slate-900'
                        }`}
                    >
                        <Key size={16} />
                        <span>{t('admin.roles.permsTab')} ({permissions.length})</span>
                    </button>
                </div>

                <div className="relative w-full sm:w-80">
                    <Search size={18} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
                    <input
                        type="text"
                        placeholder="Search..."
                        value={searchQuery}
                        onChange={e => setSearchQuery(e.target.value)}
                        className="w-full pl-10 pr-4 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none"
                    />
                </div>
            </div>

            {/* --- TAB CONTENT: ROLES --- */}
            {activeTab === 'ROLES' && (
                <div className="bg-white rounded-2xl border border-slate-200 shadow-xs overflow-hidden">
                    <div className="overflow-x-auto">
                        <table className="w-full text-left border-collapse">
                            <thead>
                                <tr className="bg-slate-50/80 border-b border-slate-200 text-xs sm:text-sm font-bold uppercase tracking-wider text-slate-500">
                                    <th className="px-5 py-4">Role Name</th>
                                    <th className="px-5 py-4">Description</th>
                                    <th className="px-5 py-4">Type</th>
                                    <th className="px-5 py-4">Status</th>
                                    <th className="px-5 py-4 text-right">Actions</th>
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
                                ) : filteredRoles.length === 0 ? (
                                    <tr>
                                        <td colSpan="5" className="text-center py-12 text-slate-400">
                                            No roles found.
                                        </td>
                                    </tr>
                                ) : (
                                    filteredRoles.map(role => {
                                        const isSystem = role.isSystem ?? role.system ?? (role.name === 'ADMIN' || role.name === 'USER');
                                        const isActive = role.isActive ?? role.active ?? true;
                                        return (
                                            <tr key={role.uuid} className="hover:bg-slate-50/70 transition-colors">
                                                <td className="px-5 py-4 font-bold text-slate-800 flex items-center gap-2.5 whitespace-nowrap">
                                                    <div className={`p-2 rounded-lg ${isSystem ? 'bg-amber-50 text-[#F9A825]' : 'bg-indigo-50 text-indigo-700'}`}>
                                                        <Shield size={18} />
                                                    </div>
                                                    <span className="text-sm font-bold">{role.name}</span>
                                                </td>
                                                <td className="px-5 py-4 text-sm text-slate-600">
                                                    {role.description || (isSystem ? 'Built-in system role' : '—')}
                                                </td>
                                                <td className="px-5 py-4 whitespace-nowrap">
                                                    {isSystem ? (
                                                        <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-amber-50 text-[#F9A825] border border-amber-200 flex items-center gap-1 w-fit">
                                                            <Lock size={12} /> System
                                                        </span>
                                                    ) : (
                                                        <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-slate-100 text-slate-600 border border-slate-200">
                                                            Custom
                                                        </span>
                                                    )}
                                                </td>
                                                <td className="px-5 py-4 whitespace-nowrap">
                                                    {isSystem ? (
                                                        <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200 flex items-center gap-1.5 w-fit" title="System role is permanently active">
                                                            <span className="w-1.5 h-1.5 rounded-full bg-emerald-500"></span> Active
                                                        </span>
                                                    ) : (
                                                        <button
                                                            type="button"
                                                            onClick={() => handleToggleRoleActive(role)}
                                                            className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold border transition-all cursor-pointer ${
                                                                isActive
                                                                    ? 'bg-emerald-50 text-emerald-700 border-emerald-200 hover:bg-emerald-100'
                                                                    : 'bg-slate-100 text-slate-500 border-slate-200 hover:bg-slate-200'
                                                            }`}
                                                            title={isActive ? 'Click to deactivate role' : 'Click to activate role'}
                                                        >
                                                            <span className={`w-1.5 h-1.5 rounded-full ${isActive ? 'bg-emerald-500' : 'bg-slate-400'}`}></span>
                                                            {isActive ? 'Active' : 'Inactive'}
                                                        </button>
                                                    )}
                                                </td>
                                                <td className="px-5 py-4 text-right whitespace-nowrap">
                                                    <div className="flex items-center justify-end gap-1.5">
                                                        {/* Manage Permissions */}
                                                        <button
                                                            type="button"
                                                            onClick={() => openRolePermsModal(role)}
                                                            className="px-2.5 py-1.5 rounded-lg bg-indigo-50 hover:bg-indigo-100 text-indigo-700 border border-indigo-200 text-xs font-bold flex items-center gap-1 transition-colors cursor-pointer"
                                                        >
                                                            <Key size={14} />
                                                            <span>Permissions</span>
                                                        </button>

                                                        {/* Toggle Active / Inactive */}
                                                        <button
                                                            type="button"
                                                            disabled={isSystem}
                                                            onClick={() => handleToggleRoleActive(role)}
                                                            className={`p-1.5 rounded-lg border transition-colors cursor-pointer ${
                                                                isSystem
                                                                    ? 'opacity-30 cursor-not-allowed border-slate-200 text-slate-400'
                                                                    : isActive
                                                                    ? 'border-emerald-200 bg-emerald-50 text-emerald-600 hover:bg-emerald-100'
                                                                    : 'border-slate-200 text-slate-400 hover:bg-slate-100'
                                                            }`}
                                                            title={isSystem ? 'System roles cannot be deactivated' : isActive ? 'Deactivate role' : 'Activate role'}
                                                        >
                                                            {isActive ? <CheckCircle2 size={16} /> : <XCircle size={16} />}
                                                        </button>

                                                        {/* Edit Role */}
                                                        <button
                                                            type="button"
                                                            disabled={isSystem}
                                                            onClick={() => {
                                                                setSelectedRole(role);
                                                                setIsEditingRole(true);
                                                                setRoleForm({ name: role.name, description: role.description || '' });
                                                                setShowRoleModal(true);
                                                            }}
                                                            className="p-1.5 rounded-lg border border-slate-200 text-slate-600 hover:bg-slate-100 disabled:opacity-30 cursor-pointer"
                                                            title="Edit Role"
                                                        >
                                                            <Edit size={16} />
                                                        </button>

                                                        {/* Delete Role */}
                                                        <button
                                                            type="button"
                                                            disabled={isSystem}
                                                            onClick={() => {
                                                                setDeleteTarget({ type: 'ROLE', item: role });
                                                                setShowDeleteModal(true);
                                                            }}
                                                            className="p-1.5 rounded-lg border border-red-100 text-[#DF1F28] hover:bg-red-50 disabled:opacity-30 cursor-pointer"
                                                            title="Delete Role"
                                                        >
                                                            <Trash2 size={16} />
                                                        </button>
                                                    </div>
                                                </td>
                                            </tr>
                                        );
                                    })
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            {/* --- TAB CONTENT: PERMISSIONS --- */}
            {activeTab === 'PERMISSIONS' && (
                <div className="bg-white rounded-2xl border border-slate-200 shadow-xs overflow-hidden">
                    <div className="overflow-x-auto">
                        <table className="w-full text-left border-collapse">
                            <thead>
                                <tr className="bg-slate-50/80 border-b border-slate-200 text-xs sm:text-sm font-bold uppercase tracking-wider text-slate-500">
                                    <th className="px-5 py-4">Authority String</th>
                                    <th className="px-5 py-4">Resource</th>
                                    <th className="px-5 py-4">Action</th>
                                    <th className="px-5 py-4">Description</th>
                                    <th className="px-5 py-4">Status</th>
                                    <th className="px-5 py-4 text-right">Actions</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100 text-sm sm:text-base">
                                {loading ? (
                                    <tr>
                                        <td colSpan="6" className="text-center py-12 text-slate-400">
                                            <RefreshCw size={24} className="animate-spin inline-block text-[#253C95] mb-2" />
                                            <p>{t('common.loading')}</p>
                                        </td>
                                    </tr>
                                ) : filteredPerms.length === 0 ? (
                                    <tr>
                                        <td colSpan="6" className="text-center py-12 text-slate-400">
                                            No permissions found.
                                        </td>
                                    </tr>
                                ) : (
                                    filteredPerms.map(perm => (
                                        <tr key={perm.uuid} className="hover:bg-slate-50/70 transition-colors">
                                            <td className="px-5 py-4 font-mono font-bold text-slate-800 flex items-center gap-2">
                                                <div className="p-1.5 rounded-md bg-emerald-50 text-emerald-700">
                                                    <Key size={14} />
                                                </div>
                                                <span>{perm.name}</span>
                                            </td>
                                            <td className="px-5 py-4 text-xs sm:text-sm font-semibold text-slate-700">
                                                {perm.resource || '—'}
                                            </td>
                                            <td className="px-5 py-4">
                                                <span className="px-2 py-0.5 rounded-md text-xs font-bold uppercase bg-slate-100 text-slate-700 border border-slate-200">
                                                    {perm.action || 'ALL'}
                                                </span>
                                            </td>
                                            <td className="px-5 py-4 text-xs sm:text-sm text-slate-500">
                                                {perm.description || '—'}
                                            </td>
                                            <td className="px-5 py-4">
                                                {(perm.isActive ?? perm.active ?? true) ? (
                                                    <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
                                                        Active
                                                    </span>
                                                ) : (
                                                    <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-slate-100 text-slate-500 border border-slate-200">
                                                        Inactive
                                                    </span>
                                                )}
                                            </td>
                                            <td className="px-5 py-4 text-right">
                                                <div className="flex items-center justify-end gap-2">
                                                    <button
                                                        disabled={perm.isSystem}
                                                        onClick={() => {
                                                            setSelectedPerm(perm);
                                                            setIsEditingPerm(true);
                                                            setPermForm({
                                                                name: perm.name,
                                                                resource: perm.resource,
                                                                action: perm.action,
                                                                description: perm.description || ''
                                                            });
                                                            setShowPermModal(true);
                                                        }}
                                                        className="p-1.5 rounded-lg border border-slate-200 text-slate-600 hover:bg-slate-100 disabled:opacity-30"
                                                        title="Edit Permission"
                                                    >
                                                        <Edit size={16} />
                                                    </button>
                                                    <button
                                                        disabled={perm.isSystem}
                                                        onClick={() => {
                                                            setDeleteTarget({ type: 'PERM', item: perm });
                                                            setShowDeleteModal(true);
                                                        }}
                                                        className="p-1.5 rounded-lg border border-red-100 text-[#DF1F28] hover:bg-red-50 disabled:opacity-30"
                                                        title="Delete Permission"
                                                    >
                                                        <Trash2 size={16} />
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    ))
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            {/* --- MODAL: CREATE / EDIT ROLE --- */}
            {showRoleModal && (
                <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
                    <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-slate-200 space-y-5 animate-in fade-in zoom-in duration-150">
                        <div className="flex items-center justify-between border-b border-slate-100 pb-3">
                            <h3 className="text-lg font-bold text-slate-800">
                                {isEditingRole ? t('admin.roles.editRole') : t('admin.roles.createRole')}
                            </h3>
                            <button onClick={() => setShowRoleModal(false)} className="text-slate-400 hover:text-slate-600">
                                <X size={20} />
                            </button>
                        </div>
                        <form onSubmit={handleSaveRole} className="space-y-4">
                            <div>
                                <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">{t('admin.roles.roleName')} *</label>
                                <input
                                    required
                                    type="text"
                                    value={roleForm.name}
                                    onChange={e => setRoleForm({ ...roleForm, name: e.target.value.toUpperCase() })}
                                    className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none font-bold uppercase"
                                    placeholder="e.g. MANAGER"
                                />
                            </div>
                            <div>
                                <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">{t('admin.roles.description')}</label>
                                <textarea
                                    rows="3"
                                    value={roleForm.description}
                                    onChange={e => setRoleForm({ ...roleForm, description: e.target.value })}
                                    className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none"
                                    placeholder="Brief role responsibilities..."
                                />
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

            {/* --- MODAL: MANAGE ROLE PERMISSIONS --- */}
            {showRolePermsModal && selectedRole && (
                <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
                    <div className="bg-white rounded-2xl max-w-lg w-full p-6 shadow-2xl border border-slate-200 space-y-5 animate-in fade-in zoom-in duration-150">
                        <div className="flex items-center justify-between border-b border-slate-100 pb-3">
                            <div>
                                <div className="flex items-center gap-2">
                                    <h3 className="text-lg font-bold text-slate-800">{t('admin.roles.managePerms')}</h3>
                                    {(selectedRole.isSystem || selectedRole.name === 'ADMIN' || selectedRole.name === 'USER') ? (
                                        <span className="px-2 py-0.5 rounded text-[11px] font-bold bg-amber-50 text-[#F9A825] border border-amber-200">
                                            System Role
                                        </span>
                                    ) : (
                                        <span className="px-2 py-0.5 rounded text-[11px] font-bold bg-slate-100 text-slate-600 border border-slate-200">
                                            Custom Role
                                        </span>
                                    )}
                                </div>
                                <p className="text-xs text-slate-400 mt-0.5">Role: <strong className="text-slate-700">{selectedRole.name}</strong> &bull; {rolePermUuids.length} of {permissions.length} permissions assigned</p>
                            </div>
                            <button onClick={() => setShowRolePermsModal(false)} className="text-slate-400 hover:text-slate-600 cursor-pointer">
                                <X size={20} />
                            </button>
                        </div>

                        {/* Quick Selection Toolbar */}
                        <div className="flex items-center justify-between px-1 py-1 bg-slate-50 rounded-xl text-xs font-semibold text-slate-600">
                            <span className="pl-2">Permissions Selection</span>
                            <div className="flex items-center gap-2">
                                <button
                                    type="button"
                                    onClick={() => setRolePermUuids(permissions.map(p => p.uuid))}
                                    className="px-2.5 py-1 rounded-lg bg-white border border-slate-200 text-[#253C95] hover:bg-blue-50 transition-colors cursor-pointer"
                                >
                                    Select All
                                </button>
                                <button
                                    type="button"
                                    onClick={() => setRolePermUuids([])}
                                    className="px-2.5 py-1 rounded-lg bg-white border border-slate-200 text-slate-600 hover:bg-slate-100 transition-colors cursor-pointer"
                                >
                                    Clear All
                                </button>
                            </div>
                        </div>

                        <div className="space-y-2 max-h-80 overflow-y-auto pr-1">
                            {permissions.map(perm => {
                                const isChecked = rolePermUuids.includes(perm.uuid);
                                return (
                                    <label
                                        key={perm.uuid}
                                        className={`flex items-center justify-between p-3 rounded-xl border cursor-pointer transition-all ${
                                            isChecked
                                                ? 'bg-indigo-50/70 border-indigo-200 text-indigo-900'
                                                : 'border-slate-200 hover:bg-slate-50 text-slate-700'
                                        }`}
                                    >
                                        <div className="flex items-center gap-3 min-w-0">
                                            <input
                                                type="checkbox"
                                                checked={isChecked}
                                                onChange={e => {
                                                    if (e.target.checked) {
                                                        setRolePermUuids([...rolePermUuids, perm.uuid]);
                                                    } else {
                                                        setRolePermUuids(rolePermUuids.filter(id => id !== perm.uuid));
                                                    }
                                                }}
                                                className="w-4 h-4 rounded text-indigo-600 focus:ring-indigo-500 cursor-pointer"
                                            />
                                            <div className="min-w-0">
                                                <div className="flex items-center gap-2">
                                                    <p className="text-sm font-bold font-mono text-slate-900">{perm.name}</p>
                                                    {perm.resource && (
                                                        <span className="px-1.5 py-0.2 rounded text-[10px] font-bold uppercase tracking-wider bg-slate-100 text-slate-500 border border-slate-200">
                                                            {perm.resource}
                                                        </span>
                                                    )}
                                                </div>
                                                <p className="text-xs text-slate-400 mt-0.5 truncate">{perm.description || `${perm.resource}:${perm.action}`}</p>
                                            </div>
                                        </div>
                                    </label>
                                );
                            })}
                        </div>
                        <div className="flex justify-end gap-3 pt-3 border-t border-slate-100">
                            <button
                                type="button"
                                onClick={() => setShowRolePermsModal(false)}
                                className="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 text-sm font-semibold hover:bg-slate-50 cursor-pointer"
                            >
                                {t('common.cancel')}
                            </button>
                            <button
                                type="button"
                                onClick={handleSaveRolePerms}
                                className="px-5 py-2 rounded-xl bg-indigo-600 text-white text-sm font-semibold hover:bg-indigo-700 shadow-md shadow-indigo-600/20 cursor-pointer"
                            >
                                {t('common.save')}
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* --- MODAL: CREATE / EDIT PERMISSION --- */}
            {showPermModal && (
                <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
                    <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-slate-200 space-y-5 animate-in fade-in zoom-in duration-150">
                        <div className="flex items-center justify-between border-b border-slate-100 pb-3">
                            <h3 className="text-lg font-bold text-slate-800">
                                {isEditingPerm ? t('admin.roles.editPerm') : t('admin.roles.createPerm')}
                            </h3>
                            <button onClick={() => setShowPermModal(false)} className="text-slate-400 hover:text-slate-600">
                                <X size={20} />
                            </button>
                        </div>
                        <form onSubmit={handleSavePerm} className="space-y-4">
                            {!isEditingPerm ? (
                                <>
                                    <div className="grid grid-cols-2 gap-3">
                                        <div>
                                            <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">{t('admin.roles.resource')} *</label>
                                            <input
                                                required
                                                type="text"
                                                value={permForm.resource}
                                                onChange={e => {
                                                    const res = e.target.value.toLowerCase();
                                                    setPermForm({
                                                        ...permForm,
                                                        resource: res,
                                                        name: permForm.action ? `${res}:${permForm.action}` : res
                                                    });
                                                }}
                                                className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none lowercase"
                                                placeholder="e.g. document"
                                            />
                                        </div>
                                        <div>
                                            <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">{t('admin.roles.action')} *</label>
                                            <input
                                                required
                                                type="text"
                                                value={permForm.action}
                                                onChange={e => {
                                                    const act = e.target.value.toLowerCase();
                                                    setPermForm({
                                                        ...permForm,
                                                        action: act,
                                                        name: permForm.resource ? `${permForm.resource}:${act}` : act
                                                    });
                                                }}
                                                className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none lowercase"
                                                placeholder="e.g. read"
                                            />
                                        </div>
                                    </div>
                                    <div>
                                        <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">{t('admin.roles.permName')} (Generated)</label>
                                        <input
                                            readOnly
                                            type="text"
                                            value={permForm.name}
                                            className="w-full px-3.5 py-2 rounded-xl border border-slate-200 bg-slate-50 text-slate-700 font-mono text-sm outline-none"
                                        />
                                    </div>
                                </>
                            ) : (
                                <div>
                                    <p className="text-sm font-mono font-bold text-slate-800 mb-2">{permForm.name}</p>
                                </div>
                            )}

                            <div>
                                <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">{t('admin.roles.description')}</label>
                                <textarea
                                    rows="3"
                                    value={permForm.description}
                                    onChange={e => setPermForm({ ...permForm, description: e.target.value })}
                                    className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none"
                                    placeholder="Explain the scope of this privilege..."
                                />
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
            {showDeleteModal && deleteTarget && (
                <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
                    <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-slate-200 space-y-4">
                        <div className="flex items-center gap-3 text-[#DF1F28]">
                            <div className="p-3 rounded-full bg-red-50 border border-red-100">
                                <Trash2 size={24} />
                            </div>
                            <div>
                                <h3 className="text-lg font-bold text-slate-800">
                                    {deleteTarget.type === 'ROLE' ? 'Delete Role' : 'Delete Permission'}
                                </h3>
                                <p className="text-xs text-slate-400">{deleteTarget.item.name}</p>
                            </div>
                        </div>
                        <p className="text-sm text-slate-600">
                            {deleteTarget.type === 'ROLE'
                                ? t('admin.roles.deleteRoleConfirm')
                                : t('admin.roles.deletePermConfirm')}
                        </p>
                        <div className="flex justify-end gap-3 pt-2">
                            <button
                                onClick={() => setShowDeleteModal(false)}
                                className="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 text-sm font-semibold hover:bg-slate-50"
                            >
                                {t('common.cancel')}
                            </button>
                            <button
                                onClick={handleConfirmDelete}
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

