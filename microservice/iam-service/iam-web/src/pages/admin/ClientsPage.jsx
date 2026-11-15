import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { adminService } from '../../services/adminService';
import {
    ShieldCheck, Plus, Search, Key, Globe, Lock,
    CheckCircle2, AlertTriangle, RefreshCw, Copy, Check,
    ExternalLink, Layers, Clock, Shield, Loader2
} from 'lucide-react';

export default function ClientsPage() {
    const { t } = useTranslation();
    const [searchQuery, setSearchQuery] = useState('');
    const [showRegisterModal, setShowRegisterModal] = useState(false);
    const [copiedId, setCopiedId] = useState(null);
    const [loading, setLoading] = useState(true);

    const [clients, setClients] = useState([]);

    const fetchClients = async () => {
        setLoading(true);
        try {
            const data = await adminService.getClients();
            if (Array.isArray(data) && data.length > 0) {
                setClients(data);
            }
        } catch (err) {
            console.error('Failed to load registered clients', err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchClients();
    }, []);

    // Toast
    const [toast, setToast] = useState({ show: false, message: '', type: 'success' });
    const showToast = (message, type = 'success') => {
        setToast({ show: true, message, type });
        setTimeout(() => setToast({ show: false, message: '', type: 'success' }), 3500);
    };

    // Client register form
    const [clientForm, setClientForm] = useState({
        clientId: '',
        clientName: '',
        clientSecret: '',
        authMethod: 'none (Public PKCE)',
        grantTypes: ['authorization_code', 'refresh_token'],
        redirectUri: '',
        scopes: 'openid, profile',
        requirePkce: true,
        requireConsent: true
    });

    const handleCopy = (text, id) => {
        navigator.clipboard.writeText(text);
        setCopiedId(id);
        showToast(`Copied ${text} to clipboard!`);
        setTimeout(() => setCopiedId(null), 2000);
    };

    const handleRegisterClient = (e) => {
        e.preventDefault();
        const newClient = {
            id: String(Date.now()),
            clientId: clientForm.clientId,
            clientName: clientForm.clientName || clientForm.clientId,
            authMethod: clientForm.authMethod,
            grantTypes: clientForm.grantTypes,
            redirectUris: clientForm.redirectUri ? clientForm.redirectUri.split(',').map(s => s.trim()) : [],
            postLogoutRedirectUris: ['http://localhost:3000'],
            scopes: clientForm.scopes.split(',').map(s => s.trim()),
            requirePkce: clientForm.requirePkce,
            requireConsent: clientForm.requireConsent,
            accessTokenTtl: '5 hours',
            refreshTokenTtl: '7 days',
            status: 'ACTIVE'
        };

        setClients([newClient, ...clients]);
        showToast('OAuth2 Client registered successfully.');
        setShowRegisterModal(false);
        setClientForm({
            clientId: '',
            clientName: '',
            clientSecret: '',
            authMethod: 'none (Public PKCE)',
            grantTypes: ['authorization_code', 'refresh_token'],
            redirectUri: '',
            scopes: 'openid, profile',
            requirePkce: true,
            requireConsent: true
        });
    };

    const filteredClients = clients.filter(c =>
        c.clientId.toLowerCase().includes(searchQuery.toLowerCase()) ||
        c.clientName.toLowerCase().includes(searchQuery.toLowerCase())
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
                        className="p-2 text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-xl transition-all cursor-pointer"
                        title="Refresh"
                    >
                        <RefreshCw size={18} className={loading ? 'animate-spin text-[#253C95]' : ''} />
                    </button>
                    <button
                        onClick={() => setShowRegisterModal(true)}
                        className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-[#253C95] hover:bg-[#1d2f77] text-white font-semibold text-sm shadow-md shadow-[#253C95]/20 transition-all cursor-pointer active:scale-95"
                    >
                        <Plus size={18} />
                        <span>{t('admin.clients.registerClient')}</span>
                    </button>
                </div>
            </div>

            {/* Filter Search */}
            <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-xs">
                <div className="relative w-full sm:w-80">
                    <Search size={18} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
                    <input
                        type="text"
                        placeholder="Search clients by ID or name..."
                        value={searchQuery}
                        onChange={e => setSearchQuery(e.target.value)}
                        className="w-full pl-10 pr-4 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none"
                    />
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
                {filteredClients.map(client => (
                    <div key={client.id} className="bg-white rounded-2xl border border-slate-200 shadow-xs p-6 space-y-4 hover:border-[#253C95]/40 transition-all">
                        {/* Title & Status */}
                        <div className="flex items-start justify-between gap-3 border-b border-slate-100 pb-4">
                            <div className="flex items-center gap-3">
                                <div className="p-3 rounded-xl bg-[#253C95]/10 text-[#253C95]">
                                    <ShieldCheck size={24} />
                                </div>
                                <div>
                                    <h3 className="text-base sm:text-lg font-bold text-slate-800">{client.clientName}</h3>
                                    <div className="flex items-center gap-2 mt-0.5">
                                        <code className="text-xs sm:text-sm font-mono font-semibold text-indigo-700 bg-indigo-50 px-2 py-0.5 rounded">
                                            {client.clientId}
                                        </code>
                                        <button
                                            onClick={() => handleCopy(client.clientId, client.id)}
                                            className="text-slate-400 hover:text-slate-700 p-0.5"
                                            title="Copy Client ID"
                                        >
                                            {copiedId === client.id ? <Check size={14} className="text-emerald-600" /> : <Copy size={14} />}
                                        </button>
                                    </div>
                                </div>
                            </div>
                            <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
                                {client.status}
                            </span>
                        </div>

                        {/* Grant Types & Scopes */}
                        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-xs sm:text-sm">
                            <div className="p-3 rounded-xl bg-slate-50 border border-slate-100">
                                <p className="text-slate-400 font-semibold uppercase text-xs">Grant Types</p>
                                <div className="flex flex-wrap gap-1 mt-1.5">
                                    {client.grantTypes.map(gt => (
                                        <span key={gt} className="px-2 py-0.5 rounded bg-white text-slate-700 border border-slate-200 font-mono text-xs font-medium">
                                            {gt}
                                        </span>
                                    ))}
                                </div>
                            </div>

                            <div className="p-3 rounded-xl bg-slate-50 border border-slate-100">
                                <p className="text-slate-400 font-semibold uppercase text-xs">Scopes</p>
                                <div className="flex flex-wrap gap-1 mt-1.5">
                                    {client.scopes.map(sc => (
                                        <span key={sc} className="px-2 py-0.5 rounded bg-white text-emerald-700 border border-emerald-200 font-mono text-xs font-medium">
                                            {sc}
                                        </span>
                                    ))}
                                </div>
                            </div>
                        </div>

                        {/* Redirect URIs */}
                        {client.redirectUris.length > 0 && (
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

                        {/* Security Policies */}
                        <div className="flex flex-wrap items-center justify-between gap-2 pt-2 border-t border-slate-100 text-xs font-medium text-slate-500">
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
                                <span>Token TTL: {client.accessTokenTtl}</span>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
            )}

            {/* --- MODAL: REGISTER CLIENT --- */}
            {showRegisterModal && (
                <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
                    <div className="bg-white rounded-2xl max-w-lg w-full p-6 shadow-2xl border border-slate-200 space-y-5 animate-in fade-in zoom-in duration-150">
                        <div className="flex items-center justify-between border-b border-slate-100 pb-3">
                            <h3 className="text-lg font-bold text-slate-800">{t('admin.clients.registerClient')}</h3>
                            <button onClick={() => setShowRegisterModal(false)} className="text-slate-400 hover:text-slate-600">
                                &times;
                            </button>
                        </div>
                        <form onSubmit={handleRegisterClient} className="space-y-4">
                            <div className="grid grid-cols-2 gap-3">
                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">{t('admin.clients.clientId')} *</label>
                                    <input
                                        required
                                        type="text"
                                        value={clientForm.clientId}
                                        onChange={e => setClientForm({ ...clientForm, clientId: e.target.value })}
                                        className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none font-mono"
                                        placeholder="e.g. mobile-app-client"
                                    />
                                </div>
                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">{t('admin.clients.clientName')}</label>
                                    <input
                                        type="text"
                                        value={clientForm.clientName}
                                        onChange={e => setClientForm({ ...clientForm, clientName: e.target.value })}
                                        className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none"
                                        placeholder="e.g. iOS Mobile App"
                                    />
                                </div>
                            </div>

                            <div>
                                <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">Redirect URIs (comma-separated)</label>
                                <input
                                    type="text"
                                    value={clientForm.redirectUri}
                                    onChange={e => setClientForm({ ...clientForm, redirectUri: e.target.value })}
                                    className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none font-mono"
                                    placeholder="http://localhost:3000/callback, https://myapp.com/login"
                                />
                            </div>

                            <div>
                                <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">Scopes (comma-separated)</label>
                                <input
                                    type="text"
                                    value={clientForm.scopes}
                                    onChange={e => setClientForm({ ...clientForm, scopes: e.target.value })}
                                    className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] outline-none font-mono"
                                    placeholder="openid, profile, email"
                                />
                            </div>

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

                            <div className="flex justify-end gap-3 pt-3 border-t border-slate-100">
                                <button
                                    type="button"
                                    onClick={() => setShowRegisterModal(false)}
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

