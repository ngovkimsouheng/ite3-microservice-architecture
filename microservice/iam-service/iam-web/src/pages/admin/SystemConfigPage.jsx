import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import {
    Shield, Clock, Lock, CheckCircle2,
    Save, RefreshCw, AlertTriangle, Key, ShieldCheck,
    RotateCcw, Laptop, UserCheck
} from 'lucide-react';
import { adminService } from '../../services/adminService';

export default function SystemConfigPage() {
    const { t } = useTranslation();

    const [config, setConfig] = useState({
        // 1. Authentication Policies
        maxFailedAttempts: 5,
        lockoutDurationMinutes: 30,
        enableAutoUnlock: true,
        mfaPolicy: 'OPTIONAL',

        // 2. Password Policies
        minPasswordLength: 8,
        requireUppercase: true,
        requireLowercase: true,
        requireDigits: true,
        requireSpecialChar: true,
        passwordExpireDays: 90,

        // 3. Session Expiration Policies
        sessionIdleTimeoutMinutes: 30,
        sessionMaxInactiveDays: 7,
        maxConcurrentSessions: 3,
        forceSingleSession: false,

        // 4. Token Durations
        accessTokenTtlHours: 5,
        refreshTokenTtlDays: 7,
        authCodeTtlMinutes: 3,
        reuseRefreshTokens: false,
        tokenFormat: 'JWT',

        updatedAt: null,
        updatedBy: null
    });

    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [resetting, setResetting] = useState(false);
    const [toast, setToast] = useState({ show: false, message: '', type: 'success' });

    const showToast = (message, type = 'success') => {
        setToast({ show: true, message, type });
        setTimeout(() => setToast({ show: false, message: '', type: 'success' }), 4000);
    };

    const fetchConfig = async () => {
        setLoading(true);
        try {
            const data = await adminService.getSystemConfig();
            if (data) {
                setConfig({
                    maxFailedAttempts: data.maxFailedAttempts ?? 5,
                    lockoutDurationMinutes: data.lockoutDurationMinutes ?? 30,
                    enableAutoUnlock: data.enableAutoUnlock ?? true,
                    mfaPolicy: data.mfaPolicy || 'OPTIONAL',

                    minPasswordLength: data.minPasswordLength ?? 8,
                    requireUppercase: data.requireUppercase ?? true,
                    requireLowercase: data.requireLowercase ?? true,
                    requireDigits: data.requireDigits ?? true,
                    requireSpecialChar: data.requireSpecialChar ?? true,
                    passwordExpireDays: data.passwordExpireDays ?? 90,

                    sessionIdleTimeoutMinutes: data.sessionIdleTimeoutMinutes ?? 30,
                    sessionMaxInactiveDays: data.sessionMaxInactiveDays ?? 7,
                    maxConcurrentSessions: data.maxConcurrentSessions ?? 3,
                    forceSingleSession: data.forceSingleSession ?? false,

                    accessTokenTtlHours: data.accessTokenTtlHours ?? 5,
                    refreshTokenTtlDays: data.refreshTokenTtlDays ?? 7,
                    authCodeTtlMinutes: data.authCodeTtlMinutes ?? 3,
                    reuseRefreshTokens: data.reuseRefreshTokens ?? false,
                    tokenFormat: data.tokenFormat || 'JWT',

                    updatedAt: data.updatedAt,
                    updatedBy: data.updatedBy
                });
            }
        } catch (err) {
            console.error('Failed to load system config:', err);
            showToast('Failed to load current system configuration from server.', 'error');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchConfig();
    }, []);

    const handleSaveConfig = async (e) => {
        if (e) e.preventDefault();
        setSaving(true);
        try {
            const payload = {
                maxFailedAttempts: Number(config.maxFailedAttempts),
                lockoutDurationMinutes: Number(config.lockoutDurationMinutes),
                enableAutoUnlock: Boolean(config.enableAutoUnlock),
                mfaPolicy: config.mfaPolicy,

                minPasswordLength: Number(config.minPasswordLength),
                requireUppercase: Boolean(config.requireUppercase),
                requireLowercase: Boolean(config.requireLowercase),
                requireDigits: Boolean(config.requireDigits),
                requireSpecialChar: Boolean(config.requireSpecialChar),
                passwordExpireDays: Number(config.passwordExpireDays),

                sessionIdleTimeoutMinutes: Number(config.sessionIdleTimeoutMinutes),
                sessionMaxInactiveDays: Number(config.sessionMaxInactiveDays),
                maxConcurrentSessions: Number(config.maxConcurrentSessions),
                forceSingleSession: Boolean(config.forceSingleSession),

                accessTokenTtlHours: Number(config.accessTokenTtlHours),
                refreshTokenTtlDays: Number(config.refreshTokenTtlDays),
                authCodeTtlMinutes: Number(config.authCodeTtlMinutes),
                reuseRefreshTokens: Boolean(config.reuseRefreshTokens),
                tokenFormat: config.tokenFormat
            };

            const updated = await adminService.updateSystemConfig(payload);
            if (updated) {
                setConfig(prev => ({
                    ...prev,
                    ...updated,
                    updatedAt: updated.updatedAt,
                    updatedBy: updated.updatedBy
                }));
            }
            showToast('System configuration policies updated and enforced successfully.');
        } catch (err) {
            console.error('Failed to update system config:', err);
            const msg = err.response?.data?.message || err.response?.data?.detail || 'Failed to update system configuration.';
            showToast(msg, 'error');
        } finally {
            setSaving(false);
        }
    };

    const handleResetDefaults = async () => {
        if (!window.confirm('Are you sure you want to restore all system security and token policies to factory defaults?')) {
            return;
        }
        setResetting(true);
        try {
            const defaults = await adminService.resetSystemConfig();
            if (defaults) {
                setConfig({
                    maxFailedAttempts: defaults.maxFailedAttempts ?? 5,
                    lockoutDurationMinutes: defaults.lockoutDurationMinutes ?? 30,
                    enableAutoUnlock: defaults.enableAutoUnlock ?? true,
                    mfaPolicy: defaults.mfaPolicy || 'OPTIONAL',

                    minPasswordLength: defaults.minPasswordLength ?? 8,
                    requireUppercase: defaults.requireUppercase ?? true,
                    requireLowercase: defaults.requireLowercase ?? true,
                    requireDigits: defaults.requireDigits ?? true,
                    requireSpecialChar: defaults.requireSpecialChar ?? true,
                    passwordExpireDays: defaults.passwordExpireDays ?? 90,

                    sessionIdleTimeoutMinutes: defaults.sessionIdleTimeoutMinutes ?? 30,
                    sessionMaxInactiveDays: defaults.sessionMaxInactiveDays ?? 7,
                    maxConcurrentSessions: defaults.maxConcurrentSessions ?? 3,
                    forceSingleSession: defaults.forceSingleSession ?? false,

                    accessTokenTtlHours: defaults.accessTokenTtlHours ?? 5,
                    refreshTokenTtlDays: defaults.refreshTokenTtlDays ?? 7,
                    authCodeTtlMinutes: defaults.authCodeTtlMinutes ?? 3,
                    reuseRefreshTokens: defaults.reuseRefreshTokens ?? false,
                    tokenFormat: defaults.tokenFormat || 'JWT',

                    updatedAt: defaults.updatedAt,
                    updatedBy: defaults.updatedBy
                });
            }
            showToast('Configuration restored to factory defaults.');
        } catch (err) {
            console.error('Failed to reset config:', err);
            showToast('Failed to reset configuration.', 'error');
        } finally {
            setResetting(false);
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
                    <h2 className="text-xl sm:text-2xl font-bold text-slate-800">{t('admin.system.title')}</h2>
                    <p className="text-sm sm:text-base text-slate-500 mt-1">{t('admin.system.subtitle')}</p>
                    {config.updatedAt && (
                        <div className="flex items-center gap-2 mt-2 text-xs text-slate-400">
                            <Clock size={13} />
                            <span>
                                Last modified: {new Date(config.updatedAt).toLocaleString()} by <strong className="text-slate-600">{config.updatedBy || 'SYSTEM'}</strong>
                            </span>
                        </div>
                    )}
                </div>
                <div className="flex flex-wrap items-center gap-3">
                    <button
                        type="button"
                        onClick={fetchConfig}
                        disabled={loading || saving}
                        className="flex items-center gap-2 px-4 py-2.5 rounded-xl border border-slate-200 hover:bg-slate-50 text-slate-700 font-semibold text-sm transition-all cursor-pointer active:scale-95 disabled:opacity-60"
                        title="Reload from server"
                    >
                        <RefreshCw size={16} className={loading ? 'animate-spin' : ''} />
                        <span className="hidden sm:inline">Refresh</span>
                    </button>

                    <button
                        type="button"
                        onClick={handleResetDefaults}
                        disabled={loading || saving || resetting}
                        className="flex items-center gap-2 px-4 py-2.5 rounded-xl border border-slate-200 hover:bg-slate-50 text-slate-700 font-semibold text-sm transition-all cursor-pointer active:scale-95 disabled:opacity-60"
                        title={t('admin.system.resetDefaults')}
                    >
                        <RotateCcw size={16} className={resetting ? 'animate-spin' : ''} />
                        <span>Defaults</span>
                    </button>

                    <button
                        type="button"
                        onClick={handleSaveConfig}
                        disabled={loading || saving || resetting}
                        className="flex items-center gap-2 px-5 py-2.5 rounded-xl bg-[#253C95] hover:bg-[#1d2f77] text-white font-semibold text-sm shadow-md shadow-[#253C95]/20 transition-all cursor-pointer active:scale-95 disabled:opacity-60"
                    >
                        {saving ? <RefreshCw size={17} className="animate-spin" /> : <Save size={17} />}
                        <span>{saving ? 'Saving...' : t('admin.system.saveConfig')}</span>
                    </button>
                </div>
            </div>

            {loading ? (
                <div className="bg-white p-12 rounded-2xl border border-slate-200 shadow-xs flex flex-col items-center justify-center gap-3 text-slate-400">
                    <RefreshCw size={28} className="animate-spin text-[#253C95]" />
                    <span className="text-sm font-medium">Loading system configuration policies...</span>
                </div>
            ) : (
                <form onSubmit={handleSaveConfig} className="grid grid-cols-1 lg:grid-cols-2 gap-6">

                    {/* --- Section 1: Authentication & MFA Policies --- */}
                    <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-5">
                        <div className="flex items-center gap-3 border-b border-slate-100 pb-3">
                            <div className="p-2.5 rounded-xl bg-red-50 text-[#DF1F28]">
                                <ShieldCheck size={22} />
                            </div>
                            <div>
                                <h3 className="text-base sm:text-lg font-bold text-slate-800">{t('admin.system.authSection')}</h3>
                                <p className="text-xs sm:text-sm text-slate-400">Brute-force lockout and Multi-Factor Authentication controls</p>
                            </div>
                        </div>

                        <div className="space-y-4">
                            <div>
                                <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">
                                    {t('admin.system.maxFailedAttempts')}
                                </label>
                                <input
                                    type="number"
                                    min="1"
                                    max="50"
                                    value={config.maxFailedAttempts}
                                    onChange={e => setConfig({ ...config, maxFailedAttempts: Number(e.target.value) })}
                                    className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 text-sm font-semibold focus:border-[#253C95] outline-none"
                                />
                                <p className="text-xs text-slate-400 mt-1">Freezes account after this many consecutive incorrect credentials.</p>
                            </div>

                            <div>
                                <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">
                                    {t('admin.system.lockoutDuration')}
                                </label>
                                <input
                                    type="number"
                                    min="1"
                                    max="10080"
                                    value={config.lockoutDurationMinutes}
                                    onChange={e => setConfig({ ...config, lockoutDurationMinutes: Number(e.target.value) })}
                                    className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 text-sm font-semibold focus:border-[#253C95] outline-none"
                                />
                                <p className="text-xs text-slate-400 mt-1">Duration the account remains locked before credentials can be reattempted.</p>
                            </div>

                            <div>
                                <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">
                                    {t('admin.system.mfaPolicy')}
                                </label>
                                <select
                                    value={config.mfaPolicy}
                                    onChange={e => setConfig({ ...config, mfaPolicy: e.target.value })}
                                    className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 text-sm font-semibold focus:border-[#253C95] outline-none bg-white"
                                >
                                    <option value="OPTIONAL">Optional (User may configure Authenticator / OTP)</option>
                                    <option value="REQUIRED_ADMIN">Required for Administrators only</option>
                                    <option value="REQUIRED_ALL">Enforced for All System Accounts</option>
                                    <option value="DISABLED">Disabled Globally</option>
                                </select>
                                <p className="text-xs text-slate-400 mt-1">Enforcement rule for Two-Factor / Multi-Factor Authentication.</p>
                            </div>

                            <div className="pt-2">
                                <label className="flex items-center gap-3 cursor-pointer">
                                    <input
                                        type="checkbox"
                                        checked={config.enableAutoUnlock}
                                        onChange={e => setConfig({ ...config, enableAutoUnlock: e.target.checked })}
                                        className="w-4 h-4 rounded text-[#253C95] focus:ring-[#253C95]"
                                    />
                                    <span className="text-sm font-semibold text-slate-700">{t('admin.system.enableAutoUnlock')}</span>
                                </label>
                            </div>
                        </div>
                    </div>

                    {/* --- Section 2: Enterprise Password Policy --- */}
                    <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-5">
                        <div className="flex items-center gap-3 border-b border-slate-100 pb-3">
                            <div className="p-2.5 rounded-xl bg-amber-500/10 text-[#F9A825]">
                                <Key size={22} />
                            </div>
                            <div>
                                <h3 className="text-base sm:text-lg font-bold text-slate-800">{t('admin.system.passwordSection')}</h3>
                                <p className="text-xs sm:text-sm text-slate-400">Credential complexity rules and rotation lifecycles</p>
                            </div>
                        </div>

                        <div className="space-y-4">
                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">
                                        {t('admin.system.minPasswordLength')}
                                    </label>
                                    <input
                                        type="number"
                                        min="6"
                                        max="64"
                                        value={config.minPasswordLength}
                                        onChange={e => setConfig({ ...config, minPasswordLength: Number(e.target.value) })}
                                        className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 text-sm font-semibold focus:border-[#253C95] outline-none"
                                    />
                                </div>

                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">
                                        {t('admin.system.passwordExpireDays')}
                                    </label>
                                    <input
                                        type="number"
                                        min="0"
                                        max="730"
                                        value={config.passwordExpireDays}
                                        onChange={e => setConfig({ ...config, passwordExpireDays: Number(e.target.value) })}
                                        className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 text-sm font-semibold focus:border-[#253C95] outline-none"
                                    />
                                </div>
                            </div>

                            <div className="space-y-2.5 pt-1">
                                <label className="flex items-center gap-3 cursor-pointer">
                                    <input
                                        type="checkbox"
                                        checked={config.requireUppercase}
                                        onChange={e => setConfig({ ...config, requireUppercase: e.target.checked })}
                                        className="w-4 h-4 rounded text-[#253C95] focus:ring-[#253C95]"
                                    />
                                    <span className="text-sm font-semibold text-slate-700">{t('admin.system.requireUppercase')}</span>
                                </label>

                                <label className="flex items-center gap-3 cursor-pointer">
                                    <input
                                        type="checkbox"
                                        checked={config.requireLowercase}
                                        onChange={e => setConfig({ ...config, requireLowercase: e.target.checked })}
                                        className="w-4 h-4 rounded text-[#253C95] focus:ring-[#253C95]"
                                    />
                                    <span className="text-sm font-semibold text-slate-700">{t('admin.system.requireLowercase')}</span>
                                </label>

                                <label className="flex items-center gap-3 cursor-pointer">
                                    <input
                                        type="checkbox"
                                        checked={config.requireDigits}
                                        onChange={e => setConfig({ ...config, requireDigits: e.target.checked })}
                                        className="w-4 h-4 rounded text-[#253C95] focus:ring-[#253C95]"
                                    />
                                    <span className="text-sm font-semibold text-slate-700">{t('admin.system.requireDigits')}</span>
                                </label>

                                <label className="flex items-center gap-3 cursor-pointer">
                                    <input
                                        type="checkbox"
                                        checked={config.requireSpecialChar}
                                        onChange={e => setConfig({ ...config, requireSpecialChar: e.target.checked })}
                                        className="w-4 h-4 rounded text-[#253C95] focus:ring-[#253C95]"
                                    />
                                    <span className="text-sm font-semibold text-slate-700">{t('admin.system.requireSpecialChar')}</span>
                                </label>
                            </div>
                        </div>
                    </div>

                    {/* --- Section 3: Session Expiration & Concurrency Policies --- */}
                    <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-5">
                        <div className="flex items-center gap-3 border-b border-slate-100 pb-3">
                            <div className="p-2.5 rounded-xl bg-purple-50 text-purple-600">
                                <Laptop size={22} />
                            </div>
                            <div>
                                <h3 className="text-base sm:text-lg font-bold text-slate-800">{t('admin.system.sessionSection')}</h3>
                                <p className="text-xs sm:text-sm text-slate-400">Session inactivity timeout and multi-device access limits</p>
                            </div>
                        </div>

                        <div className="space-y-4">
                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">
                                        {t('admin.system.sessionIdleTimeout')}
                                    </label>
                                    <input
                                        type="number"
                                        min="5"
                                        max="1440"
                                        value={config.sessionIdleTimeoutMinutes}
                                        onChange={e => setConfig({ ...config, sessionIdleTimeoutMinutes: Number(e.target.value) })}
                                        className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 text-sm font-semibold focus:border-[#253C95] outline-none"
                                    />
                                    <p className="text-xs text-slate-400 mt-1">Logs out inactive browser tabs automatically.</p>
                                </div>

                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">
                                        {t('admin.system.sessionMaxInactiveDays')}
                                    </label>
                                    <input
                                        type="number"
                                        min="1"
                                        max="365"
                                        value={config.sessionMaxInactiveDays}
                                        onChange={e => setConfig({ ...config, sessionMaxInactiveDays: Number(e.target.value) })}
                                        className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 text-sm font-semibold focus:border-[#253C95] outline-none"
                                    />
                                    <p className="text-xs text-slate-400 mt-1">Clears dormant sessions from Redis cache.</p>
                                </div>
                            </div>

                            <div>
                                <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">
                                    {t('admin.system.maxConcurrentSessions')}
                                </label>
                                <input
                                    type="number"
                                    min="1"
                                    max="20"
                                    value={config.maxConcurrentSessions}
                                    onChange={e => setConfig({ ...config, maxConcurrentSessions: Number(e.target.value) })}
                                    className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 text-sm font-semibold focus:border-[#253C95] outline-none"
                                />
                                <p className="text-xs text-slate-400 mt-1">Maximum active simultaneous devices per user account.</p>
                            </div>

                            <div className="pt-2">
                                <label className="flex items-center gap-3 cursor-pointer">
                                    <input
                                        type="checkbox"
                                        checked={config.forceSingleSession}
                                        onChange={e => setConfig({ ...config, forceSingleSession: e.target.checked })}
                                        className="w-4 h-4 rounded text-[#253C95] focus:ring-[#253C95]"
                                    />
                                    <span className="text-sm font-semibold text-slate-700">{t('admin.system.forceSingleSession')}</span>
                                </label>
                            </div>
                        </div>
                    </div>

                    {/* --- Section 4: OAuth2 Token Lifetimes & Settings --- */}
                    <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-5">
                        <div className="flex items-center gap-3 border-b border-slate-100 pb-3">
                            <div className="p-2.5 rounded-xl bg-[#253C95]/10 text-[#253C95]">
                                <Clock size={22} />
                            </div>
                            <div>
                                <h3 className="text-base sm:text-lg font-bold text-slate-800">{t('admin.system.tokenSection')}</h3>
                                <p className="text-xs sm:text-sm text-slate-400">Spring Authorization Server OAuth2.1 token issuance parameters</p>
                            </div>
                        </div>

                        <div className="space-y-4">
                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">
                                        {t('admin.system.accessTokenTTL')}
                                    </label>
                                    <input
                                        type="number"
                                        min="1"
                                        max="72"
                                        value={config.accessTokenTtlHours}
                                        onChange={e => setConfig({ ...config, accessTokenTtlHours: Number(e.target.value) })}
                                        className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 text-sm font-semibold focus:border-[#253C95] outline-none"
                                    />
                                </div>

                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">
                                        {t('admin.system.refreshTokenTTL')}
                                    </label>
                                    <input
                                        type="number"
                                        min="1"
                                        max="90"
                                        value={config.refreshTokenTtlDays}
                                        onChange={e => setConfig({ ...config, refreshTokenTtlDays: Number(e.target.value) })}
                                        className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 text-sm font-semibold focus:border-[#253C95] outline-none"
                                    />
                                </div>
                            </div>

                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">
                                        {t('admin.system.authCodeTTL')}
                                    </label>
                                    <input
                                        type="number"
                                        min="1"
                                        max="15"
                                        value={config.authCodeTtlMinutes}
                                        onChange={e => setConfig({ ...config, authCodeTtlMinutes: Number(e.target.value) })}
                                        className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 text-sm font-semibold focus:border-[#253C95] outline-none"
                                    />
                                </div>

                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">
                                        {t('admin.system.tokenFormat')}
                                    </label>
                                    <select
                                        value={config.tokenFormat}
                                        onChange={e => setConfig({ ...config, tokenFormat: e.target.value })}
                                        className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 text-sm font-semibold focus:border-[#253C95] outline-none bg-white"
                                    >
                                        <option value="JWT">SELF_CONTAINED (JWT)</option>
                                        <option value="OPAQUE">REFERENCE (Opaque)</option>
                                    </select>
                                </div>
                            </div>

                            <div className="pt-2">
                                <label className="flex items-center gap-3 cursor-pointer">
                                    <input
                                        type="checkbox"
                                        checked={config.reuseRefreshTokens}
                                        onChange={e => setConfig({ ...config, reuseRefreshTokens: e.target.checked })}
                                        className="w-4 h-4 rounded text-[#253C95] focus:ring-[#253C95]"
                                    />
                                    <span className="text-sm font-semibold text-slate-700">{t('admin.system.reuseRefreshTokens')}</span>
                                </label>
                            </div>
                        </div>
                    </div>

                </form>
            )}
        </div>
    );
}
