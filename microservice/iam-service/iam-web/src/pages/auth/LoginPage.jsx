import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import {
    Lock, User, LogIn, AlertCircle, Eye, EyeOff,
    Globe, ArrowRight, ShieldCheck, Sparkles, Check, X
} from 'lucide-react';
import TelegramLoginModal from '../../components/TelegramLoginModal';

export default function LoginPage() {
    const { t, i18n } = useTranslation();
    const [username, setUsername] = useState('kungsovannda');
    const [password, setPassword] = useState('supersecret');
    const [showPassword, setShowPassword] = useState(false);
    const [rememberMe, setRememberMe] = useState(false);
    const [telegramModalOpen, setTelegramModalOpen] = useState(false);

    // Check if there is an error in URL params (e.g. /login?error)
    const searchParams = new URLSearchParams(window.location.search);
    const hasError = searchParams.has('error');
    const [showToast, setShowToast] = useState(hasError);
    const [toastProgress, setToastProgress] = useState(100);

    // Standard 5-second auto-dismiss with progress countdown
    useEffect(() => {
        if (hasError) {
            setShowToast(true);
            setToastProgress(100);
            const duration = 5000; // 5 seconds
            const interval = 50;
            const step = (interval / duration) * 100;

            const timer = setInterval(() => {
                setToastProgress((prev) => {
                    if (prev <= step) {
                        clearInterval(timer);
                        setShowToast(false);
                        return 0;
                    }
                    return prev - step;
                });
            }, interval);

            return () => clearInterval(timer);
        }
    }, [hasError]);

    const toggleLanguage = () => {
        const next = i18n.language === 'en' ? 'km' : 'en';
        i18n.changeLanguage(next);
        localStorage.setItem('lang', next);
    };

    return (
        <div className="min-h-screen bg-[#f8fafc] text-slate-800 flex flex-col justify-between relative overflow-hidden font-sans">
            {/* Ambient Background Gradient Circles */}
            <div className="absolute top-0 left-1/2 -translate-x-1/2 w-[1000px] h-[500px] bg-gradient-to-b from-[#253C95]/5 to-transparent rounded-full blur-3xl pointer-events-none -z-10" />
            <div className="absolute -bottom-32 -left-32 w-96 h-96 bg-[#253C95]/5 rounded-full blur-3xl pointer-events-none -z-10" />
            <div className="absolute -bottom-32 -right-32 w-96 h-96 bg-[#F9A825]/5 rounded-full blur-3xl pointer-events-none -z-10" />

            {/* Standard Floating Toast Alert Notification */}
            {showToast && (
                <div className="fixed top-6 left-1/2 -translate-x-1/2 z-50 w-full max-w-md px-4 transition-all duration-300">
                    <div className="bg-white/95 backdrop-blur-md border border-red-200/90 rounded-2xl shadow-xs p-4 relative overflow-hidden flex items-start gap-3.5">
                        {/* Alert Icon */}
                        <div className="w-9 h-9 rounded-xl bg-red-50 flex items-center justify-center shrink-0 text-[#DF1F28] border border-red-100">
                            <AlertCircle size={20} />
                        </div>

                        {/* Alert Content */}
                        <div className="flex-1 pr-1 pt-0.5">
                            <h4 className="text-sm font-bold text-slate-900 leading-tight">
                                {i18n.language === 'en' ? 'Authentication Failed' : 'ការផ្ទៀងផ្ទាត់បរាជ័យ'}
                            </h4>
                            <p className="text-xs sm:text-sm text-slate-600 font-medium mt-1 leading-snug">
                                {i18n.language === 'en'
                                    ? 'Invalid username or password. Please try again.'
                                    : 'ឈ្មោះអ្នកប្រើប្រាស់ ឬពាក្យសម្ងាត់មិនត្រឹមត្រូវទេ។ សូមព្យាយាមម្តងទៀត។'}
                            </p>
                        </div>

                        {/* Close Button X */}
                        <button
                            type="button"
                            onClick={() => setShowToast(false)}
                            className="text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-lg p-1 transition-all cursor-pointer shrink-0"
                            title="Dismiss"
                            aria-label="Close alert"
                        >
                            <X size={18} />
                        </button>

                        {/* Standard Auto-Dismiss Progress Bar (5 seconds) */}
                        <div className="absolute bottom-0 left-0 right-0 h-1 bg-red-100/70">
                            <div
                                className="h-full bg-[#DF1F28] transition-all duration-75 ease-linear"
                                style={{ width: `${toastProgress}%` }}
                            />
                        </div>
                    </div>
                </div>
            )}

            {/* Top Navbar Header */}
            <header className="w-full px-6 py-4 flex items-center justify-end max-w-7xl mx-auto">
                {/* Navbar Language Switcher */}
                <button
                    onClick={toggleLanguage}
                    className="flex items-center gap-2.5 px-3.5 py-1.5 rounded-xl border border-slate-200 bg-white hover:bg-slate-50 text-sm font-semibold text-slate-700 transition-all shadow-2xs cursor-pointer active:scale-95"
                >
                    <Globe size={18} className="text-[#253C95]" />
                    <span className="text-sm font-medium">Language / ភាសា</span>
                    <span className="bg-[#253C95]/10 text-[#253C95] px-2.5 py-0.5 rounded-lg text-xs font-bold">
                        {i18n.language === 'en' ? 'EN 🇬🇧' : 'KM 🇰🇭'}
                    </span>
                </button>
            </header>

            {/* Main Form Container */}
            <div className="w-full max-w-md mx-auto px-4 py-4">
                {/* Centered Brand Header with ISTAD Logo */}
                <div className="text-center mb-6">
                    <div className="flex justify-center mb-3">
                        {/*<img*/}
                        {/*    src="/logo.png"*/}
                        {/*    alt="ISTAD Logo"*/}
                        {/*    className="h-14 sm:h-16 w-auto object-contain"*/}
                        {/*    onError={(e) => {*/}
                        {/*        e.currentTarget.style.display = 'none';*/}
                        {/*        e.currentTarget.nextElementSibling.style.display = 'flex';*/}
                        {/*    }}*/}
                        {/*/>*/}
                        <div
                            style={{ display: 'none' }}
                            className="w-14 h-14 rounded-2xl bg-[#253C95] text-white font-extrabold text-2xl items-center justify-center shadow-lg shadow-[#253C95]/25"
                        >
                            ISTAD
                        </div>
                    </div>
                    <h1 className="text-2xl sm:text-3xl font-extrabold text-[#253C95] tracking-tight">
                        {i18n.language === 'en' ? 'Sign in ' : 'ចូលគណនី '}
                    </h1>
                    {/*<p className="mt-1.5 text-sm sm:text-base text-slate-500 font-medium">*/}
                    {/*    {i18n.language === 'en'*/}
                    {/*        ? 'Identity & Access Management Platform'*/}
                    {/*        : 'ប្រព័ន្ធគ្រប់គ្រងអត្តសញ្ញាណ និងការចូលប្រើប្រាស់'}*/}
                    {/*</p>*/}
                </div>

                {/* Form Card */}
                <div className="bg-white p-7 sm:p-9 shadow-xl shadow-slate-200/70 border border-slate-200/90 rounded-3xl backdrop-blur-sm">
                    {/* Standard Form Login to Spring Boot */}
                    <form action="/login" method="POST" className="space-y-4">
                        {/* Username Input */}
                        <div>
                            <label className="block text-xs font-bold uppercase tracking-wider text-slate-600 mb-1.5">
                                {i18n.language === 'en' ? 'Username' : 'ឈ្មោះអ្នកប្រើប្រាស់'}
                            </label>
                            <div className="relative">
                                <User size={19} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
                                <input
                                    type="text"
                                    name="username"
                                    required
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                    placeholder="e.g. kungsovannda"
                                    className="w-full pl-11 pr-4 py-2.5 rounded-2xl border border-slate-200 bg-slate-50/50 hover:bg-white focus:bg-white focus:border-[#253C95] focus:ring-4 focus:ring-[#253C95]/10 outline-none text-sm sm:text-base font-medium text-slate-800 transition-all placeholder:text-slate-400"
                                />
                            </div>
                        </div>

                        {/* Password Input */}
                        <div>
                            <label className="block text-xs font-bold uppercase tracking-wider text-slate-600 mb-1.5">
                                {i18n.language === 'en' ? 'Password' : 'ពាក្យសម្ងាត់'}
                            </label>
                            <div className="relative">
                                <Lock size={19} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
                                <input
                                    type={showPassword ? 'text' : 'password'}
                                    name="password"
                                    required
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    placeholder="••••••••"
                                    className="w-full pl-11 pr-11 py-2.5 rounded-2xl border border-slate-200 bg-slate-50/50 hover:bg-white focus:bg-white focus:border-[#253C95] focus:ring-4 focus:ring-[#253C95]/10 outline-none text-sm sm:text-base font-medium text-slate-800 transition-all placeholder:text-slate-400"
                                />
                                <button
                                    type="button"
                                    onClick={() => setShowPassword(!showPassword)}
                                    className="absolute right-3.5 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 p-1 cursor-pointer"
                                    title={showPassword ? 'Hide password' : 'Show password'}
                                >
                                    {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                                </button>
                            </div>
                        </div>

                        {/* Custom Styled Remember Me Checkbox with Primary Color */}
                        <div className="flex items-center justify-between text-xs sm:text-sm pt-1">
                            <label className="group flex items-center gap-2.5 text-slate-600 hover:text-slate-900 cursor-pointer select-none transition-colors">
                                {/* Native input (visually hidden, handles change and form post) */}
                                <input
                                    type="checkbox"
                                    name="remember-me"
                                    checked={rememberMe}
                                    onChange={(e) => setRememberMe(e.target.checked)}
                                    className="sr-only"
                                />

                                {/* Custom Interactive Checkbox Box */}
                                <div
                                    className={`w-5 h-5 rounded-lg flex items-center justify-center transition-all duration-200 border ${
                                        rememberMe
                                            ? 'bg-[#253C95] border-[#253C95] shadow-sm shadow-[#253C95]/30'
                                            : 'bg-white border-slate-300 group-hover:border-[#253C95]/60 group-hover:bg-slate-50'
                                    }`}
                                >
                                    <Check
                                        size={13}
                                        strokeWidth={3.5}
                                        className={`text-white transition-all duration-200 ${
                                            rememberMe ? 'scale-100 opacity-100' : 'scale-50 opacity-0'
                                        }`}
                                    />
                                </div>

                                <span className="font-medium text-slate-700 text-sm">
                                    {i18n.language === 'en' ? 'Remember me for 7 days' : 'ចងចាំខ្ញុំរយៈពេល ៧ ថ្ងៃ'}
                                </span>
                            </label>
                        </div>

                        {/* Submit Button (Brand Blue #253C95 with interactive hover & active effects) */}
                        <div className="pt-2">
                            <button
                                type="submit"
                                className="group w-full py-3 px-4 bg-[#253C95] hover:bg-[#1c2f78] text-white font-bold text-sm sm:text-base rounded-2xl shadow-lg shadow-[#253C95]/20 hover:shadow-xl hover:shadow-[#253C95]/30 transition-all duration-200 active:scale-[0.98] flex items-center justify-center gap-2.5 cursor-pointer"
                            >
                                <LogIn size={19} className="text-[#F9A825] transition-transform duration-200 group-hover:translate-x-0.5" />
                                <span>{i18n.language === 'en' ? 'Sign In' : 'ចូលគណនី'}</span>
                            </button>
                        </div>
                    </form>

                    {/* Social Logins Divider */}
                    <div className="mt-7">
                        <div className="relative">
                            <div className="absolute inset-0 flex items-center">
                                <div className="w-full border-t border-slate-200/80" />
                            </div>
                            <div className="relative flex justify-center text-xs uppercase">
                                <span className="bg-white px-3 text-slate-400 font-bold tracking-wider">
                                    {i18n.language === 'en' ? 'Or continue with' : 'ឬចូលតាមរយៈ'}
                                </span>
                            </div>
                        </div>

                        {/* Social Logins: Google, GitHub, Facebook, Telegram (Interactive 1:1 Squares with rounded-2xl) */}
                        <div className="mt-5 flex items-center justify-center gap-3.5">
                            {/* Google */}
                            <a
                                href="/oauth2/authorization/google"
                                title="Google"
                                aria-label="Sign in with Google"
                                className="w-12 h-12 flex items-center justify-center rounded-2xl border border-slate-200/90 bg-white hover:bg-[#ebf0ff]/50 hover:border-[#253C95]/40 shadow-2xs hover:shadow-md hover:shadow-[#253C95]/10 transition-all duration-200 active:scale-95 group shrink-0"
                            >
                                <svg className="w-5 h-5 shrink-0 transition-transform duration-200 group-hover:scale-115" viewBox="0 0 24 24">
                                    <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                                    <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                                    <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z"/>
                                    <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z"/>
                                </svg>
                            </a>

                            {/* GitHub */}
                            <a
                                href="/oauth2/authorization/github"
                                title="GitHub"
                                aria-label="Sign in with GitHub"
                                className="w-12 h-12 flex items-center justify-center rounded-2xl border border-slate-200/90 bg-white hover:bg-[#ebf0ff]/50 hover:border-[#253C95]/40 shadow-2xs hover:shadow-md hover:shadow-[#253C95]/10 transition-all duration-200 active:scale-95 group shrink-0"
                            >
                                <svg className="w-5 h-5 shrink-0 fill-[#24292F] transition-transform duration-200 group-hover:scale-115" viewBox="0 0 24 24">
                                    <path d="M12 0C5.37 0 0 5.37 0 12c0 5.31 3.435 9.795 8.205 11.385.6.105.825-.255.825-.57 0-.285-.015-1.23-.015-2.235-3.015.555-3.795-.735-4.035-1.41-.135-.345-.72-1.41-1.23-1.695-.42-.225-1.02-.78-.015-.795.945-.015 1.62.87 1.845 1.23 1.08 1.815 2.805 1.305 3.495.99.105-.78.42-1.305.765-1.605-2.67-.3-5.46-1.335-5.46-5.925 0-1.305.465-2.385 1.23-3.225-.12-.3-.54-1.53.12-3.18 0 0 1.005-.315 3.3 1.23.96-.27 1.98-.405 3-.405s2.04.135 3 .405c2.295-1.56 3.3-1.23 3.3-1.23.66 1.65.24 2.88.12 3.18.765.84 1.23 1.905 1.23 3.225 0 4.605-2.805 5.625-5.475 5.925.435.375.81 1.095.81 2.22 0 1.605-.015 2.895-.015 3.3 0 .315.225.69.825.57A12.02 12.02 0 0024 12c0-6.63-5.37-12-12-12z"/>
                                </svg>
                            </a>

                            {/* Facebook */}
                            <a
                                href="/oauth2/authorization/facebook"
                                title="Facebook"
                                aria-label="Sign in with Facebook"
                                className="w-12 h-12 flex items-center justify-center rounded-2xl border border-slate-200/90 bg-white hover:bg-[#ebf0ff]/50 hover:border-[#253C95]/40 shadow-2xs hover:shadow-md hover:shadow-[#253C95]/10 transition-all duration-200 active:scale-95 group shrink-0"
                            >
                                <svg className="w-5 h-5 shrink-0 fill-[#1877F2] transition-transform duration-200 group-hover:scale-115" viewBox="0 0 24 24">
                                    <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
                                </svg>
                            </a>

                            {/* Telegram */}
                            <button
                                type="button"
                                onClick={() => setTelegramModalOpen(true)}
                                title="Telegram"
                                aria-label="Sign in with Telegram"
                                className="w-12 h-12 flex items-center justify-center rounded-2xl border border-slate-200/90 bg-white hover:bg-[#ebf0ff]/50 hover:border-[#253C95]/40 shadow-2xs hover:shadow-md hover:shadow-[#253C95]/10 transition-all duration-200 active:scale-95 group shrink-0 cursor-pointer"
                            >
                                <svg className="w-5 h-5 shrink-0 fill-[#229ED9] transition-transform duration-200 group-hover:scale-115" viewBox="0 0 24 24">
                                    <path d="M12 0C5.373 0 0 5.373 0 12s5.373 12 12 12 12-5.373 12-12S18.627 0 12 0zm5.894 8.221l-1.97 9.28c-.145.658-.537.818-1.084.508l-3-2.21-1.446 1.394c-.16.16-.295.295-.605.295l.213-3.053 5.56-5.023c.242-.213-.054-.333-.373-.121l-6.871 4.326-2.962-.924c-.643-.204-.657-.643.136-.953l11.57-4.458c.538-.196 1.006.128.832.941z"/>
                                </svg>
                            </button>
                        </div>
                    </div>
                </div>

    
            </div>

            {/* Telegram Login Widget Modal */}
            <TelegramLoginModal
                isOpen={telegramModalOpen}
                onClose={() => setTelegramModalOpen(false)}
                mode="login"
            />
        </div>
    );
}
