import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { adminService } from '../../services/adminService';
import {
    ShieldCheck, Plus, Search, Key, Globe, Lock, Unlock,
    CheckCircle2, AlertTriangle, RefreshCw, Copy, Check,
    ExternalLink, Layers, Clock, Shield, Loader2, Edit3, Trash2,
    Eye, EyeOff, Smartphone, Server, Sparkles
} from 'lucide-react';

export default function ClientsPage() {
    const { t } = useTranslation();
    const [searchQuery, setSearchQuery] = useState('');
    const [showModal, setShowModal] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [editingClientId, setEditingClientId] = useState(null);
    const [deleteCandidate, setDeleteCandidate] = useState(null);
    const [copiedId, setCopiedId] = useState(null);
    const [showSecret, setShowSecret] = useState(false);
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);

    const [clients, setClients] = useState([]);

    // Toast
    const [toast, setToast] = useState({ show: false, message: '', type: 'success' });
    const showToast = (message, type = 'success') => {
        setToast({ show: true, message, type });
        setTimeout(() => setToast({ show: false, message: '', type: 'success' }), 3500);
    };

    // Client form state
    const initialFormState = {
        clientId: '',
        clientName: '',
        clientType: 'PUBLIC', // 'PUBLIC' or 'CONFIDENTIAL'
        clientSecret: '',
        grantTypes: ['authorization_code', 'refresh_token'],
        redirectUri: '',
        scopes: 'openid, profile',
        requirePkce: true,
        requireConsent: true
    };
    const [clientForm, setClientForm] = useState(initialFormState);

    const fetchClients = async () => {
        setLoading(true);
        try {
            const data = await adminService.getClients();
            if (Array.isArray(data)) {
                setClients(data);
            }
        } catch (err) {
            console.error('Failed to load registered clients', err);
            showToast('Failed to load registered clients', 'error');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchClients();
    }, []);

    const handleCopy = (text, id) => {
        navigator.clipboard.writeText(text);
        setCopiedId(id);
        showToast(`Copied ${text} to clipboard!`);
        setTimeout(() => setCopiedId(null), 2000);
    };

    const handleGenerateSecret = () => {
        const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_#$!';
        let result = '';
        for (let i = 0; i < 32; i++) {
            result += chars.charAt(Math.floor(Math.random() * chars.length));
        }
        setClientForm(prev => ({ ...prev, clientSecret: result }));
        setShowSecret(true);
    };

    const handleClientTypeChange = (type) => {
        if (type === 'PUBLIC') {
            setClientForm(prev => ({
                ...prev,
                clientType: 'PUBLIC',
                clientSecret: '',
                requirePkce: true,
                grantTypes: prev.grantTypes.filter(g => g !== 'client_credentials')
            }));
        } else {
            setClientForm(prev => ({
                ...prev,
                clientType: 'CONFIDENTIAL',
                clientSecret: prev.clientSecret || 'secret_' + Math.random().toString(36).substring(2, 10),
                grantTypes: prev.grantTypes.includes('authorization_code')
                    ? prev.grantTypes
                    : ['authorization_code', 'refresh_token']
            }));
        }
    };

    const handleGrantTypeToggle = (gt) => {
        setClientForm(prev => {
            const exists = prev.grantTypes.includes(gt);
            const updated = exists ? prev.grantTypes.filter(g => g !== gt) : [...prev.grantTypes, gt];
            return { ...prev, grantTypes: updated };
        });
    };

    const openCreateModal = () => {
        setIsEditing(false);
        setEditingClientId(null);
        setShowSecret(false);
        setClientForm(initialFormState);
        setShowModal(true);
    };

    const openEditModal = (client) => {
        setIsEditing(true);
        setEditingClientId(client.id);
        setShowSecret(false);
        setClientForm({
            clientId: client.clientId,
            clientName: client.clientName || client.clientId,
            clientType: client.clientType || 'PUBLIC',
            clientSecret: '',
            grantTypes: client.grantTypes || ['authorization_code', 'refresh_token'],
            redirectUri: (client.redirectUris || []).join(', '),
            scopes: (client.scopes || []).join(', '),
            requirePkce: client.requirePkce ?? true,
            requireConsent: client.requireConsent ?? true
        });
        setShowModal(true);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSubmitting(true);
        try {
            const payload = {
                clientId: clientForm.clientId.trim(),
                clientName: clientForm.clientName.trim(),
                clientType: clientForm.clientType,
                clientSecret: clientForm.clientType === 'CONFIDENTIAL' ? clientForm.clientSecret : null,
                grantTypes: clientForm.grantTypes,
                redirectUris: clientForm.redirectUri ? clientForm.redirectUri.split(',').map(s => s.trim()).filter(Boolean) : [],
                postLogoutRedirectUris: ['http://localhost:3000'],
                scopes: clientForm.scopes ? clientForm.scopes.split(',').map(s => s.trim()).filter(Boolean) : ['openid', 'profile'],
                requirePkce: clientForm.requirePkce,
                requireConsent: clientForm.requireConsent
            };

            if (isEditing) {
                await adminService.updateClient(editingClientId, payload);
                showToast(`OAuth2 Client "${clientForm.clientId}" updated successfully.`);
            } else {
                await adminService.createClient(payload);
                showToast(`OAuth2 Client "${clientForm.clientId}" registered successfully.`);
            }

            setShowModal(false);
            fetchClients();
        } catch (err) {
            console.error('Failed to save client', err);
            const msg = err.response?.data?.message || 'Failed to save client. Client ID may already exist.';
            showToast(msg, 'error');
        } finally {
            setSubmitting(false);
        }
    };

    const handleDeleteClient = async () => {
        if (!deleteCandidate) return;
        setSubmitting(true);
        try {
            await adminService.deleteClient(deleteCandidate.id);
            showToast(`OAuth2 Client "${deleteCandidate.clientId}" deleted successfully.`);
            setDeleteCandidate(null);
            fetchClients();
        } catch (err) {
            console.error('Failed to delete client', err);
            showToast('Failed to delete OAuth2 client.', 'error');
        } finally {
            setSubmitting(false);
        }
    };

    const filteredClients = clients.filter(c =>
        c.clientId.toLowerCase().includes(searchQuery.toLowerCase()) ||
        c.clientName.toLowerCase().includes(searchQuery.toLowerCase()) ||
        (c.clientType && c.clientType.toLowerCase().includes(searchQuery.toLowerCase()))
    );

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
                    <h2 className="text-xl sm:text-2xl font-bold text-slate-800">{t('admin.clients.title')}</h2>
                    <p className="text-sm sm:text-base text-slate-500 mt-1">{t('admin.clients.subtitle')}</p>
                </div>
                <div className="flex items-center gap-3">
                    <button
                        onClick={fetchClients}
                        className="p-2.5 text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-xl transition-all cursor-pointer"
                        title="Refresh"
                    >
                        <RefreshCw size={18} className={loading ? 'animate-spin text-[#253C95]' : ''} />
                    </button>
                    <button
                        onClick={openCreateModal}
                        className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-[#253C95] hover:bg-[#1d2f77] text-white font-semibold text-sm shadow-md shadow-[#253C95]/20 transition-all cursor-pointer active:scale-95"
                    >
                        <Plus size={18} />
                        <span>{t('admin.clients.registerClient')}</span>
                    </button>
                </div>
            </div>

            {/* Filter Search */}
            <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-xs flex flex-col sm:flex-row items-center justify-between gap-3">
                <div className="relative w-full sm:w-80">
                    <Search size={18} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
                    <input
                        type="text"
                        placeholder="Search clients by ID, name, or type..."
                        value={searchQuery}
                        onChange={e => setSearchQuery(e.target.value)}
                        className="w-full pl-10 pr-4 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none"
                    />
                </div>
                <div className="flex items-center gap-2 text-xs font-semibold text-slate-500">
                    <span className="flex items-center gap-1.5 px-3 py-1 rounded-full bg-cyan-50 text-cyan-700 border border-cyan-200">
                        <Smartphone size={13} /> Public (PKCE)
                    </span>
                    <span className="flex items-center gap-1.5 px-3 py-1 rounded-full bg-amber-50 text-amber-700 border border-amber-200">
                        <Key size={13} /> Confidential (Secret)
                    </span>
                </div>
            </div>

            {/* Clients Cards Grid */}
            {loading ? (
                <div className="bg-white p-12 rounded-2xl border border-slate-200/90 shadow-2xs flex flex-col items-center justify-center gap-2 text-slate-400">
                    <Loader2 size={32} className="animate-spin text-[#253C95]" />
                    <span className="text-xs font-medium">Loading registered OAuth2 clients...</span>
                </div>
            ) : filteredClients.length === 0 ? (
                <div className="bg-white p-10 rounded-2xl border border-slate-200/90 text-center text-slate-400 text-xs sm:text-sm font-medium">
                    No OAuth2 clients match your search.
                </div>
            ) : (
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-5">
                    {filteredClients.map(client => {
                        const isPublic = client.clientType === 'PUBLIC';
                        return (
                            <div key={client.id} className="bg-white rounded-2xl border border-slate-200 shadow-xs p-6 space-y-4 hover:border-[#253C95]/40 transition-all flex flex-col justify-between">
                                <div className="space-y-4">
                                    {/* Header: Title, Client Type Badge & Status */}
                                    <div className="flex items-start justify-between gap-3 border-b border-slate-100 pb-3.5">
                                        <div className="flex items-start gap-3 min-w-0 flex-1">
                                            <div className={`p-2.5 rounded-xl shrink-0 mt-0.5 ${isPublic ? 'bg-cyan-50 text-cyan-600' : 'bg-amber-50 text-amber-600'}`}>
                                                {isPublic ? <Smartphone size={22} /> : <Server size={22} />}
                                            </div>
                                            <div className="min-w-0 flex-1">
                                                <div className="flex flex-wrap items-center gap-2">
                                                    <h3 className="text-base font-bold text-slate-800 leading-snug">
                                                        {client.clientName}
                                                    </h3>
                                                    <span className={`px-2 py-0.5 rounded text-[11px] font-bold tracking-wide uppercase whitespace-nowrap inline-flex items-center shrink-0 ${
                                                        isPublic
                                                            ? 'bg-cyan-50 text-cyan-700 border border-cyan-200'
                                                            : 'bg-amber-50 text-amber-700 border border-amber-200'
                                                    }`}>
                                                        {isPublic ? 'Public Client' : 'Confidential Client'}
                                                    </span>
                                                </div>
                                                <div className="flex items-center gap-2 mt-1">
                                                    <code className="text-xs font-mono font-semibold text-indigo-700 bg-indigo-50 px-2 py-0.5 rounded truncate max-w-[220px]">
                                                        {client.clientId}
                                                    </code>
                                                    <button
                                                        onClick={() => handleCopy(client.clientId, client.id)}
                                                        className="text-slate-400 hover:text-slate-700 p-0.5 cursor-pointer shrink-0"
                                                        title="Copy Client ID"
                                                    >
                                                        {copiedId === client.id ? <Check size={14} className="text-emerald-600" /> : <Copy size={14} />}
                                                    </button>
                                                </div>
                                            </div>
                                        </div>

                                        <div className="flex items-center gap-1.5 shrink-0">
                                            <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200 whitespace-nowrap">
                                                {client.status || 'ACTIVE'}
                                            </span>
                                            {/* Action Buttons: Edit and Delete */}
                                            <button
                                                onClick={() => openEditModal(client)}
                                                className="p-1.5 text-slate-400 hover:text-[#253C95] hover:bg-slate-100 rounded-lg transition-colors cursor-pointer"
                                                title="Edit Client"
                                            >
                                                <Edit3 size={15} />
                                            </button>
                                            <button
                                                onClick={() => setDeleteCandidate(client)}
                                                className="p-1.5 text-slate-400 hover:text-[#DF1F28] hover:bg-red-50 rounded-lg transition-colors cursor-pointer"
                                                title="Delete Client"
                                            >
                                                <Trash2 size={15} />
                                            </button>
                                        </div>
                                    </div>

                                    {/* Client Secret / Authentication Method Info */}
                                    <div className={`p-3 rounded-xl border text-xs flex items-center justify-between ${
                                        isPublic
                                            ? 'bg-cyan-50/50 border-cyan-100 text-cyan-800'
                                            : 'bg-amber-50/50 border-amber-100 text-amber-800'
                                    }`}>
                                        <div className="flex items-center gap-2">
                                            {isPublic ? <Shield size={15} /> : <Key size={15} />}
                                            <span className="font-semibold">
                                                {isPublic ? 'Public Client (No Secret / Uses PKCE Verification)' : 'Confidential Client (Client Secret Protected)'}
                                            </span>
                                        </div>
                                        <span className="font-mono text-[11px] px-2 py-0.5 rounded bg-white border border-slate-200">
                                            Auth: {isPublic ? 'none' : 'client_secret_basic'}
                                        </span>
                                    </div>

                                    {/* Grant Types & Scopes */}
                                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-xs sm:text-sm">
                                        <div className="p-3 rounded-xl bg-slate-50 border border-slate-100">
                                            <p className="text-slate-400 font-semibold uppercase text-xs">Grant Types</p>
                                            <div className="flex flex-wrap gap-1 mt-1.5">
                                                {(client.grantTypes || []).map(gt => (
                                                    <span key={gt} className="px-2 py-0.5 rounded bg-white text-slate-700 border border-slate-200 font-mono text-xs font-medium">
                                                        {gt}
                                                    </span>
                                                ))}
                                            </div>
                                        </div>

                                        <div className="p-3 rounded-xl bg-slate-50 border border-slate-100">
                                            <p className="text-slate-400 font-semibold uppercase text-xs">Scopes</p>
                                            <div className="flex flex-wrap gap-1 mt-1.5">
                                                {(client.scopes || []).map(sc => (
                                                    <span key={sc} className="px-2 py-0.5 rounded bg-white text-emerald-700 border border-emerald-200 font-mono text-xs font-medium">
                                                        {sc}
                                                    </span>
                                                ))}
                                            </div>
                                        </div>
                                    </div>

                                    {/* Redirect URIs */}
                                    {(client.redirectUris && client.redirectUris.length > 0) && (
                                        <div className="p-3 rounded-xl bg-slate-50 border border-slate-100 space-y-1">
                                            <p className="text-slate-400 font-semibold uppercase text-xs">Authorized Redirect URIs</p>
                                            {client.redirectUris.map((uri, i) => (
                                                <div key={i} className="flex items-center gap-1.5 text-xs text-slate-600 font-mono truncate">
                                                    <ExternalLink size={12} className="text-slate-400 shrink-0" />
                                                    <span className="truncate">{uri}</span>
                                                </div>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                {/* Security Policies & Token TTL */}
                                <div className="flex flex-wrap items-center justify-between gap-2 pt-3 border-t border-slate-100 text-xs font-medium text-slate-500">
                                    <div className="flex items-center gap-3">
                                        <span className={`flex items-center gap-1 ${client.requirePkce ? 'text-emerald-700 font-semibold' : 'text-slate-400'}`}>
                                            <Lock size={13} /> PKCE: {client.requirePkce ? 'Enforced' : 'Off'}
                                        </span>
                                        <span className={`flex items-center gap-1 ${client.requireConsent ? 'text-indigo-700 font-semibold' : 'text-slate-400'}`}>
                                            <Shield size={13} /> Consent: {client.requireConsent ? 'Required' : 'Skip'}
                                        </span>
                                    </div>
                                    <div className="flex items-center gap-1 text-slate-400">
                                        <Clock size={13} />
                                        <span>Token TTL: {client.accessTokenTtl || '5 hours'}</span>
                                    </div>
                                </div>
                            </div>
                        );
                    })}
                </div>
            )}

            {/* --- MODAL: REGISTER / EDIT CLIENT --- */}
            {showModal && (
                <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4 overflow-y-auto">
                    <div className="bg-white rounded-2xl max-w-xl w-full p-6 shadow-2xl border border-slate-200 space-y-5 animate-in fade-in zoom-in duration-150 my-8">
                        <div className="flex items-center justify-between border-b border-slate-100 pb-3">
                            <h3 className="text-lg font-bold text-slate-800">
                                {isEditing ? `Edit Client: ${clientForm.clientId}` : t('admin.clients.registerClient')}
                            </h3>
                            <button onClick={() => setShowModal(false)} className="text-slate-400 hover:text-slate-600 text-xl font-bold cursor-pointer">
                                &times;
                            </button>
                        </div>

                        <form onSubmit={handleSubmit} className="space-y-4">
                            {/* CLIENT TYPE SELECTOR: PUBLIC vs CONFIDENTIAL */}
                            <div>
                                <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-2">
                                    Client Type *
                                </label>
                                <div className="grid grid-cols-2 gap-3">
                                    <button
                                        type="button"
                                        disabled={isEditing}
                                        onClick={() => handleClientTypeChange('PUBLIC')}
                                        className={`p-3.5 rounded-xl border text-left flex items-start gap-3 transition-all cursor-pointer ${
                                            clientForm.clientType === 'PUBLIC'
                                                ? 'border-[#253C95] bg-[#253C95]/5 ring-1 ring-[#253C95]'
                                                : 'border-slate-200 hover:border-slate-300'
                                        } ${isEditing ? 'opacity-70 cursor-not-allowed' : ''}`}
                                    >
                                        <div className={`p-2 rounded-lg ${clientForm.clientType === 'PUBLIC' ? 'bg-[#253C95] text-white' : 'bg-slate-100 text-slate-500'}`}>
                                            <Smartphone size={20} />
                                        </div>
                                        <div>
                                            <div className="font-bold text-sm text-slate-800">Public Client</div>
                                            <div className="text-xs text-slate-500 mt-0.5">SPA (React/Vue), Mobile App. No client secret; enforces PKCE.</div>
                                        </div>
                                    </button>

                                    <button
                                        type="button"
                                        disabled={isEditing}
                                        onClick={() => handleClientTypeChange('CONFIDENTIAL')}
                                        className={`p-3.5 rounded-xl border text-left flex items-start gap-3 transition-all cursor-pointer ${
                                            clientForm.clientType === 'CONFIDENTIAL'
                                                ? 'border-[#253C95] bg-[#253C95]/5 ring-1 ring-[#253C95]'
                                                : 'border-slate-200 hover:border-slate-300'
                                        } ${isEditing ? 'opacity-70 cursor-not-allowed' : ''}`}
                                    >
                                        <div className={`p-2 rounded-lg ${clientForm.clientType === 'CONFIDENTIAL' ? 'bg-[#253C95] text-white' : 'bg-slate-100 text-slate-500'}`}>
                                            <Server size={20} />
                                        </div>
                                        <div>
                                            <div className="font-bold text-sm text-slate-800">Confidential Client</div>
                                            <div className="text-xs text-slate-500 mt-0.5">Backend services & servers. Requires a secured Client Secret.</div>
                                        </div>
                                    </button>
                                </div>
                            </div>

                            {/* CLIENT ID & NAME */}
                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">
                                        {t('admin.clients.clientId')} *
                                    </label>
                                    <input
                                        required
                                        disabled={isEditing}
                                        type="text"
                                        value={clientForm.clientId}
                                        onChange={e => setClientForm({ ...clientForm, clientId: e.target.value })}
                                        className={`w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none font-mono ${
                                            isEditing ? 'bg-slate-100 text-slate-500 cursor-not-allowed' : ''
                                        }`}
                                        placeholder="e.g. front-bff-client"
                                    />
                                </div>
                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">
                                        {t('admin.clients.clientName')}
                                    </label>
                                    <input
                                        type="text"
                                        value={clientForm.clientName}
                                        onChange={e => setClientForm({ ...clientForm, clientName: e.target.value })}
                                        className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none"
                                        placeholder="e.g. Customer Portal SPA"
                                    />
                                </div>
                            </div>

                            {/* CLIENT SECRET (Only for CONFIDENTIAL Clients) */}
                            {clientForm.clientType === 'CONFIDENTIAL' && (
                                <div className="p-3.5 rounded-xl bg-amber-50/60 border border-amber-200/80 space-y-2">
                                    <div className="flex items-center justify-between">
                                        <label className="text-xs font-bold uppercase tracking-wider text-amber-900 flex items-center gap-1.5">
                                            <Key size={14} className="text-amber-700" /> Client Secret {isEditing ? '(Leave blank to keep current)' : '*'}
                                        </label>
                                        <button
                                            type="button"
                                            onClick={handleGenerateSecret}
                                            className="text-xs font-semibold text-[#253C95] hover:underline flex items-center gap-1 cursor-pointer"
                                        >
                                            <Sparkles size={12} /> Generate Strong Secret
                                        </button>
                                    </div>
                                    <div className="relative">
                                        <input
                                            required={!isEditing && clientForm.clientType === 'CONFIDENTIAL'}
                                            type={showSecret ? 'text' : 'password'}
                                            value={clientForm.clientSecret}
                                            onChange={e => setClientForm({ ...clientForm, clientSecret: e.target.value })}
                                            className="w-full pl-3.5 pr-10 py-2 rounded-xl border border-amber-300 bg-white text-sm focus:border-[#253C95] outline-none font-mono text-slate-800"
                                            placeholder={isEditing ? 'Enter new secret to rotate, or leave empty' : 'e.g. secret_xyz987...'}
                                        />
                                        <button
                                            type="button"
                                            onClick={() => setShowSecret(!showSecret)}
                                            className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-700 cursor-pointer"
                                        >
                                            {showSecret ? <EyeOff size={16} /> : <Eye size={16} />}
                                        </button>
                                    </div>
                                    <p className="text-[11px] text-amber-700">
                                        Store this secret safely in your backend configuration. It will be hashed using BCrypt.
                                    </p>
                                </div>
                            )}

                            {/* GRANT TYPES CHECKBOXES */}
                            <div>
                                <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1.5">
                                    Allowed Grant Types *
                                </label>
                                <div className="flex flex-wrap gap-2">
                                    {['authorization_code', 'refresh_token', 'client_credentials'].map(gt => {
                                        const isSelected = clientForm.grantTypes.includes(gt);
                                        return (
                                            <button
                                                key={gt}
                                                type="button"
                                                onClick={() => handleGrantTypeToggle(gt)}
                                                className={`px-3 py-1.5 rounded-lg border text-xs font-mono font-medium transition-colors cursor-pointer flex items-center gap-1.5 ${
                                                    isSelected
                                                        ? 'bg-[#253C95] text-white border-[#253C95]'
                                                        : 'bg-white text-slate-600 border-slate-200 hover:border-slate-300'
                                                }`}
                                            >
                                                {isSelected && <Check size={13} />}
                                                {gt}
                                            </button>
                                        );
                                    })}
                                </div>
                            </div>

                            {/* REDIRECT URIS */}
                            <div>
                                <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">
                                    Redirect URIs (comma-separated)
                                </label>
                                <input
                                    type="text"
                                    value={clientForm.redirectUri}
                                    onChange={e => setClientForm({ ...clientForm, redirectUri: e.target.value })}
                                    className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none font-mono"
                                    placeholder="http://localhost:3000/callback, http://localhost:8080/login/oauth2/code/front-bff-client"
                                />
                            </div>

                            {/* SCOPES */}
                            <div>
                                <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">
                                    Scopes (comma-separated)
                                </label>
                                <input
                                    type="text"
                                    value={clientForm.scopes}
                                    onChange={e => setClientForm({ ...clientForm, scopes: e.target.value })}
                                    className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none font-mono"
                                    placeholder="openid, profile, email"
                                />
                            </div>

                            {/* POLICIES (PKCE & CONSENT) */}
                            <div className="flex items-center gap-6 pt-2">
                                <label className="flex items-center gap-2 cursor-pointer text-sm font-semibold text-slate-700">
                                    <input
                                        type="checkbox"
                                        checked={clientForm.requirePkce}
                                        onChange={e => setClientForm({ ...clientForm, requirePkce: e.target.checked })}
                                        className="w-4 h-4 rounded text-[#253C95] focus:ring-[#253C95]"
                                    />
                                    <span>Enforce PKCE</span>
                                </label>

                                <label className="flex items-center gap-2 cursor-pointer text-sm font-semibold text-slate-700">
                                    <input
                                        type="checkbox"
                                        checked={clientForm.requireConsent}
                                        onChange={e => setClientForm({ ...clientForm, requireConsent: e.target.checked })}
                                        className="w-4 h-4 rounded text-[#253C95] focus:ring-[#253C95]"
                                    />
                                    <span>Consent Screen</span>
                                </label>
                            </div>

                            {/* MODAL FOOTER */}
                            <div className="flex justify-end gap-3 pt-3 border-t border-slate-100">
                                <button
                                    type="button"
                                    onClick={() => setShowModal(false)}
                                    className="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 text-sm font-semibold hover:bg-slate-50 cursor-pointer"
                                >
                                    {t('common.cancel')}
                                </button>
                                <button
                                    type="submit"
                                    disabled={submitting}
                                    className="flex items-center gap-2 px-5 py-2 rounded-xl bg-[#253C95] text-white text-sm font-semibold hover:bg-[#1d2f77] shadow-md shadow-[#253C95]/20 cursor-pointer disabled:opacity-50"
                                >
                                    {submitting && <Loader2 size={16} className="animate-spin" />}
                                    <span>{t('common.save')}</span>
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* --- MODAL: DELETE CONFIRMATION --- */}
            {deleteCandidate && (
                <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
                    <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-slate-200 space-y-4 animate-in fade-in zoom-in duration-150">
                        <div className="flex items-center gap-3 text-[#DF1F28]">
                            <div className="p-3 rounded-full bg-red-100">
                                <AlertTriangle size={24} />
                            </div>
                            <h3 className="text-lg font-bold text-slate-800">Delete OAuth2 Client</h3>
                        </div>
                        <p className="text-sm text-slate-600">
                            Are you sure you want to delete client <span className="font-mono font-bold text-slate-800">"{deleteCandidate.clientId}"</span>?
                            Any active user authorizations, refresh tokens, and consent records for this client will be revoked immediately.
                        </p>
                        <div className="flex justify-end gap-3 pt-2">
                            <button
                                type="button"
                                onClick={() => setDeleteCandidate(null)}
                                className="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 text-sm font-semibold hover:bg-slate-50 cursor-pointer"
                            >
                                {t('common.cancel')}
                            </button>
                            <button
                                type="button"
                                disabled={submitting}
                                onClick={handleDeleteClient}
                                className="flex items-center gap-2 px-5 py-2 rounded-xl bg-[#DF1F28] text-white text-sm font-semibold hover:bg-red-700 shadow-md shadow-[#DF1F28]/20 cursor-pointer disabled:opacity-50"
                            >
                                {submitting && <Loader2 size={16} className="animate-spin" />}
                                <span>Delete Client</span>
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
