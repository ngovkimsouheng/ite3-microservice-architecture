import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
    KeyRound, User, Mail, Lock, Eye, EyeOff,
    CheckCircle2, AlertCircle, Loader2, X, ShieldCheck
} from 'lucide-react';
import { userService } from '../services/userService';

export default function ResetPasswordModal({ isOpen, onClose, initialUsername = '', onSuccess }) {
    const { t, i18n } = useTranslation();
    const [username, setUsername] = useState(initialUsername);
    const [email, setEmail] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [showNew, setShowNew] = useState(false);
    const [showConfirm, setShowConfirm] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    if (!isOpen) return null;

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

    const strength = getStrength(newPassword);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);

        if (!username.trim()) {
            setError(i18n.language === 'en' ? 'Please enter your username or identifier.' : 'សូមបញ្ចូលឈ្មោះអ្នកប្រើប្រាស់របស់អ្នក។');
            return;
        }

        if (!email.trim()) {
            setError(i18n.language === 'en' ? 'Please enter your registered email address.' : 'សូមបញ្ចូលអាសយដ្ឋានអ៊ីមែលដែលបានចុះឈ្មោះ។');
            return;
        }

        if (newPassword.length < 8) {
            setError(i18n.language === 'en' ? 'Password must be at least 8 characters long.' : 'ពាក្យសម្ងាត់ត្រូវមានយ៉ាងហោចណាស់ 8 តួអក្សរ។');
            return;
        }

        if (newPassword !== confirmPassword) {
            setError(i18n.language === 'en' ? 'New passwords do not match.' : 'ពាក្យសម្ងាត់ថ្មីមិនត្រូវគ្នាទេ។');
            return;
        }

        try {
            setLoading(true);
            const res = await userService.resetPassword({
                username: username.trim(),
                email: email.trim(),
                newPassword,
                confirmPassword
            });

            if (onSuccess) {
                onSuccess(res.username || username.trim());
            }
            onClose();
        } catch (err) {
            const msg = err.response?.data?.detail
                || err.response?.data?.message
                || (i18n.language === 'en' ? 'Failed to reset password. Please check your credentials.' : 'មិនអាចកំណត់ពាក្យសម្ងាត់ឡើងវិញបានទេ។');
            setError(msg);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-xs animate-in fade-in duration-200">
            <div className="bg-white w-full max-w-md rounded-3xl border border-slate-200/90 shadow-2xl p-6 sm:p-7 relative overflow-hidden">
                {/* Header */}
                <div className="flex items-start justify-between pb-4 border-b border-slate-100">
                    <div className="flex items-center gap-3">
                        <div className="w-10 h-10 rounded-2xl bg-[#253C95]/10 text-[#253C95] flex items-center justify-center shrink-0">
                            <KeyRound size={20} />
                        </div>
                        <div>
                            <h3 className="text-base sm:text-lg font-bold text-slate-900 leading-tight">
                                {i18n.language === 'en' ? 'Reset Password' : 'កំណត់ពាក្យសម្ងាត់ឡើងវិញ'}
                            </h3>
                            <p className="text-xs text-slate-500 font-medium mt-0.5">
                                {i18n.language === 'en'
                                    ? 'Verify your identity to set a new password.'
                                    : 'ផ្ទៀងផ្ទាត់អត្តសញ្ញាណរបស់អ្នកដើម្បីកំណត់ពាក្យសម្ងាត់ថ្មី។'}
                            </p>
                        </div>
                    </div>
                    <button
                        type="button"
                        onClick={onClose}
                        className="p-1.5 text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-xl transition-colors cursor-pointer"
                    >
                        <X size={18} />
                    </button>
                </div>

                {/* Error Banner */}
                {error && (
                    <div className="mt-4 p-3 rounded-xl bg-red-50 border border-red-200 text-xs text-[#DF1F28] font-medium flex items-center gap-2">
                        <AlertCircle size={16} className="shrink-0" />
                        <span>{error}</span>
                    </div>
                )}

                {/* Form */}
                <form onSubmit={handleSubmit} className="mt-4 space-y-3.5">
                    {/* Username */}
                    <div>
                        <label className="block text-xs font-bold uppercase tracking-wider text-slate-600 mb-1">
                            {i18n.language === 'en' ? 'Username' : 'ឈ្មោះអ្នកប្រើប្រាស់'}
                        </label>
                        <div className="relative">
                            <User size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none" />
                            <input
                                type="text"
                                required
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                placeholder={i18n.language === 'en' ? 'Enter username or identifier' : 'បញ្ចូលឈ្មោះអ្នកប្រើប្រាស់'}
                                className="w-full pl-10 pr-4 py-2 rounded-xl border border-slate-200 bg-slate-50/50 hover:bg-white focus:bg-white focus:border-[#253C95] focus:ring-2 focus:ring-[#253C95]/10 outline-none text-xs sm:text-sm font-medium text-slate-800 transition-all placeholder:text-slate-400"
                            />
                        </div>
                    </div>

                    {/* Email */}
                    <div>
                        <label className="block text-xs font-bold uppercase tracking-wider text-slate-600 mb-1">
                            {i18n.language === 'en' ? 'Registered Email Address' : 'អាសយដ្ឋានអ៊ីមែលដែលបានចុះឈ្មោះ'}
                        </label>
                        <div className="relative">
                            <Mail size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none" />
                            <input
                                type="email"
                                required
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                placeholder="name@example.com"
                                className="w-full pl-10 pr-4 py-2 rounded-xl border border-slate-200 bg-slate-50/50 hover:bg-white focus:bg-white focus:border-[#253C95] focus:ring-2 focus:ring-[#253C95]/10 outline-none text-xs sm:text-sm font-medium text-slate-800 transition-all placeholder:text-slate-400"
                            />
                        </div>
                    </div>

                    {/* New Password */}
                    <div>
                        <div className="flex items-center justify-between mb-1">
                            <label className="block text-xs font-bold uppercase tracking-wider text-slate-600">
                                {i18n.language === 'en' ? 'New Password' : 'ពាក្យសម្ងាត់ថ្មី'}
                            </label>
                            {strength.text && (
                                <span className={`text-xs font-bold px-2 py-0.5 rounded ${
                                    strength.score === 1 ? 'bg-red-50 text-[#DF1F28]' : strength.score === 2 ? 'bg-amber-50 text-[#F9A825]' : 'bg-emerald-50 text-emerald-700'
                                }`}>
                                    {strength.text}
                                </span>
                            )}
                        </div>
                        <div className="relative">
                            <Lock size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none" />
                            <input
                                type={showNew ? 'text' : 'password'}
                                required
                                value={newPassword}
                                onChange={(e) => setNewPassword(e.target.value)}
                                placeholder="••••••••"
                                className="w-full pl-10 pr-10 py-2 rounded-xl border border-slate-200 bg-slate-50/50 hover:bg-white focus:bg-white focus:border-[#253C95] focus:ring-2 focus:ring-[#253C95]/10 outline-none text-xs sm:text-sm font-medium text-slate-800 transition-all placeholder:text-slate-400"
                            />
                            <button
                                type="button"
                                onClick={() => setShowNew(!showNew)}
                                className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 p-0.5 cursor-pointer"
                            >
                                {showNew ? <EyeOff size={15} /> : <Eye size={15} />}
                            </button>
                        </div>
                        {/* Strength Indicator Bar */}
                        {newPassword && (
                            <div className="w-full bg-slate-100 h-1 rounded-full mt-1.5 overflow-hidden">
                                <div
                                    className={`h-full transition-all duration-300 ${strength.color}`}
                                    style={{ width: `${(strength.score / 3) * 100}%` }}
                                />
                            </div>
                        )}
                    </div>

                    {/* Confirm Password */}
                    <div>
                        <label className="block text-xs font-bold uppercase tracking-wider text-slate-600 mb-1">
                            {i18n.language === 'en' ? 'Confirm Password' : 'ផ្ទៀងផ្ទាត់ពាក្យសម្ងាត់'}
                        </label>
                        <div className="relative">
                            <Lock size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none" />
                            <input
                                type={showConfirm ? 'text' : 'password'}
                                required
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                placeholder="••••••••"
                                className="w-full pl-10 pr-10 py-2 rounded-xl border border-slate-200 bg-slate-50/50 hover:bg-white focus:bg-white focus:border-[#253C95] focus:ring-2 focus:ring-[#253C95]/10 outline-none text-xs sm:text-sm font-medium text-slate-800 transition-all placeholder:text-slate-400"
                            />
                            <button
                                type="button"
                                onClick={() => setShowConfirm(!showConfirm)}
                                className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 p-0.5 cursor-pointer"
                            >
                                {showConfirm ? <EyeOff size={15} /> : <Eye size={15} />}
                            </button>
                        </div>
                    </div>

                    {/* Submit Actions */}
                    <div className="flex items-center justify-end gap-2.5 pt-3">
                        <button
                            type="button"
                            onClick={onClose}
                            className="px-4 py-2 rounded-xl border border-slate-200 hover:bg-slate-50 text-slate-600 font-semibold text-xs sm:text-sm transition-all cursor-pointer"
                        >
                            {i18n.language === 'en' ? 'Cancel' : 'បោះបង់'}
                        </button>
                        <button
                            type="submit"
                            disabled={loading}
                            className="px-5 py-2 rounded-xl bg-[#253C95] hover:bg-[#1c2f78] text-white font-bold text-xs sm:text-sm shadow-md shadow-[#253C95]/20 transition-all flex items-center gap-2 cursor-pointer disabled:opacity-50"
                        >
                            {loading && <Loader2 size={15} className="animate-spin" />}
                            <span>{i18n.language === 'en' ? 'Save New Password' : 'រក្សាទុកពាក្យសម្ងាត់ថ្មី'}</span>
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
