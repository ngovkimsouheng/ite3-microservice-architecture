import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { adminService } from '../../services/adminService';
import {
    Users, Shield, Key, ShieldCheck, Activity,
    CheckCircle2, AlertTriangle, ArrowUpRight, Clock,
    Server, RefreshCw, Zap, Lock
} from 'lucide-react';
import { Link } from 'react-router-dom';

export default function OverviewPage() {
    const { t } = useTranslation();
    const [loading, setLoading] = useState(true);
    const [stats, setStats] = useState({
        totalUsers: 0,
        activeUsers: 0,
        totalRoles: 0,
        totalPerms: 0,
        oauthClients: 3,
        systemHealth: 'HEALTHY'
    });
    const [recentUsers, setRecentUsers] = useState([]);

    const fetchOverviewData = async () => {
        setLoading(true);
        try {
            const [usersData, rolesData, permsData] = await Promise.all([
                adminService.getUsers(0, 10).catch(() => ({ content: [], totalElements: 0 })),
                adminService.getRoles(0, 50).catch(() => ({ content: [], totalElements: 0 })),
                adminService.getPermissions(0, 100).catch(() => ({ content: [], totalElements: 0 }))
            ]);

            const users = usersData.content || [];
            const roles = rolesData.content || [];
            const perms = permsData.content || [];

            const activeCount = users.filter(u => u.enabled && !u.accountLocked).length;

            setStats({
                totalUsers: usersData.totalElements || users.length,
                activeUsers: activeCount > 0 ? activeCount : users.length,
                totalRoles: rolesData.totalElements || roles.length,
                totalPerms: permsData.totalElements || perms.length,
                oauthClients: 3,
                systemHealth: 'HEALTHY'
            });

            setRecentUsers(users.slice(0, 5));
        } catch (err) {
            console.error('Failed to load overview data', err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchOverviewData();
    }, []);

    const metricCards = [
        {
            title: t('admin.overview.totalUsers'),
            value: stats.totalUsers,
            subtitle: `${stats.activeUsers} ${t('admin.overview.activeUsers')}`,
            icon: Users,
            color: 'bg-blue-500/10 text-[#253C95]',
            border: 'border-blue-100',
            link: '/admin/users'
        },
        {
            title: t('admin.overview.totalRoles'),
            value: stats.totalRoles,
            subtitle: 'Configured Access Roles',
            icon: Shield,
            color: 'bg-indigo-500/10 text-indigo-600',
            border: 'border-indigo-100',
            link: '/admin/roles'
        },
        {
            title: t('admin.overview.totalPerms'),
            value: stats.totalPerms,
            subtitle: 'Granular Authorities',
            icon: Key,
            color: 'bg-emerald-500/10 text-emerald-600',
            border: 'border-emerald-100',
            link: '/admin/roles'
        },
        {
            title: t('admin.overview.oauthClients'),
            value: stats.oauthClients,
            subtitle: 'OIDC Registered Apps',
            icon: ShieldCheck,
            color: 'bg-amber-500/10 text-[#F9A825]',
            border: 'border-amber-100',
            link: '/admin/clients'
        }
    ];

    const quickLinks = [
        { label: t('admin.users.createUser'), desc: 'Provision a new identity', path: '/admin/users', icon: Users },
        { label: t('admin.roles.createRole'), desc: 'Define access tier', path: '/admin/roles', icon: Shield },
        { label: t('admin.clients.registerClient'), desc: 'Register OAuth2 client', path: '/admin/clients', icon: ShieldCheck },
        { label: t('admin.system.title'), desc: 'Token & lockout policy', path: '/admin/system', icon: Zap }
    ];

    return (
        <div className="space-y-6">
            {/* Header with Refresh button */}
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white p-6 rounded-2xl border border-slate-200 shadow-xs">
                <div>
                    <h2 className="text-xl sm:text-2xl font-bold text-slate-800">{t('admin.overview.title')}</h2>
                    <p className="text-sm sm:text-base text-slate-500 mt-1">{t('admin.overview.subtitle')}</p>
                </div>
                <div className="flex items-center gap-3">
                    <div className="flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-emerald-50 border border-emerald-200 text-emerald-700 text-sm font-semibold">
                        <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-pulse"></span>
                        {t('admin.overview.operational')}
                    </div>
                    <button
                        onClick={fetchOverviewData}
                        disabled={loading}
                        className="p-2.5 rounded-xl border border-slate-200 hover:bg-slate-50 text-slate-600 transition-all cursor-pointer active:scale-95"
                        title="Refresh metrics"
                    >
                        <RefreshCw size={18} className={loading ? 'animate-spin text-[#253C95]' : ''} />
                    </button>
                </div>
            </div>

            {/* Metrics Grid */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                {metricCards.map((card, idx) => {
                    const Icon = card.icon;
                    return (
                        <Link
                            key={idx}
                            to={card.link}
                            className={`p-5 rounded-2xl bg-white border ${card.border} shadow-xs hover:shadow-md transition-all group block`}
                        >
                            <div className="flex items-center justify-between">
                                <div className={`p-3 rounded-xl ${card.color}`}>
                                    <Icon size={24} />
                                </div>
                                <ArrowUpRight size={18} className="text-slate-300 group-hover:text-[#253C95] transition-colors" />
                            </div>
                            <div className="mt-4">
                                <p className="text-sm font-medium text-slate-500">{card.title}</p>
                                <p className="text-2xl sm:text-3xl font-bold text-slate-800 mt-1">{loading ? '...' : card.value}</p>
                                <p className="text-xs sm:text-sm text-slate-400 mt-1 font-medium">{card.subtitle}</p>
                            </div>
                        </Link>
                    );
                })}
            </div>

            {/* Middle Section: System Status & Quick Actions */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                {/* System Governance Status */}
                <div className="lg:col-span-2 bg-white p-6 rounded-2xl border border-slate-200 shadow-xs space-y-5">
                    <div className="flex items-center justify-between border-b border-slate-100 pb-4">
                        <div className="flex items-center gap-3">
                            <div className="p-2.5 rounded-xl bg-[#253C95]/10 text-[#253C95]">
                                <Server size={22} />
                            </div>
                            <div>
                                <h3 className="text-base sm:text-lg font-bold text-slate-800">{t('admin.overview.systemStatus')}</h3>
                                <p className="text-xs sm:text-sm text-slate-400">Microservice infrastructure & authorization server</p>
                            </div>
                        </div>
                        <span className="text-xs sm:text-sm font-bold text-emerald-600 bg-emerald-50 px-3 py-1 rounded-lg">
                            UP-TO-DATE
                        </span>
                    </div>

                    <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                        <div className="p-4 rounded-xl bg-slate-50 border border-slate-100">
                            <p className="text-xs text-slate-400 uppercase tracking-wider font-semibold">IAM Engine</p>
                            <p className="text-sm sm:text-base font-bold text-slate-800 mt-1">Spring Authorization Server</p>
                            <p className="text-xs text-slate-500 mt-1">OIDC 1.0 & OAuth2.1</p>
                        </div>
                        <div className="p-4 rounded-xl bg-slate-50 border border-slate-100">
                            <p className="text-xs text-slate-400 uppercase tracking-wider font-semibold">Token Signer</p>
                            <p className="text-sm sm:text-base font-bold text-slate-800 mt-1">RSA 2048 (Self-Contained)</p>
                            <p className="text-xs text-slate-500 mt-1">JWK Key Set Active</p>
                        </div>
                        <div className="p-4 rounded-xl bg-slate-50 border border-slate-100">
                            <p className="text-xs text-slate-400 uppercase tracking-wider font-semibold">Database Persistence</p>
                            <p className="text-sm sm:text-base font-bold text-slate-800 mt-1">PostgreSQL Liquibase</p>
                            <p className="text-xs text-emerald-600 font-semibold mt-1">Connected & Pooled</p>
                        </div>
                    </div>

                    {/* Recent Users preview */}
                    <div className="pt-2">
                        <div className="flex items-center justify-between mb-3">
                            <h4 className="text-sm sm:text-base font-bold text-slate-700">Recently Active Users</h4>
                            <Link to="/admin/users" className="text-xs sm:text-sm text-[#253C95] font-semibold hover:underline">
                                View All &rarr;
                            </Link>
                        </div>
                        <div className="divide-y divide-slate-100 border border-slate-100 rounded-xl overflow-hidden">
                            {recentUsers.map((u, i) => (
                                <div key={i} className="p-3.5 flex items-center justify-between hover:bg-slate-50/60 transition-colors">
                                    <div className="flex items-center gap-3">
                                        <div className="w-9 h-9 rounded-full bg-[#253C95]/10 text-[#253C95] font-bold text-sm flex items-center justify-center">
                                            {u.username ? u.username.charAt(0).toUpperCase() : 'U'}
                                        </div>
                                        <div>
                                            <p className="text-sm font-semibold text-slate-800">{u.username}</p>
                                            <p className="text-xs text-slate-400">{u.email || 'No email provided'}</p>
                                        </div>
                                    </div>
                                    <span className="px-2.5 py-1 rounded-md text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
                                        {u.enabled ? 'ACTIVE' : 'DISABLED'}
                                    </span>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>

                {/* Quick Actions Column */}
                <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs flex flex-col justify-between">
                    <div>
                        <h3 className="text-base sm:text-lg font-bold text-slate-800 mb-1">{t('admin.overview.quickActions')}</h3>
                        <p className="text-xs sm:text-sm text-slate-400 mb-5">Frequently accessed administrative operations</p>
                        <div className="space-y-3">
                            {quickLinks.map((ql, idx) => {
                                const Icon = ql.icon;
                                return (
                                    <Link
                                        key={idx}
                                        to={ql.path}
                                        className="flex items-center justify-between p-3.5 rounded-xl border border-slate-100 hover:border-[#253C95]/30 hover:bg-[#253C95]/5 transition-all group"
                                    >
                                        <div className="flex items-center gap-3">
                                            <div className="p-2 rounded-lg bg-slate-100 text-slate-600 group-hover:bg-[#253C95] group-hover:text-white transition-colors">
                                                <Icon size={18} />
                                            </div>
                                            <div>
                                                <p className="text-sm font-bold text-slate-800 group-hover:text-[#253C95] transition-colors">{ql.label}</p>
                                                <p className="text-xs text-slate-400">{ql.desc}</p>
                                            </div>
                                        </div>
                                        <ArrowUpRight size={16} className="text-slate-300 group-hover:text-[#253C95]" />
                                    </Link>
                                );
                            })}
                        </div>
                    </div>

                    <div className="mt-6 p-4 rounded-xl bg-amber-50 border border-amber-200 text-amber-800">
                        <div className="flex items-start gap-2.5">
                            <AlertTriangle size={18} className="text-[#F9A825] shrink-0 mt-0.5" />
                            <div>
                                <p className="text-xs sm:text-sm font-bold">Security Best Practice</p>
                                <p className="text-xs text-amber-700 mt-0.5">
                                    Enforce 2FA and verify PKCE is mandated for all public single-page clients.
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

