import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { userService } from '../../services/userService';
import Toast from '../../components/Toast';
import {
    User, Mail, Phone, Globe2, CheckCircle2,
    AlertCircle, Loader2, ShieldCheck, Sparkles,
    ChevronDown, Check, Pencil, X
} from 'lucide-react';

export default function ProfilePage() {
    const { t, i18n } = useTranslation();
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [user, setUser] = useState(null);
    const [localeDropdownOpen, setLocaleDropdownOpen] = useState(false);
    const [form, setForm] = useState({
        username: '',
        givenName: '',
        nickname: '',
        phoneNumber: '',
        locale: 'en'
    });
    const [alert, setAlert] = useState(null);

    useEffect(() => {
        loadProfile();
    }, []);

    const loadProfile = async () => {
        try {
            setLoading(true);
            const data = await userService.getCurrentUser();
            setUser(data);
            setForm({
                username: data.username || '',
                givenName: data.givenName || data.name || '',
                nickname: data.nickname || '',
                phoneNumber: data.phoneNumber || '',
                locale: data.locale || 'en'
            });
        } catch (err) {
            console.error(err);
            const fallback = {
                name: "Sovannda Kung",
                username: "kungsovannda",
                email: "kungsovannda@gmail.com",
                emailVerified: true,
                phoneNumber: "+855 12 345 678",
                locale: "en",
                roles: [{ name: "ADMIN" }]
            };
            setUser(fallback);
            setForm({
                username: fallback.username,
                givenName: fallback.name,
                nickname: "Sovannda",
                phoneNumber: fallback.phoneNumber,
                locale: fallback.locale
            });
        } finally {
            setLoading(false);
        }
    };

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleCancel = () => {
        setIsEditing(false);
        if (user) {
            setForm({
                username: user.username || '',
                givenName: user.givenName || user.name || '',
                nickname: user.nickname || '',
                phoneNumber: user.phoneNumber || '',
                locale: user.locale || 'en'
            });
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSaving(true);
        setAlert(null);
        try {
            const updated = await userService.updateCurrentUser(form);
            setAlert({ type: 'success', message: t('profile.successMsg') });
            setIsEditing(false);
            if (updated) {
                setUser(updated);
                setForm({
                    username: updated.username || '',
                    givenName: updated.givenName || updated.name || '',
                    nickname: updated.nickname || '',
                    phoneNumber: updated.phoneNumber || '',
                    locale: updated.locale || 'en'
                });
            } else {
                loadProfile();
            }
        } catch (err) {
            setAlert({
                type: 'error',
                message: err.response?.data?.detail || t('profile.errorMsg')
            });
        } finally {
            setSaving(false);
        }
    };

    if (loading) {
        return (
            <div className="flex flex-col items-center justify-center min-h-[300px] gap-2">
                <Loader2 className="animate-spin text-[#253C95]" size={32} />
                <span className="text-xs text-slate-400">Loading...</span>
            </div>
        );
    }

    const initials = user?.name
        ? user.name.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase()
        : (user?.username?.substring(0, 2).toUpperCase() || 'U');

    return (
        <div className="space-y-4">
            {/* Floating Toast Notification */}
            <Toast alert={alert} onClose={() => setAlert(null)} />

            {/* User Header Profile Card */}
            <div className="bg-white p-6 sm:p-7 rounded-3xl border border-slate-200/90 shadow-2xs flex flex-col sm:flex-row items-start sm:items-center justify-between gap-5">
                <div className="flex items-center gap-5">
                    {/* Primary Blue Avatar (#253C95) */}
                    <div className="w-18 h-18 rounded-2xl bg-[#253C95] text-white font-bold text-2xl flex items-center justify-center shadow-md shadow-[#253C95]/20 shrink-0">
                        {initials}
                    </div>
                    <div>
                        <div className="flex items-center gap-2">
                            <h2 className="text-xl sm:text-2xl font-bold text-slate-900 leading-tight">
                                {user?.name || user?.username}
                            </h2>
                            {user?.emailVerified && (
                                <ShieldCheck size={18} className="text-emerald-500" title="Verified Account" />
                            )}
                        </div>
                        <p className="text-slate-500 text-sm sm:text-base mt-1">{user?.email}</p>
                        <div className="flex flex-wrap items-center gap-2 mt-2.5">
                            <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-lg text-sm font-semibold bg-emerald-50 text-emerald-700 border border-emerald-100">
                                <span className="w-2 h-2 rounded-full bg-[#F9A825]" />
                                Active
                            </span>
                            {user?.roles?.map((r, i) => (
                                <span key={i} className="inline-flex items-center px-3 py-1 rounded-lg text-sm font-semibold bg-[#253C95]/10 text-[#253C95] border border-[#253C95]/15">
                                    {typeof r === 'string' ? r : r.name}
                                </span>
                            ))}
                        </div>
                    </div>
                </div>

                <div className="sm:text-right text-sm text-slate-400">
                    <span className="text-sm font-medium">User ID: </span>
                    <span className="font-mono text-slate-700 text-sm block mt-1">
                        {user?.uuid ? `${user.uuid.substring(0, 16)}...` : 'N/A'}
                    </span>
                </div>
            </div>

            {/* Profile Details Card (View Mode / Edit Mode) */}
            <div className="bg-white p-6 sm:p-8 rounded-3xl border border-slate-200/90 shadow-2xs">
                {/* Card Header with Edit / Cancel Toggle */}
                <div className="flex items-center justify-between pb-4 border-b border-slate-100 mb-6">
                    <div className="flex items-center gap-2.5">
                        <Sparkles size={18} className="text-[#F9A825]" />
                        <h3 className="text-sm sm:text-base font-bold uppercase tracking-wider text-slate-700">
                            {isEditing
                                ? (i18n.language === 'en' ? 'Edit Profile Details' : 'កែប្រែព័ត៌មានលម្អិត')
                                : (i18n.language === 'en' ? 'Profile Details' : 'ព័ត៌មានលម្អិតគណនី')
                            }
                        </h3>
                    </div>

                    {!isEditing ? (
                        <button
                            type="button"
                            onClick={() => setIsEditing(true)}
                            className="px-4 py-2 bg-[#253C95]/10 hover:bg-[#253C95]/15 text-[#253C95] font-semibold text-sm sm:text-base rounded-xl flex items-center gap-2 cursor-pointer transition-all active:scale-95"
                        >
                            <Pencil size={16} />
                            <span>{i18n.language === 'en' ? 'Edit' : 'កែប្រែ'}</span>
                        </button>
                    ) : (
                        <button
                            type="button"
                            onClick={handleCancel}
                            className="px-3.5 py-1.5 text-slate-500 hover:bg-slate-100 rounded-xl text-sm sm:text-base font-semibold flex items-center gap-1.5 cursor-pointer transition-all"
                        >
                            <X size={17} />
                            <span>{t('common.cancel')}</span>
                        </button>
                    )}
                </div>

                {!isEditing ? (
                    /* VIEW MODE: Clean, Read-Only Details */
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                        {/* Username */}
                        <div className="bg-slate-50/70 p-4 rounded-2xl border border-slate-100/90">
                            <span className="block text-xs sm:text-sm font-bold uppercase tracking-wider text-slate-400 mb-1">
                                {t('profile.username')}
                            </span>
                            <div className="flex items-center gap-2.5 text-slate-800 font-medium text-sm sm:text-base">
                                <User size={18} className="text-[#253C95]" />
                                <span>{user?.username || form.username || '—'}</span>
                            </div>
                        </div>

                        {/* Email Address */}
                        <div className="bg-slate-50/70 p-4 rounded-2xl border border-slate-100/90">
                            <span className="block text-xs sm:text-sm font-bold uppercase tracking-wider text-slate-400 mb-1">
                                {t('profile.email')}
                            </span>
                            <div className="flex items-center gap-2.5 text-slate-800 font-medium text-sm sm:text-base">
                                <Mail size={18} className="text-[#253C95]" />
                                <span>{user?.email || '—'}</span>
                            </div>
                        </div>

                        {/* Given Name */}
                        <div className="bg-slate-50/70 p-4 rounded-2xl border border-slate-100/90">
                            <span className="block text-xs sm:text-sm font-bold uppercase tracking-wider text-slate-400 mb-1">
                                {t('profile.givenName')}
                            </span>
                            <div className="flex items-center gap-2.5 text-slate-800 font-medium text-sm sm:text-base">
                                <User size={18} className="text-[#253C95]" />
                                <span>{user?.givenName || user?.name || form.givenName || '—'}</span>
                            </div>
                        </div>

                        {/* Nickname */}
                        <div className="bg-slate-50/70 p-4 rounded-2xl border border-slate-100/90">
                            <span className="block text-xs sm:text-sm font-bold uppercase tracking-wider text-slate-400 mb-1">
                                {t('profile.nickname')}
                            </span>
                            <div className="flex items-center gap-2.5 text-slate-800 font-medium text-sm sm:text-base">
                                <User size={18} className="text-[#253C95]" />
                                <span>{user?.nickname || form.nickname || 'Not set'}</span>
                            </div>
                        </div>

                        {/* Phone Number */}
                        <div className="bg-slate-50/70 p-4 rounded-2xl border border-slate-100/90">
                            <span className="block text-xs sm:text-sm font-bold uppercase tracking-wider text-slate-400 mb-1">
                                {t('profile.phone')}
                            </span>
                            <div className="flex items-center gap-2.5 text-slate-800 font-medium text-sm sm:text-base">
                                <Phone size={18} className="text-[#253C95]" />
                                <span>{user?.phoneNumber || form.phoneNumber || 'Not set'}</span>
                            </div>
                        </div>

                        {/* Preferred Language */}
                        <div className="bg-slate-50/70 p-4 rounded-2xl border border-slate-100/90">
                            <span className="block text-xs sm:text-sm font-bold uppercase tracking-wider text-slate-400 mb-1">
                                {t('profile.locale')}
                            </span>
                            <div className="flex items-center gap-2.5 text-slate-800 font-medium text-sm sm:text-base">
                                <Globe2 size={18} className="text-[#253C95]" />
                                <span>
                                    {form.locale === 'km' ? '🇰🇭 ភាសាខ្មែរ (Cambodia)' : '🇺🇸 English (United States)'}
                                </span>
                            </div>
                        </div>
                    </div>
                ) : (
                    /* EDIT MODE: Form Inputs */
                    <form onSubmit={handleSubmit} className="space-y-5">
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                            {/* Username */}
                            <div>
                                <label className="block text-sm font-bold uppercase tracking-wider text-slate-600 mb-2">
                                    {t('profile.username')}
                                </label>
                                <div className="relative">
                                    <User size={18} className="absolute left-3.5 top-3.5 text-slate-400" />
                                    <input
                                        type="text"
                                        name="username"
                                        value={form.username}
                                        onChange={handleChange}
                                        className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-[#253C95]/20 focus:border-[#253C95] text-sm sm:text-base font-medium transition-all"
                                        required
                                    />
                                </div>
                            </div>

                            {/* Email (Read only) */}
                            <div>
                                <label className="block text-sm font-bold uppercase tracking-wider text-slate-600 mb-2">
                                    {t('profile.email')}
                                </label>
                                <div className="relative">
                                    <Mail size={18} className="absolute left-3.5 top-3.5 text-slate-400" />
                                    <input
                                        type="email"
                                        value={user?.email || ''}
                                        disabled
                                        className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-200 bg-slate-50 text-slate-500 cursor-not-allowed text-sm sm:text-base font-medium"
                                    />
                                </div>
                            </div>

                            {/* Given Name */}
                            <div>
                                <label className="block text-sm font-bold uppercase tracking-wider text-slate-600 mb-2">
                                    {t('profile.givenName')}
                                </label>
                                <input
                                    type="text"
                                    name="givenName"
                                    value={form.givenName}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-[#253C95]/20 focus:border-[#253C95] text-sm sm:text-base font-medium transition-all"
                                />
                            </div>

                            {/* Nickname */}
                            <div>
                                <label className="block text-sm font-bold uppercase tracking-wider text-slate-600 mb-2">
                                    {t('profile.nickname')}
                                </label>
                                <input
                                    type="text"
                                    name="nickname"
                                    value={form.nickname}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-[#253C95]/20 focus:border-[#253C95] text-sm sm:text-base font-medium transition-all"
                                />
                            </div>

                            {/* Phone */}
                            <div>
                                <label className="block text-sm font-bold uppercase tracking-wider text-slate-600 mb-2">
                                    {t('profile.phone')}
                                </label>
                                <div className="relative">
                                    <Phone size={18} className="absolute left-3.5 top-3.5 text-slate-400" />
                                    <input
                                        type="text"
                                        name="phoneNumber"
                                        value={form.phoneNumber}
                                        onChange={handleChange}
                                        placeholder="+855 12 345 678"
                                        className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-[#253C95]/20 focus:border-[#253C95] text-sm sm:text-base font-medium transition-all"
                                    />
                                </div>
                            </div>

                            {/* Preferred Language Custom Dropdown */}
                            <div className="relative">
                                <label className="block text-sm font-bold uppercase tracking-wider text-slate-600 mb-2">
                                    {t('profile.locale')}
                                </label>

                                {/* Trigger Button */}
                                <button
                                    type="button"
                                    onClick={() => setLocaleDropdownOpen(!localeDropdownOpen)}
                                    className="w-full flex items-center justify-between px-4 py-2.5 rounded-xl border border-slate-200 bg-white hover:border-[#253C95]/50 focus:outline-none focus:ring-2 focus:ring-[#253C95]/20 focus:border-[#253C95] text-sm sm:text-base font-medium transition-all cursor-pointer shadow-2xs"
                                >
                                    <div className="flex items-center gap-3 min-w-0">
                                        <Globe2 size={18} className="text-[#253C95] shrink-0" />
                                        <span className="truncate text-slate-800 text-sm sm:text-base">
                                            {form.locale === 'km' ? 'ភាសាខ្មែរ (Cambodia)' : 'English (United States)'}
                                        </span>
                                    </div>

                                    <ChevronDown
                                        size={17}
                                        className={`text-slate-400 shrink-0 transition-transform duration-200 ${
                                            localeDropdownOpen ? 'rotate-180 text-[#253C95]' : ''
                                        }`}
                                    />
                                </button>

                                {/* Dropdown Menu */}
                                {localeDropdownOpen && (
                                    <>
                                        <div
                                            className="fixed inset-0 z-20"
                                            onClick={() => setLocaleDropdownOpen(false)}
                                        />

                                        <div className="absolute left-0 right-0 top-full mt-2 bg-white rounded-2xl shadow-xl shadow-slate-200/70 border border-slate-100 p-2 z-30">
                                            {/* English Option */}
                                            <button
                                                type="button"
                                                onClick={() => {
                                                    setForm({ ...form, locale: 'en' });
                                                    setLocaleDropdownOpen(false);
                                                }}
                                                className={`w-full flex items-center justify-between px-3.5 py-2.5 rounded-xl text-sm sm:text-base font-medium transition-colors cursor-pointer ${
                                                    form.locale === 'en'
                                                        ? 'bg-[#253C95]/10 text-[#253C95] font-semibold'
                                                        : 'text-slate-700 hover:bg-slate-50 hover:text-slate-900'
                                                }`}
                                            >
                                                <div className="flex items-center gap-3">
                                                    <span className="text-base">🇺🇸</span>
                                                    <span className="text-sm sm:text-base">English (United States)</span>
                                                </div>
                                                {form.locale === 'en' && <Check size={17} className="text-[#253C95]" />}
                                            </button>

                                            {/* Khmer Option */}
                                            <button
                                                type="button"
                                                onClick={() => {
                                                    setForm({ ...form, locale: 'km' });
                                                    setLocaleDropdownOpen(false);
                                                }}
                                                className={`w-full flex items-center justify-between px-3.5 py-2.5 rounded-xl text-sm sm:text-base font-medium transition-colors cursor-pointer ${
                                                    form.locale === 'km'
                                                        ? 'bg-[#253C95]/10 text-[#253C95] font-semibold'
                                                        : 'text-slate-700 hover:bg-slate-50 hover:text-slate-900'
                                                }`}
                                            >
                                                <div className="flex items-center gap-3">
                                                    <span className="text-base">🇰🇭</span>
                                                    <span className="text-sm sm:text-base">ភាសាខ្មែរ (Cambodia)</span>
                                                </div>
                                                {form.locale === 'km' && <Check size={17} className="text-[#253C95]" />}
                                            </button>
                                        </div>
                                    </>
                                )}
                            </div>
                        </div>

                        {/* Form Buttons: Cancel & Save Changes */}
                        <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100">
                            <button
                                type="button"
                                onClick={handleCancel}
                                className="px-5 py-2.5 border border-slate-200 text-slate-600 hover:bg-slate-50 font-semibold text-sm sm:text-base rounded-xl transition-all cursor-pointer"
                            >
                                {t('common.cancel')}
                            </button>
                            <button
                                type="submit"
                                disabled={saving}
                                className="px-6 py-2.5 bg-[#253C95] hover:bg-[#1c2f78] text-white font-semibold text-sm sm:text-base rounded-xl shadow-sm transition-all duration-150 active:scale-95 disabled:opacity-50 flex items-center gap-2 cursor-pointer"
                            >
                                {saving && <Loader2 size={18} className="animate-spin" />}
                                <span>{saving ? t('profile.saving') : t('profile.saveBtn')}</span>
                            </button>
                        </div>
                    </form>
                )}
            </div>
        </div>
    );
}