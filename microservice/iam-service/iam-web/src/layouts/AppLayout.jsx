import React, { useState, useEffect } from 'react';
import { Outlet, NavLink, useLocation, Navigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import {
    User, Shield, Link2, Smartphone, History,
    LayoutDashboard, Users, Key, Settings, FileText,
    LogOut, Globe, ChevronRight, Sliders, ShieldCheck,
    ShieldAlert
} from 'lucide-react';
import { userService } from '../services/userService';

export default function AppLayout() {
    const { t, i18n } = useTranslation();
    const location = useLocation();

    const [currentUser, setCurrentUser] = useState(null);
    const [loadingUser, setLoadingUser] = useState(true);

    useEffect(() => {
        userService.getCurrentUser()
            .then(data => {
                setCurrentUser(data);
            })
            .catch(err => {
                console.warn('Could not load current user profile:', err.message);
            })
            .finally(() => {
                setLoadingUser(false);
            });
    }, []);

    // Check if current authenticated user has ADMIN privileges
    const isAdmin = Boolean(
        currentUser?.roles?.some(r => {
            const roleName = (typeof r === 'string' ? r : (r?.name || '')).toUpperCase();
            return roleName === 'ADMIN' || roleName === 'ROLE_ADMIN' || roleName === 'SYSTEM_ADMIN';
        })
    );

    const toggleLanguage = () => {
        const nextLang = i18n.language === 'en' ? 'km' : 'en';
        i18n.changeLanguage(nextLang);
        localStorage.setItem('lang', nextLang);
    };

    const accountNav = [
        { name: t('nav.profile'), path: '/account/profile', icon: User },
        { name: t('nav.security'), path: '/account/security', icon: Shield },
        { name: t('nav.linkedAccounts'), path: '/account/linked-accounts', icon: Link2 },
        { name: t('nav.sessions'), path: '/account/sessions', icon: Smartphone },
        { name: t('nav.history'), path: '/account/history', icon: History },
    ];

    const adminNav = [
        { name: t('nav.overview'), path: '/admin/overview', icon: LayoutDashboard },
        { name: t('nav.users'), path: '/admin/users', icon: Users },
        { name: t('nav.roles'), path: '/admin/roles', icon: Key },
        { name: t('nav.clients'), path: '/admin/clients', icon: ShieldCheck },
        { name: t('nav.audit'), path: '/admin/audit', icon: FileText },
        { name: t('nav.systemConfig'), path: '/admin/system', icon: Sliders },
    ];

    const allNavs = [...accountNav, ...adminNav];
    const currentNav = allNavs.find(item => location.pathname === item.path);
    const pageTitle = currentNav ? currentNav.name : t('nav.profile');

    // Route Protection: If non-admin tries to access /admin/** routes, redirect to profile
    if (!loadingUser && !isAdmin && location.pathname.startsWith('/admin')) {
        return <Navigate to="/account/profile" replace />;
    }

    // Primary active link styling (#253C95)
    const navItemClass = ({ isActive }) =>
        `group flex items-center justify-between px-3.5 py-2.5 rounded-xl text-sm sm:text-base font-medium transition-all duration-150 ${
            isActive
                ? 'bg-[#253C95] text-white shadow-md shadow-[#253C95]/20 font-semibold'
                : 'text-slate-600 hover:bg-[#253C95]/5 hover:text-[#253C95]'
        }`;

    return (
        <div className="flex h-screen bg-[#f8fafc] text-slate-800 antialiased overflow-hidden">
            {/* Sidebar */}
            <aside className="w-72 bg-white border-r border-slate-200/80 flex flex-col justify-between shrink-0 z-20">
                <div className="flex flex-col h-full overflow-hidden">
                    {/* Logo Box */}
                    <div className="h-16 flex items-center gap-3 px-6 border-b border-slate-100">
                        <div className="flex items-center gap-2.5">
                            <img
                                src="/logo.png"
                                alt="Logo"
                                className="h-12 w-auto object-contain"
                                onError={(e) => {
                                    e.currentTarget.style.display = 'none';
                                    e.currentTarget.nextElementSibling.style.display = 'flex';
                                }}
                            />
                            <div
                                style={{ display: 'none' }}
                                className="w-10 h-10 rounded-xl bg-[#253C95] text-white font-bold text-xl items-center justify-center shadow-md shadow-[#253C95]/20"
                            >
                                I
                            </div>
                        </div>
                    </div>

                    {/* Navigation Items */}
                    <div className="p-3.5 space-y-4 overflow-y-auto flex-1 custom-scrollbar">
                        {/* Account Settings (Always visible to all authenticated users) */}
                        <div>
                            <p className="px-3 text-xs sm:text-sm font-bold uppercase tracking-wider text-slate-400 mb-2">
                                {t('nav.accountGroup')}
                            </p>
                            <nav className="space-y-1">
                                {accountNav.map((item) => {
                                    const Icon = item.icon;
                                    return (
                                        <NavLink key={item.path} to={item.path} className={navItemClass}>
                                            {({ isActive }) => (
                                                <>
                                                    <div className="flex items-center gap-3">
                                                        <Icon size={19} className={isActive ? 'text-white' : 'text-slate-400 group-hover:text-[#253C95]'} />
                                                        <span className="text-sm sm:text-base">{item.name}</span>
                                                    </div>
                                                    {isActive && <ChevronRight size={16} className="text-[#F9A825]" />}
                                                </>
                                            )}
                                        </NavLink>
                                    );
                                })}
                            </nav>
                        </div>

                        {/* Administration Section (STRICTLY visible only to ADMIN users) */}
                        {isAdmin && (
                            <div className="pt-2 border-t border-slate-100">
                                <p className="px-3 text-xs sm:text-sm font-bold uppercase tracking-wider text-slate-400 mb-2 flex items-center justify-between">
                                    <span>{t('nav.managementGroup')}</span>
                                    <span className="bg-[#253C95]/10 text-[#253C95] text-[10px] px-1.5 py-0.5 rounded font-bold">ADMIN</span>
                                </p>
                                <nav className="space-y-1">
                                    {adminNav.map((item) => {
                                        const Icon = item.icon;
                                        return (
                                            <NavLink key={item.path} to={item.path} className={navItemClass}>
                                                {({ isActive }) => (
                                                    <>
                                                        <div className="flex items-center gap-3">
                                                            <Icon size={19} className={isActive ? 'text-white' : 'text-slate-400 group-hover:text-[#253C95]'} />
                                                            <span className="text-sm sm:text-base">{item.name}</span>
                                                        </div>
                                                        {isActive && <ChevronRight size={16} className="text-[#F9A825]" />}
                                                    </>
                                                )}
                                            </NavLink>
                                        );
                                    })}
                                </nav>
                            </div>
                        )}
                    </div>
                </div>

                {/* Sidebar Footer */}
                <div className="border-t border-slate-100 bg-slate-50/50">
                    {/* User Profile Mini Card */}
                    {currentUser && (
                        <div className="px-4 py-3 border-b border-slate-100 flex items-center justify-between gap-2.5">
                            <div className="flex items-center gap-2.5 min-w-0">
                                <div className="w-8 h-8 rounded-xl bg-[#253C95] text-white font-bold text-xs flex items-center justify-center shrink-0">
                                    {currentUser.name ? currentUser.name[0].toUpperCase() : (currentUser.username?.[0]?.toUpperCase() || 'U')}
                                </div>
                                <div className="min-w-0">
                                    <p className="text-xs font-bold text-slate-800 truncate">
                                        {currentUser.name || currentUser.username}
                                    </p>
                                    <p className="text-[11px] text-slate-400 font-mono truncate">
                                        @{currentUser.username}
                                    </p>
                                </div>
                            </div>
                            <span className={`px-2 py-0.5 rounded text-[10px] font-bold uppercase shrink-0 ${
                                isAdmin
                                    ? 'bg-amber-50 text-[#F9A825] border border-amber-200/60'
                                    : 'bg-slate-200/70 text-slate-600'
                            }`}>
                                {isAdmin ? 'Admin' : 'User'}
                            </span>
                        </div>
                    )}

                    {/* Secondary Red (#DF1F28) for Sign Out */}
                    <div className="p-3">
                        <a
                            href="/logout"
                            className="w-full flex items-center gap-2.5 px-3.5 py-2 rounded-xl text-sm font-semibold text-[#DF1F28] hover:bg-[#DF1F28]/10 transition-all cursor-pointer"
                        >
                            <LogOut size={17} />
                            <span className="text-xs sm:text-sm">{t('nav.logout')}</span>
                        </a>
                    </div>
                </div>
            </aside>

            {/* Main Container */}
            <div className="flex-1 flex flex-col h-screen overflow-hidden">
                {/* Top Header Bar */}
                <header className="h-16 bg-white border-b border-slate-200/80 px-7 flex items-center justify-between shrink-0 z-10">
                    <div>
                        <h1 className="text-xl font-bold text-primary leading-tight">
                            {pageTitle}
                        </h1>
                    </div>

                    {/* Right Side: Language Toggle Button */}
                    <div className="flex items-center gap-3">
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
                    </div>
                </header>

                {/* Content Area */}
                <main className="flex-1 overflow-y-auto px-6 py-5 sm:px-4 sm:py-4">
                    <div className="max-w-7xl container mx-auto space-y-4">
                        <Outlet context={{ currentUser, isAdmin }} />
                    </div>
                </main>
            </div>
        </div>
    );
}
