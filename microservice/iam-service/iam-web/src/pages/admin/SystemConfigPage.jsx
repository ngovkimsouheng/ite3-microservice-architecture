import React, { useState, useEffect, useMemo } from 'react';
import { useTranslation } from 'react-i18next';
import {
    Shield, Clock, Lock, CheckCircle2,
    Save, RefreshCw, AlertTriangle, Key, ShieldCheck,
    RotateCcw, Laptop, UserCheck, Undo2, Check
} from 'lucide-react';
import { adminService } from '../../services/adminService';
import CustomSelect from '../../components/CustomSelect';

// Enterprise Toggle Switch Component
function ToggleSwitch({ checked, onChange, label, description, disabled = false }) {
    return (
        <label className={`flex items-start justify-between gap-4 p-3.5 rounded-xl border transition-all select-none cursor-pointer ${
            checked 
                ? 'bg-blue-50/40 border-[#253C95]/20 hover:border-[#253C95]/40' 
                : 'bg-slate-50/50 border-slate-200/80 hover:border-slate-300 hover:bg-slate-50'
        } ${disabled ? 'opacity-50 cursor-not-allowed' : ''}`}>
            <div className="flex-1 min-w-0 pr-2">
                <span className={`text-sm font-semibold block transition-colors ${checked ? 'text-[#253C95]' : 'text-slate-700'}`}>
                    {label}
                </span>
                {description && (
                    <p className="text-xs text-slate-400 mt-0.5 leading-relaxed">{description}</p>
                )}
            </div>
            <div className="relative inline-flex items-center shrink-0 mt-0.5">
                <input
                    type="checkbox"
                    checked={checked}
                    onChange={onChange}
                    disabled={disabled}
                    className="sr-only peer"
                />
                <div className="w-11 h-6 bg-slate-200 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-slate-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-[#253C95]"></div>
            </div>
        </label>
    );
}

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

    // Reference to original loaded state to calculate isDirty
    const [originalConfig, setOriginalConfig] = useState(null);

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
                const loaded = {
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
                };
                setConfig(loaded);
                setOriginalConfig(loaded);
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

    // Dirty form evaluation
    const dirtyKeys = useMemo(() => {
        if (!originalConfig) return [];
        return Object.keys(config).filter(key => {
            if (key === 'updatedAt' || key === 'updatedBy') return false;
            return config[key] !== originalConfig[key];
        });
    }, [config, originalConfig]);

    const isDirty = dirtyKeys.length > 0;

    // Check if a specific card section has unsaved edits
    const isSectionDirty = (keys) => keys.some(k => dirtyKeys.includes(k));

    // Warn administrator before leaving page if there are unsaved changes
    useEffect(() => {
        const handleBeforeUnload = (e) => {
            if (isDirty) {
                e.preventDefault();
                e.returnValue = '';
            }
        };
        window.addEventListener('beforeunload', handleBeforeUnload);
        return () => window.removeEventListener('beforeunload', handleBeforeUnload);
    }, [isDirty]);

    const handleDiscard = () => {
        if (originalConfig) {
            setConfig({ ...originalConfig });
            showToast('All unsaved modifications have been discarded.', 'info');
        }
    };

    const handleSaveConfig = async (e) => {
        if (e) e.preventDefault();
        if (!isDirty && !saving) return;

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
            const savedState = {
                ...config,
                ...payload,
                updatedAt: updated?.updatedAt || new Date().toISOString(),
                updatedBy: updated?.updatedBy || config.updatedBy
            };
            setConfig(savedState);
            setOriginalConfig(savedState);
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
                const resetState = {
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
                };
                setConfig(resetState);
                setOriginalConfig(resetState);
            }
            showToast('Configuration restored to factory defaults.');
        } catch (err) {
            console.error('Failed to reset config:', err);
            showToast('Failed to reset configuration.', 'error');
        } finally {
            setResetting(false);
        }
    };

    const mfaPolicyOptions = [
        { value: 'OPTIONAL', label: 'Optional (User may configure Authenticator / OTP)', description: 'Users can voluntarily configure TOTP / Google Authenticator' },
        { value: 'REQUIRED_ADMIN', label: 'Required for Administrators only', badge: 'ADMIN', description: 'Enforces Multi-Factor Authentication for administrator accounts' },
        { value: 'REQUIRED_ALL', label: 'Enforced for All System Accounts', badge: 'STRICT', description: 'Mandatory 2FA across all registered user accounts' },
        { value: 'DISABLED', label: 'Disabled Globally', description: 'Deactivates two-factor authentication platform-wide' }
    ];

    const tokenFormatOptions = [
        { value: 'JWT', label: 'SELF_CONTAINED (JWT)', badge: 'JWT', description: 'Signed cryptographically verifiable JSON Web Token' },
        { value: 'OPAQUE', label: 'REFERENCE (Opaque)', badge: 'OPAQUE', description: 'Random reference string verified against cache storage' }
    ];

    return (
        <div className="space-y-6 pb-20">
            {/* Toast Notification */}
            {toast.show && (
                <div className={`fixed bottom-6 right-6 z-50 flex items-center gap-3 px-5 py-3.5 rounded-xl shadow-xl text-white font-medium text-sm transition-all duration-300 animate-in fade-in slide-in-from-bottom-2 ${
                    toast.type === 'error' ? 'bg-[#DF1F28]' : toast.type === 'info' ? 'bg-slate-800' : 'bg-emerald-600'
                }`}>
                    {toast.type === 'error' ? <AlertTriangle size={18} /> : toast.type === 'info' ? <CheckCircle2 size={18} /> : <CheckCircle2 size={18} />}
                    <span>{toast.message}</span>
                </div>
            )}

            {/* Enterprise Header */}
            <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div>
                    <div className="flex flex-wrap items-center gap-3">
                        <h2 className="text-xl sm:text-2xl font-bold text-slate-800">{t('admin.system.title')}</h2>
                        {/* Dirty / Clean Indicator Pill */}
                        {isDirty ? (
                            <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold bg-amber-50 text-amber-700 border border-amber-300/80 animate-pulse">
                                <span className="w-2 h-2 rounded-full bg-amber-500 animate-ping"></span>
                                {dirtyKeys.length} unsaved {dirtyKeys.length === 1 ? 'change' : 'changes'}
                            </span>
                        ) : (
                            <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
                                <Check size={13} className="text-emerald-600 stroke-[3]" />
                                Up to date
                            </span>
                        )}
                    </div>
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
                        className="flex items-center gap-2 px-4 py-2.5 rounded-xl border border-slate-200 hover:bg-slate-50 text-slate-700 font-semibold text-sm transition-all cursor-pointer active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed"
                        title="Reload from server"
                    >
                        <RefreshCw size={16} className={loading ? 'animate-spin' : ''} />
                        <span className="hidden sm:inline">Refresh</span>
                    </button>

                    <button
                        type="button"
                        onClick={handleResetDefaults}
                        disabled={loading || saving || resetting}
                        className="flex items-center gap-2 px-4 py-2.5 rounded-xl border border-slate-200 hover:bg-slate-50 text-slate-700 font-semibold text-sm transition-all cursor-pointer active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed"
                        title={t('admin.system.resetDefaults')}
                    >
                        <RotateCcw size={16} className={resetting ? 'animate-spin' : ''} />
                        <span>Defaults</span>
                    </button>

                    {/* Discard Button (Shown only when form is dirty) */}
                    {isDirty && (
                        <button
                            type="button"
                            onClick={handleDiscard}
                            disabled={loading || saving}
                            className="flex items-center gap-2 px-4 py-2.5 rounded-xl border border-slate-200 hover:bg-slate-100 text-slate-700 font-semibold text-sm transition-all cursor-pointer active:scale-95 disabled:opacity-50 animate-in fade-in"
                            title="Discard unsaved changes"
                        >
                            <Undo2 size={16} className="text-slate-500" />
                            <span>Discard</span>
                        </button>
                    )}

                    {/* Save Changes Button (Disabled when clean, active when dirty) */}
                    <button
                        type="button"
                        onClick={handleSaveConfig}
                        disabled={!isDirty || loading || saving || resetting}
                        className={`flex items-center gap-2 px-5 py-2.5 rounded-xl font-semibold text-sm transition-all duration-200 ${
                            isDirty && !saving
                                ? 'bg-[#253C95] hover:bg-[#1d2f77] text-white shadow-md shadow-[#253C95]/25 cursor-pointer active:scale-95'
                                : 'bg-slate-100 text-slate-400 border border-slate-200 cursor-not-allowed'
                        }`}
                        title={!isDirty ? 'No changes to save' : 'Apply changes'}
                    >
                        {saving ? (
                            <RefreshCw size={17} className="animate-spin text-white" />
                        ) : isDirty ? (
                            <Save size={17} />
                        ) : (
                            <CheckCircle2 size={17} className="text-slate-400" />
                        )}
                        <span>{saving ? 'Saving...' : isDirty ? t('admin.system.saveConfig') : 'All Saved'}</span>
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
                    <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-5 transition-all">
                        <div className="flex items-center justify-between border-b border-slate-100 pb-3">
                            <div className="flex items-center gap-3">
                                <div className="p-2.5 rounded-xl bg-red-50 text-[#DF1F28]">
                                    <ShieldCheck size={22} />
                                </div>
                                <div>
                                    <h3 className="text-base sm:text-lg font-bold text-slate-800">{t('admin.system.authSection')}</h3>
                                    <p className="text-xs sm:text-sm text-slate-400">Brute-force lockout and Multi-Factor Authentication controls</p>
                                </div>
                            </div>
                            {isSectionDirty(['maxFailedAttempts', 'lockoutDurationMinutes', 'enableAutoUnlock', 'mfaPolicy']) && (
                                <span className="px-2.5 py-1 text-[11px] font-bold uppercase tracking-wider bg-amber-50 text-amber-700 rounded-full border border-amber-200/80">
                                    Modified
                                </span>
                            )}
                        </div>

                        <div className="space-y-4">
                            <div>
                                <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1.5">
                                    {t('admin.system.maxFailedAttempts')}
                                </label>
                                <input
                                    type="number"
                                    min="1"
                                    max="50"
                                    value={config.maxFailedAttempts}
                                    onChange={e => setConfig({ ...config, maxFailedAttempts: Number(e.target.value) })}
                                    className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 text-sm font-semibold text-slate-800 focus:border-[#253C95] focus:ring-2 focus:ring-[#253C95]/10 outline-none transition-all"
                                />
                                <p className="text-xs text-slate-400 mt-1">Freezes account after this many consecutive incorrect credentials.</p>
                            </div>

                            <div>
                                <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1.5">
                                    {t('admin.system.lockoutDuration')}
                                </label>
                                <input
                                    type="number"
                                    min="1"
                                    max="10080"
                                    value={config.lockoutDurationMinutes}
                                    onChange={e => setConfig({ ...config, lockoutDurationMinutes: Number(e.target.value) })}
                                    className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 text-sm font-semibold text-slate-800 focus:border-[#253C95] focus:ring-2 focus:ring-[#253C95]/10 outline-none transition-all"
                                />
                                <p className="text-xs text-slate-400 mt-1">Duration the account remains locked before credentials can be reattempted.</p>
                            </div>

                            <div>
                                <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1.5">
                                    {t('admin.system.mfaPolicy')}
                                </label>
                                <CustomSelect
                                    value={config.mfaPolicy}
                                    onChange={val => setConfig({ ...config, mfaPolicy: val })}
                                    options={mfaPolicyOptions}
                                    className="w-full"
                                />
                                <p className="text-xs text-slate-400 mt-1">Enforcement rule for Two-Factor / Multi-Factor Authentication.</p>
                            </div>

                            <div className="pt-1">
                                <ToggleSwitch
                                    checked={config.enableAutoUnlock}
                                    onChange={e => setConfig({ ...config, enableAutoUnlock: e.target.checked })}
                                    label={t('admin.system.enableAutoUnlock')}
                                    description="Automatically re-enables locked accounts once the lockout duration expires"
                                />
                            </div>
                        </div>
                    </div>

                    {/* --- Section 2: Enterprise Password Policy --- */}
                    <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-5 transition-all">
                        <div className="flex items-center justify-between border-b border-slate-100 pb-3">
                            <div className="flex items-center gap-3">
                                <div className="p-2.5 rounded-xl bg-amber-500/10 text-[#F9A825]">
                                    <Key size={22} />
                                </div>
                                <div>
                                    <h3 className="text-base sm:text-lg font-bold text-slate-800">{t('admin.system.passwordSection')}</h3>
                                    <p className="text-xs sm:text-sm text-slate-400">Credential complexity rules and rotation lifecycles</p>
                                </div>
                            </div>
                            {isSectionDirty(['minPasswordLength', 'passwordExpireDays', 'requireUppercase', 'requireLowercase', 'requireDigits', 'requireSpecialChar']) && (
                                <span className="px-2.5 py-1 text-[11px] font-bold uppercase tracking-wider bg-amber-50 text-amber-700 rounded-full border border-amber-200/80">
                                    Modified
                                </span>
                            )}
                        </div>

                        <div className="space-y-4">
                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1.5">
                                        {t('admin.system.minPasswordLength')}
                                    </label>
                                    <input
                                        type="number"
                                        min="6"
                                        max="64"
                                        value={config.minPasswordLength}
                                        onChange={e => setConfig({ ...config, minPasswordLength: Number(e.target.value) })}
                                        className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 text-sm font-semibold text-slate-800 focus:border-[#253C95] focus:ring-2 focus:ring-[#253C95]/10 outline-none transition-all"
                                    />
                                    <p className="text-xs text-slate-400 mt-1">Minimum required characters.</p>
                                </div>

                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1.5">
                                        {t('admin.system.passwordExpireDays')}
                                    </label>
                                    <input
                                        type="number"
                                        min="0"
                                        max="730"
                                        value={config.passwordExpireDays}
                                        onChange={e => setConfig({ ...config, passwordExpireDays: Number(e.target.value) })}
                                        className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 text-sm font-semibold text-slate-800 focus:border-[#253C95] focus:ring-2 focus:ring-[#253C95]/10 outline-none transition-all"
                                    />
                                    <p className="text-xs text-slate-400 mt-1">Set to 0 to disable expiration.</p>
                                </div>
                            </div>

                            <div className="space-y-2.5 pt-1">
                                <ToggleSwitch
                                    checked={config.requireUppercase}
                                    onChange={e => setConfig({ ...config, requireUppercase: e.target.checked })}
                                    label={t('admin.system.requireUppercase')}
                                    description="Must contain at least one capital letter (A-Z)"
                                />

                                <ToggleSwitch
                                    checked={config.requireLowercase}
                                    onChange={e => setConfig({ ...config, requireLowercase: e.target.checked })}
                                    label={t('admin.system.requireLowercase')}
                                    description="Must contain at least one small letter (a-z)"
                                />

                                <ToggleSwitch
                                    checked={config.requireDigits}
                                    onChange={e => setConfig({ ...config, requireDigits: e.target.checked })}
                                    label={t('admin.system.requireDigits')}
                                    description="Must contain at least one numerical digit (0-9)"
                                />

                                <ToggleSwitch
                                    checked={config.requireSpecialChar}
                                    onChange={e => setConfig({ ...config, requireSpecialChar: e.target.checked })}
                                    label={t('admin.system.requireSpecialChar')}
                                    description="Must contain at least one symbol (@$!%*?&)"
                                />
                            </div>
                        </div>
                    </div>

                    {/* --- Section 3: Session Expiration & Concurrency Policies --- */}
                    <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-5 transition-all">
                        <div className="flex items-center justify-between border-b border-slate-100 pb-3">
                            <div className="flex items-center gap-3">
                                <div className="p-2.5 rounded-xl bg-purple-50 text-purple-600">
                                    <Laptop size={22} />
                                </div>
                                <div>
                                    <h3 className="text-base sm:text-lg font-bold text-slate-800">{t('admin.system.sessionSection')}</h3>
                                    <p className="text-xs sm:text-sm text-slate-400">Session inactivity timeout and multi-device access limits</p>
                                </div>
                            </div>
                            {isSectionDirty(['sessionIdleTimeoutMinutes', 'sessionMaxInactiveDays', 'maxConcurrentSessions', 'forceSingleSession']) && (
                                <span className="px-2.5 py-1 text-[11px] font-bold uppercase tracking-wider bg-amber-50 text-amber-700 rounded-full border border-amber-200/80">
                                    Modified
                                </span>
                            )}
                        </div>

                        <div className="space-y-4">
                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1.5">
                                        {t('admin.system.sessionIdleTimeout')}
                                    </label>
                                    <input
                                        type="number"
                                        min="5"
                                        max="1440"
                                        value={config.sessionIdleTimeoutMinutes}
                                        onChange={e => setConfig({ ...config, sessionIdleTimeoutMinutes: Number(e.target.value) })}
                                        className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 text-sm font-semibold text-slate-800 focus:border-[#253C95] focus:ring-2 focus:ring-[#253C95]/10 outline-none transition-all"
                                    />
                                    <p className="text-xs text-slate-400 mt-1">Logs out inactive browser tabs automatically.</p>
                                </div>

                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1.5">
                                        {t('admin.system.sessionMaxInactiveDays')}
                                    </label>
                                    <input
                                        type="number"
                                        min="1"
                                        max="365"
                                        value={config.sessionMaxInactiveDays}
                                        onChange={e => setConfig({ ...config, sessionMaxInactiveDays: Number(e.target.value) })}
                                        className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 text-sm font-semibold text-slate-800 focus:border-[#253C95] focus:ring-2 focus:ring-[#253C95]/10 outline-none transition-all"
                                    />
                                    <p className="text-xs text-slate-400 mt-1">Clears dormant sessions from Redis cache.</p>
                                </div>
                            </div>

                            <div>
                                <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1.5">
                                    {t('admin.system.maxConcurrentSessions')}
                                </label>
                                <input
                                    type="number"
                                    min="1"
                                    max="20"
                                    value={config.maxConcurrentSessions}
                                    onChange={e => setConfig({ ...config, maxConcurrentSessions: Number(e.target.value) })}
                                    className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 text-sm font-semibold text-slate-800 focus:border-[#253C95] focus:ring-2 focus:ring-[#253C95]/10 outline-none transition-all"
                                />
                                <p className="text-xs text-slate-400 mt-1">Maximum active simultaneous devices per user account.</p>
                            </div>

                            <div className="pt-1">
                                <ToggleSwitch
                                    checked={config.forceSingleSession}
                                    onChange={e => setConfig({ ...config, forceSingleSession: e.target.checked })}
                                    label={t('admin.system.forceSingleSession')}
                                    description="Terminates older browser sessions immediately upon new login"
                                />
                            </div>
                        </div>
                    </div>

                    {/* --- Section 4: OAuth2 Token Lifetimes & Settings --- */}
                    <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-5 transition-all">
                        <div className="flex items-center justify-between border-b border-slate-100 pb-3">
                            <div className="flex items-center gap-3">
                                <div className="p-2.5 rounded-xl bg-[#253C95]/10 text-[#253C95]">
                                    <Clock size={22} />
                                </div>
                                <div>
                                    <h3 className="text-base sm:text-lg font-bold text-slate-800">{t('admin.system.tokenSection')}</h3>
                                    <p className="text-xs sm:text-sm text-slate-400">Spring Authorization Server OAuth2.1 token issuance parameters</p>
                                </div>
                            </div>
                            {isSectionDirty(['accessTokenTtlHours', 'refreshTokenTtlDays', 'authCodeTtlMinutes', 'reuseRefreshTokens', 'tokenFormat']) && (
                                <span className="px-2.5 py-1 text-[11px] font-bold uppercase tracking-wider bg-amber-50 text-amber-700 rounded-full border border-amber-200/80">
                                    Modified
                                </span>
                            )}
                        </div>

                        <div className="space-y-4">
                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1.5">
                                        {t('admin.system.accessTokenTTL')}
                                    </label>
                                    <input
                                        type="number"
                                        min="1"
                                        max="72"
                                        value={config.accessTokenTtlHours}
                                        onChange={e => setConfig({ ...config, accessTokenTtlHours: Number(e.target.value) })}
                                        className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 text-sm font-semibold text-slate-800 focus:border-[#253C95] focus:ring-2 focus:ring-[#253C95]/10 outline-none transition-all"
                                    />
                                    <p className="text-xs text-slate-400 mt-1">Resource access token lifetime in hours.</p>
                                </div>

                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1.5">
                                        {t('admin.system.refreshTokenTTL')}
                                    </label>
                                    <input
                                        type="number"
                                        min="1"
                                        max="90"
                                        value={config.refreshTokenTtlDays}
                                        onChange={e => setConfig({ ...config, refreshTokenTtlDays: Number(e.target.value) })}
                                        className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 text-sm font-semibold text-slate-800 focus:border-[#253C95] focus:ring-2 focus:ring-[#253C95]/10 outline-none transition-all"
                                    />
                                    <p className="text-xs text-slate-400 mt-1">Refresh token validity window in days.</p>
                                </div>
                            </div>

                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1.5">
                                        {t('admin.system.authCodeTTL')}
                                    </label>
                                    <input
                                        type="number"
                                        min="1"
                                        max="15"
                                        value={config.authCodeTtlMinutes}
                                        onChange={e => setConfig({ ...config, authCodeTtlMinutes: Number(e.target.value) })}
                                        className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 text-sm font-semibold text-slate-800 focus:border-[#253C95] focus:ring-2 focus:ring-[#253C95]/10 outline-none transition-all"
                                    />
                                    <p className="text-xs text-slate-400 mt-1">PKCE authorization code exchange window.</p>
                                </div>

                                <div>
                                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1.5">
                                        {t('admin.system.tokenFormat')}
                                    </label>
                                    <CustomSelect
                                        value={config.tokenFormat}
                                        onChange={val => setConfig({ ...config, tokenFormat: val })}
                                        options={tokenFormatOptions}
                                        className="w-full"
                                    />
                                    <p className="text-xs text-slate-400 mt-1">Format of signed client authorization tokens.</p>
                                </div>
                            </div>

                            <div className="pt-1">
                                <ToggleSwitch
                                    checked={config.reuseRefreshTokens}
                                    onChange={e => setConfig({ ...config, reuseRefreshTokens: e.target.checked })}
                                    label={t('admin.system.reuseRefreshTokens')}
                                    description="If disabled, automatically rotates refresh token on every exchange request"
                                />
                            </div>
                        </div>
                    </div>

                </form>
            )}

            {/* Enterprise Floating Sticky Save / Discard Bar (Appears when dirty) */}
            {isDirty && (
                <div className="fixed bottom-6 left-1/2 -translate-x-1/2 z-50 max-w-2xl w-[92%] sm:w-auto min-w-[340px] sm:min-w-[500px] bg-slate-900/95 backdrop-blur-md text-white px-5 py-3.5 rounded-2xl shadow-2xl border border-slate-700/80 flex items-center justify-between gap-4 transition-all duration-300 animate-in fade-in slide-in-from-bottom-5">
                    <div className="flex items-center gap-3 min-w-0">
                        <span className="flex h-3 w-3 relative shrink-0">
                            <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-amber-400 opacity-75"></span>
                            <span className="relative inline-flex rounded-full h-3 w-3 bg-amber-500"></span>
                        </span>
                        <div className="min-w-0">
                            <p className="text-sm font-bold text-white truncate">Unsaved policy changes</p>
                            <p className="text-xs text-slate-400 truncate">
                                {dirtyKeys.length} {dirtyKeys.length === 1 ? 'setting' : 'settings'} modified. Save before leaving.
                            </p>
                        </div>
                    </div>

                    <div className="flex items-center gap-2.5 shrink-0">
                        <button
                            type="button"
                            onClick={handleDiscard}
                            disabled={saving}
                            className="px-3.5 py-2 rounded-xl text-xs sm:text-sm font-semibold text-slate-300 hover:text-white hover:bg-slate-800 transition-colors cursor-pointer"
                        >
                            Discard
                        </button>
                        <button
                            type="button"
                            onClick={handleSaveConfig}
                            disabled={saving}
                            className="flex items-center gap-2 px-4 py-2 rounded-xl bg-[#253C95] hover:bg-[#1d2f77] text-white font-semibold text-xs sm:text-sm shadow-md shadow-[#253C95]/40 transition-all cursor-pointer active:scale-95"
                        >
                            {saving ? <RefreshCw size={15} className="animate-spin" /> : <Save size={15} />}
                            <span>{saving ? 'Saving...' : 'Save Changes'}</span>
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
}
