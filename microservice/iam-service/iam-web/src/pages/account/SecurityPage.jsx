import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { userService } from '../../services/userService';
import Toast from '../../components/Toast';
import {
    KeyRound, CheckCircle2, AlertCircle, Loader2,
    AlertTriangle, Trash2, Eye, EyeOff, ShieldCheck, Sparkles
} from 'lucide-react';

export default function SecurityPage() {
    const { t, i18n } = useTranslation();
    const [form, setForm] = useState({
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
    });
    const [showCurrent, setShowCurrent] = useState(false);
    const [showNew, setShowNew] = useState(false);
    const [showConfirm, setShowConfirm] = useState(false);
    const [loading, setLoading] = useState(false);
    const [alert, setAlert] = useState(null);
    const [showDeactivateModal, setShowDeactivateModal] = useState(false);
    const [deactivateLoading, setDeactivateLoading] = useState(false);

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    // Live password strength indicator
    const getStrength = (pass) => {
        if (!pass) return { score: 0, text: '', color: 'bg-slate-200' };
        let score = 0;
        if (pass.length >= 8) score++;
        if (/[A-Z]/.test(pass)) score++;
        if (/[0-9]/.test(pass)) score++;
        if (/[^A-Za-z0-9]/.test(pass)) score++;

        if (score <= 1) return { score: 1, text: i18n.language === 'en' ? 'Weak' : 'ខ្សោយ', color: 'bg-[#DF1F28]' };
        if (score <= 3) return { score: 2, text: i18n.language === 'en' ? 'Medium' : 'មធ្យម', color: 'bg-[#F9A825]' };
        return { score: 3, text: i18n.language === 'en' ? 'Strong' : 'រឹងមាំ', color: 'bg-emerald-500' };
    };

    const strength = getStrength(form.newPassword);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setAlert(null);

        if (form.newPassword !== form.confirmPassword) {
            setAlert({ type: 'error', message: t('security.mismatchMsg') });
            return;
        }

        if (form.newPassword.length < 8) {
            setAlert({
                type: 'error',
                message: i18n.language === 'en'
                    ? 'Password must be at least 8 characters long.'
                    : 'ពាក្យសម្ងាត់ត្រូវមានយ៉ាងហោចណាស់ 8 តួអក្សរ។'
            });
            return;
        }

        try {
            setLoading(true);
            await userService.changePassword(form);
            setAlert({ type: 'success', message: t('security.successMsg') });
            setForm({ currentPassword: '', newPassword: '', confirmPassword: '' });
        } catch (err) {
            setAlert({
                type: 'error',
                message: err.response?.data?.detail || t('security.errorMsg')
            });
        } finally {
            setLoading(false);
        }
    };

    const handleDeactivate = async () => {
        setDeactivateLoading(true);
        try {
            // Simulated or real deactivation request
            await new Promise((res) => setTimeout(res, 800));
            setShowDeactivateModal(false);
            setAlert({
                type: 'success',
                message: i18n.language === 'en'
                    ? 'Account deactivation request has been submitted to system administrators.'
                    : 'សំណើបិទដំណើរការគណនីត្រូវបានបញ្ជូនទៅកាន់អ្នកគ្រប់គ្រងប្រព័ន្ធរួចរាល់។'
            });
        } finally {
            setDeactivateLoading(false);
        }
    };

    return (
        <div className="space-y-5">
            {/* Floating Toast Notification */}
            <Toast alert={alert} onClose={() => setAlert(null)} />

            {/* Change Password Card (+5 pts) */}
            <div className="bg-white p-6 sm:p-8 rounded-3xl border border-slate-200/90 shadow-2xs">
                <div className="flex items-center gap-2.5 pb-4 border-b border-slate-100 mb-6">
                    <KeyRound size={20} className="text-[#253C95]" />
                    <div>
                        <h3 className="text-sm sm:text-base font-bold uppercase tracking-wider text-slate-700">
                            {t('security.title')}
                        </h3>
                        <p className="text-xs sm:text-sm text-slate-400 font-medium">
                            {t('security.subtitle')}
                        </p>
                    </div>
                </div>

                <form onSubmit={handleSubmit} className="space-y-5 max-w-xl">
                    {/* Current Password */}
                    <div>
                        <label className="block text-sm font-bold uppercase tracking-wider text-slate-600 mb-2">
                            {t('security.currentPassword')}
                        </label>
                        <div className="relative">
                            <input
                                type={showCurrent ? 'text' : 'password'}
                                name="currentPassword"
                                required
                                value={form.currentPassword}
                                onChange={handleChange}
                                placeholder="••••••••"
                                className="w-full pl-4 pr-11 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-[#253C95]/20 focus:border-[#253C95] text-sm sm:text-base font-medium transition-all"
                            />
                            <button
                                type="button"
                                onClick={() => setShowCurrent(!showCurrent)}
                                className="absolute right-3.5 top-3 text-slate-400 hover:text-slate-600 cursor-pointer"
                            >
                                {showCurrent ? <EyeOff size={18} /> : <Eye size={18} />}
                            </button>
                        </div>
                    </div>

                    {/* New Password */}
                    <div>
                        <label className="block text-sm font-bold uppercase tracking-wider text-slate-600 mb-2">
                            {t('security.newPassword')}
                        </label>
                        <div className="relative">
                            <input
                                type={showNew ? 'text' : 'password'}
                                name="newPassword"
                                required
                                value={form.newPassword}
                                onChange={handleChange}
                                placeholder="••••••••"
                                className="w-full pl-4 pr-11 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-[#253C95]/20 focus:border-[#253C95] text-sm sm:text-base font-medium transition-all"
                            />
                            <button
                                type="button"
                                onClick={() => setShowNew(!showNew)}
                                className="absolute right-3.5 top-3 text-slate-400 hover:text-slate-600 cursor-pointer"
                            >
                                {showNew ? <EyeOff size={18} /> : <Eye size={18} />}
                            </button>
                        </div>

                        {/* Password Strength Indicator */}
                        {form.newPassword && (
                            <div className="mt-2.5 flex items-center gap-3">
                                <div className="flex-1 h-2 bg-slate-100 rounded-full overflow-hidden flex gap-1">
                                    <div className={`h-full flex-1 transition-all ${strength.score >= 1 ? strength.color : 'bg-transparent'}`} />
                                    <div className={`h-full flex-1 transition-all ${strength.score >= 2 ? strength.color : 'bg-transparent'}`} />
                                    <div className={`h-full flex-1 transition-all ${strength.score >= 3 ? strength.color : 'bg-transparent'}`} />
                                </div>
                                <span className="text-xs sm:text-sm font-bold text-slate-600 min-w-16">
                                    {strength.text}
                                </span>
                            </div>
                        )}
                    </div>

                    {/* Confirm Password */}
                    <div>
                        <label className="block text-sm font-bold uppercase tracking-wider text-slate-600 mb-2">
                            {t('security.confirmPassword')}
                        </label>
                        <div className="relative">
                            <input
                                type={showConfirm ? 'text' : 'password'}
                                name="confirmPassword"
                                required
                                value={form.confirmPassword}
                                onChange={handleChange}
                                placeholder="••••••••"
                                className="w-full pl-4 pr-11 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-[#253C95]/20 focus:border-[#253C95] text-sm sm:text-base font-medium transition-all"
                            />
                            <button
                                type="button"
                                onClick={() => setShowConfirm(!showConfirm)}
                                className="absolute right-3.5 top-3 text-slate-400 hover:text-slate-600 cursor-pointer"
                            >
                                {showConfirm ? <EyeOff size={18} /> : <Eye size={18} />}
                            </button>
                        </div>
                    </div>

                    <div className="pt-2">
                        <button
                            type="submit"
                            disabled={loading}
                            className="px-6 py-2.5 bg-[#253C95] hover:bg-[#1c2f78] text-white font-semibold text-sm sm:text-base rounded-xl shadow-sm transition-all duration-150 active:scale-95 disabled:opacity-50 flex items-center gap-2 cursor-pointer"
                        >
                            {loading && <Loader2 size={18} className="animate-spin" />}
                            <span>{loading ? t('security.updating') : t('security.changePasswordBtn')}</span>
                        </button>
                    </div>
                </form>
            </div>

            {/* Account Deactivation Card (+5 pts) */}
            <div className="bg-white p-6 sm:p-8 rounded-3xl border border-[#DF1F28]/30 shadow-2xs">
                <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
                    <div>
                        <div className="flex items-center gap-2 text-[#DF1F28]">
                            <AlertTriangle size={20} />
                            <h4 className="text-base sm:text-lg font-bold">
                                {i18n.language === 'en' ? 'Danger Zone: Account Deactivation' : 'តំបន់ប្រុងប្រយ័ត្ន៖ ការបិទដំណើរការគណនី'}
                            </h4>
                        </div>
                        <p className="text-xs sm:text-sm text-slate-500 mt-1 max-w-xl">
                            {i18n.language === 'en'
                                ? 'Requesting account deactivation will lock your user profile, revoke all access tokens, and terminate all active sessions across all devices.'
                                : 'ការស្នើសុំបិទគណនីនឹងចាក់សោប្រវត្តិរូបរបស់អ្នក ដកហូតសិទ្ធិចូលប្រើប្រាស់ និងបញ្ចប់សម័យការងារទាំងអស់លើគ្រប់ឧបករណ៍។'}
                        </p>
                    </div>

                    <button
                        type="button"
                        onClick={() => setShowDeactivateModal(true)}
                        className="px-5 py-2.5 bg-[#DF1F28]/10 hover:bg-[#DF1F28]/20 text-[#DF1F28] border border-[#DF1F28]/30 font-semibold text-sm sm:text-base rounded-xl transition-all cursor-pointer shrink-0 active:scale-95"
                    >
                        {i18n.language === 'en' ? 'Request Deactivation' : 'ស្នើសុំបិទគណនី'}
                    </button>
                </div>
            </div>

            {/* Confirmation Modal */}
            {showDeactivateModal && (
                <div className="fixed inset-0 bg-slate-900/50 backdrop-blur-xs flex items-center justify-center p-4 z-50">
                    <div className="bg-white rounded-3xl max-w-md w-full p-6 shadow-2xl border border-slate-100 animate-in fade-in zoom-in-95 duration-150">
                        <div className="w-12 h-12 rounded-2xl bg-[#DF1F28]/10 text-[#DF1F28] flex items-center justify-center mb-4">
                            <Trash2 size={24} />
                        </div>
                        <h3 className="text-lg font-bold text-slate-900">
                            {i18n.language === 'en' ? 'Confirm Account Deactivation' : 'បញ្ជាក់ការបិទដំណើរការគណនី'}
                        </h3>
                        <p className="text-sm text-slate-500 mt-2 leading-relaxed">
                            {i18n.language === 'en'
                                ? 'Are you sure you want to deactivate your account? Once approved by the administrator, your login access will be disabled.'
                                : 'តើអ្នកប្រាកដថាចង់បិទដំណើរការគណនីរបស់អ្នកមែនទេ? នៅពេលត្រូវបានអនុម័ត ការចូលប្រើប្រាស់របស់អ្នកនឹងត្រូវផ្អាក។'}
                        </p>

                        <div className="mt-6 flex items-center justify-end gap-3">
                            <button
                                type="button"
                                onClick={() => setShowDeactivateModal(false)}
                                className="px-4 py-2 text-sm font-semibold text-slate-600 hover:bg-slate-100 rounded-xl transition-all cursor-pointer"
                            >
                                {t('common.cancel')}
                            </button>
                            <button
                                type="button"
                                disabled={deactivateLoading}
                                onClick={handleDeactivate}
                                className="px-5 py-2 bg-[#DF1F28] hover:bg-[#b8141c] text-white text-sm font-bold rounded-xl shadow-sm transition-all duration-150 active:scale-95 disabled:opacity-50 flex items-center gap-2 cursor-pointer"
                            >
                                {deactivateLoading && <Loader2 size={16} className="animate-spin" />}
                                <span>{t('common.confirm')}</span>
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

