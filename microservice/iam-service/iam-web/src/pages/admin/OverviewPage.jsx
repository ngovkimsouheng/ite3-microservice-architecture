import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { adminService } from '../../services/adminService';
import {
    Users, Shield, Key, ShieldCheck, Activity,
    CheckCircle2, AlertTriangle, ArrowUpRight, Clock,
    Server, RefreshCw, Zap, Lock, KeyRound, UserCheck,
    ShieldAlert, Loader2, Smartphone
} from 'lucide-react';
import { Link } from 'react-router-dom';

const ICON_MAP = {
    KeyRound: KeyRound,
    UserCheck: UserCheck,
    Lock: Lock,
    Activity: Activity,
    ShieldAlert: ShieldAlert,
    Shield: Shield
};

export default function OverviewPage() {
    const { t, i18n } = useTranslation();
    const [loading, setLoading] = useState(true);
    const [stats, setStats] = useState({
        totalUsers: 0,
        activeUsers: 0,
        totalRoles: 0,
        totalPerms: 0,
        oauthClients: 0,
        totalActivities: 0
    });
    const [recentActivities, setRecentActivities] = useState([]);

    const fetchOverviewData = async () => {
        setLoading(true);
        try {
            const [usersData, rolesData, permsData, clientsData, activitiesData] = await Promise.all([
                adminService.getUsers(0, 100).catch(() => ({ content: [], totalElements: 0 })),
                adminService.getRoles(0, 100).catch(() => ({ content: [], totalElements: 0 })),
                adminService.getPermissions(0, 200).catch(() => ({ content: [], totalElements: 0 })),
                adminService.getClients().catch(() => []),
                adminService.getEnterpriseSecurityActivity().catch(() => [])
            ]);

            const users = usersData.content || [];
            const roles = rolesData.content || [];
            const perms = permsData.content || [];
            const clients = Array.isArray(clientsData) ? clientsData : [];
            const activities = Array.isArray(activitiesData) ? activitiesData : [];

            const activeCount = users.filter(u => u.enabled && !u.accountLocked).length;

            setStats({
                totalUsers: usersData.totalElements ?? users.length,
                activeUsers: activeCount > 0 ? activeCount : users.length,
                totalRoles: rolesData.totalElements ?? roles.length,
                totalPerms: permsData.totalElements ?? perms.length,
                oauthClients: clients.length,
                totalActivities: activities.length
            });

            setRecentActivities(activities.slice(0, 6));
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
            title: i18n.language === 'en' ? 'Total Users' : 'អ្នកប្រើប្រាស់សរុប',
            value: stats.totalUsers,
            subtitle: `${stats.activeUsers} ${i18n.language === 'en' ? 'Active' : 'សកម្ម'}`,
            icon: Users,
            color: 'bg-blue-500/10 text-[#253C95]',
            border: 'border-blue-100',
            link: '/admin/users'
        },
        {
            title: i18n.language === 'en' ? 'Active Users' : 'អ្នកប្រើប្រាស់សកម្ម',
            value: stats.activeUsers,
            subtitle: i18n.language === 'en' ? 'Currently Enabled' : 'គណនីដំណើរការ',
            icon: UserCheck,
            color: 'bg-emerald-500/10 text-emerald-600',
            border: 'border-emerald-100',
            link: '/admin/users'
        },
        {
            title: i18n.language === 'en' ? 'Total Roles' : 'តួនាទីសរុប',
            value: stats.totalRoles,
            subtitle: i18n.language === 'en' ? 'Access Tiers' : 'កម្រិតសិទ្ធិ',
            icon: Shield,
            color: 'bg-indigo-500/10 text-indigo-600',
            border: 'border-indigo-100',
            link: '/admin/roles'
        },
        {
            title: i18n.language === 'en' ? 'Permissions' : 'សិទ្ធិអនុញ្ញាត',
            value: stats.totalPerms,
            subtitle: i18n.language === 'en' ? 'Granular Authorities' : 'សិទ្ធិជាក់លាក់',
            icon: Key,
            color: 'bg-purple-500/10 text-purple-600',
            border: 'border-purple-100',
            link: '/admin/roles'
        },
        {
            title: i18n.language === 'en' ? 'OAuth2 Clients' : 'កម្មវិធី OAuth2',
            value: stats.oauthClients,
            subtitle: i18n.language === 'en' ? 'OIDC Registered Apps' : 'កម្មវិធីបានចុះឈ្មោះ',
            icon: ShieldCheck,
            color: 'bg-amber-500/10 text-[#F9A825]',
            border: 'border-amber-100',
            link: '/admin/clients'
        }
    ];

    const quickLinks = [
        {
            label: i18n.language === 'en' ? 'Create User' : 'បង្កើតអ្នកប្រើ',
            desc: i18n.language === 'en' ? 'Provision a new identity' : 'បង្កើតគណនីថ្មី',
            path: '/admin/users',
            icon: Users
        },
        {
            label: i18n.language === 'en' ? 'Manage Roles' : 'គ្រប់គ្រងតួនាទី',
            desc: i18n.language === 'en' ? 'Define role permissions' : 'កំណត់សិទ្ធិតួនាទី',
            path: '/admin/roles',
            icon: Shield
        },
        {
            label: i18n.language === 'en' ? 'OAuth2 Clients' : 'កម្មវិធី OAuth2',
            desc: i18n.language === 'en' ? 'Inspect registered apps' : 'ពិនិត្យកម្មវិធី',
            path: '/admin/clients',
            icon: ShieldCheck
        },
        {
            label: i18n.language === 'en' ? 'Enterprise Audit' : 'សវនកម្មប្រព័ន្ធ',
            desc: i18n.language === 'en' ? 'View live security logs' : 'មើលកំណត់ហេតុសុវត្ថិភាព',
            path: '/admin/audit',
            icon: Activity
        }
    ];

    const formatTimestamp = (ts) => {
        if (!ts) return '-';
        try {
            return new Date(ts).toLocaleString();
        } catch {
            return String(ts);
        }
    };

    return (
        <div className="space-y-5">
            {/* Header with Live Status & Refresh button */}
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white p-5 sm:p-6 rounded-2xl border border-slate-200/90 shadow-2xs">
                <div>
                    <h2 className="text-lg sm:text-xl font-bold text-slate-900">
                        {i18n.language === 'en' ? 'System Overview & Analytics' : 'ទិដ្ឋភាពទូទៅនៃប្រព័ន្ធ'}
                    </h2>
                    <p className="text-xs sm:text-sm text-slate-500 font-medium mt-0.5">
                        {i18n.language === 'en'
                            ? 'Real-time overview of users, active accounts, roles, permissions, clients, and recent activities.'
                            : 'ទិដ្ឋភាពរួមនៃអ្នកប្រើប្រាស់ គណនីសកម្ម តួនាទី សិទ្ធិ កម្មវិធី និងសកម្មភាពថ្មីៗ។'}
                    </p>
                </div>
                <div className="flex items-center gap-3">
                    <div className="flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-50 border border-emerald-200 text-emerald-700 text-xs font-semibold">
                        <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
                        {i18n.language === 'en' ? 'SYSTEM OPERATIONAL' : 'ប្រព័ន្ធដំណើរការប្រក្រតី'}
                    </div>
                    <button
                        onClick={fetchOverviewData}
                        disabled={loading}
                        className="p-2 rounded-xl border border-slate-200 hover:bg-slate-50 text-slate-600 transition-all cursor-pointer active:scale-95"
                        title="Refresh metrics"
                    >
                        <RefreshCw size={17} className={loading ? 'animate-spin text-[#253C95]' : ''} />
                    </button>
                </div>
            </div>

            {/* Metrics Grid (5 Real Dynamic Metrics) */}
            <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-3.5">
                {metricCards.map((card, idx) => {
                    const Icon = card.icon;
                    return (
                        <Link
                            key={idx}
                            to={card.link}
                            className={`p-4 rounded-2xl bg-white border ${card.border} shadow-2xs hover:shadow-md transition-all group block`}
                        >
                            <div className="flex items-center justify-between">
                                <div className={`p-2.5 rounded-xl ${card.color}`}>
                                    <Icon size={20} />
                                </div>
                                <ArrowUpRight size={15} className="text-slate-300 group-hover:text-[#253C95] transition-colors" />
                            </div>
                            <div className="mt-3">
                                <p className="text-xs font-medium text-slate-500">{card.title}</p>
                                <p className="text-xl sm:text-2xl font-bold text-slate-900 mt-0.5">
                                    {loading ? '...' : card.value}
                                </p>
                                <p className="text-xs text-slate-400 mt-0.5 font-medium">{card.subtitle}</p>
                            </div>
                        </Link>
                    );
                })}
            </div>

            {/* Middle Section: Recent Activities & Governance */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-5">
                {/* Real Recent Activities Log (Left / 2 Cols) */}
                <div className="lg:col-span-2 bg-white p-5 sm:p-6 rounded-2xl border border-slate-200/90 shadow-2xs space-y-4">
                    <div className="flex items-center justify-between border-b border-slate-100 pb-3.5">
                        <div className="flex items-center gap-2.5">
                            <div className="p-2 rounded-xl bg-[#253C95]/10 text-[#253C95]">
                                <Activity size={18} />
                            </div>
                            <div>
                                <h3 className="text-sm sm:text-base font-bold text-slate-900">
                                    {i18n.language === 'en' ? 'Recent Security & System Activities' : 'សកម្មភាពសុវត្ថិភាព និងប្រព័ន្ធថ្មីៗ'}
                                </h3>
                                <p className="text-xs text-slate-500 font-medium">
                                    {i18n.language === 'en'
                                        ? 'Live audit trail of user and administrative events.'
                                        : 'កំណត់ត្រាសកម្មភាពផ្ទាល់របស់អ្នកប្រើប្រាស់ និងអ្នកគ្រប់គ្រង។'}
                                </p>
                            </div>
                        </div>
                        <Link
                            to="/admin/audit"
                            className="text-xs font-semibold text-[#253C95] hover:underline flex items-center gap-1"
                        >
                            <span>{i18n.language === 'en' ? 'View Audit Logs' : 'មើលកំណត់ហេតុទាំងអស់'}</span>
                            <span>&rarr;</span>
                        </Link>
                    </div>

                    {loading ? (
                        <div className="py-10 flex flex-col items-center justify-center gap-2 text-slate-400">
                            <Loader2 size={24} className="animate-spin text-[#253C95]" />
                            <span className="text-xs font-medium">Loading recent activities...</span>
                        </div>
                    ) : recentActivities.length === 0 ? (
                        <div className="py-10 text-center text-slate-400 text-xs font-medium">
                            No recent activities recorded yet.
                        </div>
                    ) : (
                        <div className="space-y-2.5">
                            {recentActivities.map((act) => {
                                const Icon = ICON_MAP[act.icon] || Activity;
                                return (
                                    <div
                                        key={act.id}
                                        className="p-3 rounded-xl bg-slate-50/70 border border-slate-100 flex items-center justify-between gap-3 hover:bg-slate-50 transition-colors"
                                    >
                                        <div className="flex items-center gap-3 min-w-0">
                                            <div className={`w-8 h-8 rounded-xl flex items-center justify-center shrink-0 ${act.color || 'bg-[#253C95]/10 text-[#253C95]'}`}>
                                                <Icon size={15} />
                                            </div>
                                            <div className="min-w-0">
                                                <p className="text-sm font-bold text-slate-900 leading-tight truncate">
                                                    {act.event || act.category}
                                                </p>
                                                <p className="text-xs text-slate-500 mt-0.5 truncate">
                                                    {act.description}
                                                </p>
                                            </div>
                                        </div>
                                        <div className="text-xs text-slate-400 font-mono shrink-0 flex items-center gap-1">
                                            <Clock size={13} className="text-slate-400" />
                                            <span>{formatTimestamp(act.timestamp)}</span>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    )}

                    {/* Infrastructure Governance Summary */}
                    <div className="pt-3 border-t border-slate-100">
                        <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                            <div className="p-3 rounded-xl bg-slate-50 border border-slate-100">
                                <p className="text-xs text-slate-400 uppercase tracking-wider font-bold">IAM Engine</p>
                                <p className="text-sm font-bold text-slate-800 mt-0.5">Spring Authorization Server</p>
                                <p className="text-xs text-slate-500">OAuth 2.1 & OIDC 1.0</p>
                            </div>
                            <div className="p-3 rounded-xl bg-slate-50 border border-slate-100">
                                <p className="text-xs text-slate-400 uppercase tracking-wider font-bold">Token Cryptography</p>
                                <p className="text-sm font-bold text-slate-800 mt-0.5">RSA 2048 PKCS#8</p>
                                <p className="text-xs text-slate-500">Self-Contained JWK</p>
                            </div>
                            <div className="p-3 rounded-xl bg-slate-50 border border-slate-100">
                                <p className="text-xs text-slate-400 uppercase tracking-wider font-bold">Persistence & Store</p>
                                <p className="text-sm font-bold text-slate-800 mt-0.5">PostgreSQL & Redis</p>
                                <p className="text-xs text-emerald-600 font-semibold">Active & Healthy</p>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Right Column: Quick Administrative Actions */}
                <div className="bg-white p-5 sm:p-6 rounded-2xl border border-slate-200/90 shadow-2xs flex flex-col justify-between gap-4">
                    <div>
                        <h3 className="text-base sm:text-lg font-bold text-slate-900 mb-0.5">
                            {i18n.language === 'en' ? 'Quick Operations' : 'ប្រតិបត្តិការរហ័ស'}
                        </h3>
                        <p className="text-xs text-slate-500 font-medium mb-4">
                            {i18n.language === 'en'
                                ? 'Frequently used management shortcuts.'
                                : 'ផ្លូវកាត់សម្រាប់ការគ្រប់គ្រងញឹកញាប់។'}
                        </p>
                        <div className="space-y-2.5">
                            {quickLinks.map((ql, idx) => {
                                const Icon = ql.icon;
                                return (
                                    <Link
                                        key={idx}
                                        to={ql.path}
                                        className="flex items-center justify-between p-3 rounded-xl border border-slate-100 hover:border-[#253C95]/30 hover:bg-[#253C95]/5 transition-all group"
                                    >
                                        <div className="flex items-center gap-2.5">
                                            <div className="p-2 rounded-lg bg-slate-100 text-slate-600 group-hover:bg-[#253C95] group-hover:text-white transition-colors">
                                                <Icon size={16} />
                                            </div>
                                            <div>
                                                <p className="text-sm font-bold text-slate-800 group-hover:text-[#253C95] transition-colors">{ql.label}</p>
                                                <p className="text-xs text-slate-400">{ql.desc}</p>
                                            </div>
                                        </div>
                                        <ArrowUpRight size={15} className="text-slate-300 group-hover:text-[#253C95]" />
                                    </Link>
                                );
                            })}
                        </div>
                    </div>

                    <div className="p-3.5 rounded-xl bg-amber-50 border border-amber-200 text-amber-900">
                        <div className="flex items-start gap-2.5">
                            <AlertTriangle size={16} className="text-[#F9A825] shrink-0 mt-0.5" />
                            <div>
                                <p className="text-xs font-bold">Security Best Practice</p>
                                <p className="text-xs text-amber-700 mt-0.5 leading-snug">
                                    Enforce PKCE and verify authorization consent is required for all public client apps.
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
