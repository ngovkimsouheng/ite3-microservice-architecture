import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
    Sliders, Shield, Clock, Lock, CheckCircle2,
    Save, RefreshCw, AlertTriangle, Key, ShieldCheck
} from 'lucide-react';

export default function SystemConfigPage() {
    const { t } = useTranslation();

    // Configuration state matching Spring Authorization Server settings
    const [config, setConfig] = useState({
        // Token Durations
        accessTokenTtlHours: 5,
        refreshTokenTtlDays: 7,
        authCodeTtlMinutes: 3,
        reuseRefreshTokens: false,
        tokenFormat: 'SELF_CONTAINED (JWT)',

        // Password Policy
        minPasswordLength: 8,
        requireUppercase: true,
        requireLowercase: true,
        requireDigits: true,
        requireSpecialChar: true,
        passwordExpireDays: 90,

        // Lockout Settings
        maxFailedAttempts: 5,
        lockoutDurationMinutes: 30,
        enableAutoUnlock: true
    });

    const [saving, setSaving] = useState(false);
    const [toast, setToast] = useState({ show: false, message: '', type: 'success' });

    const showToast = (message, type = 'success') => {
        setToast({ show: true, message, type });
        setTimeout(() => setToast({ show: false, message: '', type: 'success' }), 3500);
    };

    const handleSaveConfig = (e) => {
        e.preventDefault();
        setSaving(true);
        setTimeout(() => {
            setSaving(false);
            showToast('System configuration policies updated successfully.');
        }, 600);
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
                </div>
                <button
                    onClick={handleSaveConfig}
                    disabled={saving}
                    className="flex items-center gap-2 px-5 py-2.5 rounded-xl bg-[#253C95] hover:bg-[#1d2f77] text-white font-semibold text-sm shadow-md shadow-[#253C95]/20 transition-all cursor-pointer active:scale-95 disabled:opacity-60"
                >
                    {saving ? <RefreshCw size={18} className="animate-spin" /> : <Save size={18} />}
                    <span>{saving ? 'Applying...' : t('admin.system.saveConfig')}</span>
                </button>
            </div>

            <form onSubmit={handleSaveConfig} className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                {/* --- Section 1: OAuth2 Token Durations --- */}
                <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-4">
                    <div className="flex items-center gap-3 border-b border-slate-100 pb-3">
                        <div className="p-2.5 rounded-xl bg-[#253C95]/10 text-[#253C95]">
                            <Clock size={22} />
                        </div>
                        <div>
                            <h3 className="text-base sm:text-lg font-bold text-slate-800">{t('admin.system.tokenSection')}</h3>
                            <p className="text-xs sm:text-sm text-slate-400">OAuth2.1 TokenSettings in Spring Authorization Server</p>
                        </div>
                    </div>

                    <div className="space-y-4">
                        <div>
                            <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">
                                {t('admin.system.accessTokenTTL')} (Hours)
                            </label>
                            <input
                                type="number"
                                min="1"
                                max="72"
                                value={config.accessTokenTtlHours}
                                onChange={e => setConfig({ ...config, accessTokenTtlHours: Number(e.target.value) })}
                                className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm sm:text-base font-semibold focus:border-[#253C95] outline-none"
                            />
                            <p className="text-xs text-slate-400 mt-1">Default: 5 hours for self-contained JWT tokens.</p>
                        </div>

                        <div>
                            <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">
                                {t('admin.system.refreshTokenTTL')} (Days)
                            </label>
                            <input
                                type="number"
                                min="1"
                                max="90"
                                value={config.refreshTokenTtlDays}
                                onChange={e => setConfig({ ...config, refreshTokenTtlDays: Number(e.target.value) })}
                                className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm sm:text-base font-semibold focus:border-[#253C95] outline-none"
                            />
                            <p className="text-xs text-slate-400 mt-1">Default: 7 days before full re-authentication required.</p>
                        </div>

                        <div>
                            <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">
                                {t('admin.system.authCodeTTL')} (Minutes)
                            </label>
                            <input
                                type="number"
                                min="1"
                                max="15"
                                value={config.authCodeTtlMinutes}
                                onChange={e => setConfig({ ...config, authCodeTtlMinutes: Number(e.target.value) })}
                                className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm sm:text-base font-semibold focus:border-[#253C95] outline-none"
                            />
                        </div>

                        <div className="pt-2">
                            <label className="flex items-center gap-3 cursor-pointer">
                                <input
                                    type="checkbox"
                                    checked={config.reuseRefreshTokens}
                                    onChange={e => setConfig({ ...config, reuseRefreshTokens: e.target.checked })}
                                    className="w-4 h-4 rounded text-[#253C95] focus:ring-[#253C95]"
                                />
                                <span className="text-sm font-semibold text-slate-700">Allow Refresh Token Reuse (Rotates on each request if unchecked)</span>
                            </label>
                        </div>
                    </div>
                </div>

                {/* --- Section 2: Enterprise Password Policy --- */}
                <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-4">
                    <div className="flex items-center gap-3 border-b border-slate-100 pb-3">
                        <div className="p-2.5 rounded-xl bg-amber-500/10 text-[#F9A825]">
                            <Key size={22} />
                        </div>
                        <div>
                            <h3 className="text-base sm:text-lg font-bold text-slate-800">{t('admin.system.passwordSection')}</h3>
                            <p className="text-xs sm:text-sm text-slate-400">Credential complexity and validation requirements</p>
                        </div>
                    </div>

                    <div className="space-y-4">
                        <div>
                            <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">
                                {t('admin.system.minPasswordLength')}
                            </label>
                            <input
                                type="number"
                                min="6"
                                max="32"
                                value={config.minPasswordLength}
                                onChange={e => setConfig({ ...config, minPasswordLength: Number(e.target.value) })}
                                className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm sm:text-base font-semibold focus:border-[#253C95] outline-none"
                            />
                        </div>

                        <div className="space-y-2 pt-1">
                            <label className="flex items-center gap-3 cursor-pointer">
                                <input
                                    type="checkbox"
                                    checked={config.requireSpecialChar}
                                    onChange={e => setConfig({ ...config, requireSpecialChar: e.target.checked })}
                                    className="w-4 h-4 rounded text-[#253C95] focus:ring-[#253C95]"
                                />
                                <span className="text-sm font-semibold text-slate-700">{t('admin.system.requireSpecialChar')}</span>
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
                                    checked={config.requireUppercase}
                                    onChange={e => setConfig({ ...config, requireUppercase: e.target.checked })}
                                    className="w-4 h-4 rounded text-[#253C95] focus:ring-[#253C95]"
                                />
                                <span className="text-sm font-semibold text-slate-700">Require Mixed Case (Uppercase & Lowercase)</span>
                            </label>
                        </div>
                    </div>
                </div>

                {/* --- Section 3: Lockout Rules --- */}
                <div className="lg:col-span-2 bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-4">
                    <div className="flex items-center gap-3 border-b border-slate-100 pb-3">
                        <div className="p-2.5 rounded-xl bg-red-50 text-[#DF1F28]">
                            <Lock size={22} />
                        </div>
                        <div>
                            <h3 className="text-base sm:text-lg font-bold text-slate-800">{t('admin.system.lockoutSection')}</h3>
                            <p className="text-xs sm:text-sm text-slate-400">Brute-force protection and account freeze thresholds</p>
                        </div>
                    </div>

                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                        <div>
                            <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">
                                {t('admin.system.maxFailedAttempts')}
                            </label>
                            <input
                                type="number"
                                min="3"
                                max="20"
                                value={config.maxFailedAttempts}
                                onChange={e => setConfig({ ...config, maxFailedAttempts: Number(e.target.value) })}
                                className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm sm:text-base font-semibold focus:border-[#253C95] outline-none"
                            />
                            <p className="text-xs text-slate-400 mt-1">Freezes account when exceeded.</p>
                        </div>

                        <div>
                            <label className="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1">
                                {t('admin.system.lockoutDuration')}
                            </label>
                            <input
                                type="number"
                                min="5"
                                max="1440"
                                value={config.lockoutDurationMinutes}
                                onChange={e => setConfig({ ...config, lockoutDurationMinutes: Number(e.target.value) })}
                                className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm sm:text-base font-semibold focus:border-[#253C95] outline-none"
                            />
                            <p className="text-xs text-slate-400 mt-1">Automatic unlock after this time window.</p>
                        </div>
                    </div>
                </div>
            </form>
        </div>
    );
}

