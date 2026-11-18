import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { adminService } from '../../services/adminService';
import CustomSelect from '../../components/CustomSelect';
import {
    FileText, Smartphone, History, Shield, AlertTriangle,
    CheckCircle2, XCircle, Search, Filter, Trash2, RefreshCw,
    Laptop, Globe, Clock, User, LogOut, ArrowRight, Loader2,
    Monitor, Tablet, Key, Layers, Settings, UserCheck, UserX, KeyRound
} from 'lucide-react';

export default function AuditPage() {
    const { t, i18n } = useTranslation();
    const [activeTab, setActiveTab] = useState('AUDIT_LOGS'); // 'AUDIT_LOGS' | 'LOGIN_HISTORY' | 'SESSIONS'
    const [searchQuery, setSearchQuery] = useState('');
    const [activityFilter, setActivityFilter] = useState('ALL'); // 'ALL' | 'USER_CHANGES' | 'ROLE_ASSIGNMENTS' | 'PERMISSION_MODIFICATIONS' | 'OAUTH_CLIENTS' | 'SYSTEM_CONFIG'
    const [loginStatusFilter, setLoginStatusFilter] = useState('ALL'); // 'ALL' | 'SUCCESS' | 'FAILED'
    const [loginMethodFilter, setLoginMethodFilter] = useState('ALL'); // 'ALL' | 'PASSWORD' | 'GOOGLE' | 'GITHUB' | 'FACEBOOK' | 'TELEGRAM'
    const [loading, setLoading] = useState(true);

    const [sessions, setSessions] = useState([]);
    const [loginHistory, setLoginHistory] = useState([]);
    const [auditLogs, setAuditLogs] = useState([]);

    // Toast
    const [toast, setToast] = useState({ show: false, message: '', type: 'success' });
    const showToast = (message, type = 'success') => {
        setToast({ show: true, message, type });
        setTimeout(() => setToast({ show: false, message: '', type: 'success' }), 3500);
    };

    const fetchAllData = async (silent = false) => {
        if (!silent) setLoading(true);
        try {
            const [auditData, loginData, sessionData] = await Promise.all([
                adminService.getEnterpriseSecurityActivity().catch(err => {
                    console.error('Failed to load enterprise security activity:', err);
                    return [];
                }),
                adminService.getEnterpriseLoginHistory('ALL').catch(err => {
                    console.error('Failed to load enterprise login history:', err);
                    return [];
                }),
                adminService.getEnterpriseSessions().catch(err => {
                    console.error('Failed to load enterprise sessions:', err);
                    return [];
                })
            ]);
            setAuditLogs(Array.isArray(auditData) ? auditData : []);
            setLoginHistory(Array.isArray(loginData) ? loginData : []);
            setSessions(Array.isArray(sessionData) ? sessionData : []);
        } finally {
            if (!silent) setLoading(false);
        }
    };

    // Load ALL data on mount
    useEffect(() => {
        fetchAllData();
    }, []);

    // Terminate individual session
    const handleTerminateSession = async (sessionId) => {
        try {
            await adminService.terminateEnterpriseSession(sessionId);
            setSessions(prev => prev.filter(s => s.sessionId !== sessionId));
            showToast('Session terminated successfully.');
        } catch (err) {
            showToast('Failed to terminate session.', 'error');
        }
    };

    const getDeviceIcon = (deviceStr = '') => {
        const d = deviceStr.toLowerCase();
        if (d.includes('mobile') || d.includes('iphone') || d.includes('android')) return Smartphone;
        if (d.includes('tablet') || d.includes('ipad')) return Tablet;
        if (d.includes('mac') || d.includes('laptop')) return Laptop;
        return Monitor;
    };

    const formatTimestamp = (ts) => {
        if (!ts) return '-';
        try {
            return new Date(ts).toLocaleString();
        } catch {
            return String(ts);
        }
    };

    // Resolve category tag & styling for audit logs
    const resolveEventBadge = (item) => {
        const et = (item.eventType || item.event || '').toUpperCase();
        if (et.includes('ROLE_ASSIGN') || et.includes('ROLE_REVOK') || et.includes('ROLE_CREAT') || et.includes('ROLE_DELET')) {
            return { label: 'Role Assignment', color: 'bg-blue-50 text-[#253C95] border-blue-200' };
        }
        if (et.includes('PERMISSION') || et.includes('PERM')) {
            return { label: 'Permission Modification', color: 'bg-purple-50 text-purple-700 border-purple-200' };
        }
        if (et.includes('CLIENT')) {
            return { label: 'OAuth2 Client', color: 'bg-indigo-50 text-indigo-700 border-indigo-200' };
        }
        if (et.includes('PASSWORD')) {
            return { label: 'Password Security', color: 'bg-amber-50 text-amber-700 border-amber-200' };
        }
        if (et.includes('DEACTIVAT') || et.includes('LOCK')) {
            return { label: 'Account Restriction', color: 'bg-red-50 text-[#DF1F28] border-red-200' };
        }
        if (et.includes('USER_') || et.includes('PROFILE') || et.includes('USERNAME')) {
            return { label: 'User Change', color: 'bg-emerald-50 text-emerald-700 border-emerald-200' };
        }
        if (et.includes('SYSTEM') || et.includes('CONFIG')) {
            return { label: 'System Policy', color: 'bg-slate-100 text-slate-700 border-slate-300' };
        }
        return { label: item.event || 'System Activity', color: 'bg-slate-50 text-slate-700 border-slate-200' };
    };

    // Filtered lists
    const filteredAuditLogs = auditLogs.filter(log => {
        const et = (log.eventType || log.event || '').toUpperCase();
        if (activityFilter === 'USER_CHANGES') {
            const matches = ['USER_', 'PROFILE', 'USERNAME', 'PASSWORD', 'DEACTIVAT', 'LOCK'];
            if (!matches.some(m => et.includes(m))) return false;
        } else if (activityFilter === 'ROLE_ASSIGNMENTS') {
            if (!et.includes('ROLE')) return false;
        } else if (activityFilter === 'PERMISSION_MODIFICATIONS') {
            if (!et.includes('PERMISSION') && !et.includes('PERM')) return false;
        } else if (activityFilter === 'OAUTH_CLIENTS') {
            if (!et.includes('CLIENT')) return false;
        } else if (activityFilter === 'SYSTEM_CONFIG') {
            if (!et.includes('SYSTEM') && !et.includes('CONFIG')) return false;
        }

        if (!searchQuery.trim()) return true;
        const q = searchQuery.toLowerCase();
        return (log.description?.toLowerCase().includes(q)) ||
               (log.details?.toLowerCase().includes(q)) ||
               (log.username?.toLowerCase().includes(q)) ||
               (log.ipAddress?.toLowerCase().includes(q)) ||
               (log.event?.toLowerCase().includes(q));
    });

    const filteredLoginHistory = loginHistory.filter(log => {
        if (loginStatusFilter !== 'ALL' && log.status !== loginStatusFilter) return false;
        if (loginMethodFilter !== 'ALL') {
            const m = (log.authMethod || log.method || 'Password').toUpperCase();
            if (!m.includes(loginMethodFilter)) return false;
        }
        if (!searchQuery.trim()) return true;
        const q = searchQuery.toLowerCase();
        return (log.device?.toLowerCase().includes(q)) ||
               (log.ipAddress?.toLowerCase().includes(q)) ||
               (log.location?.toLowerCase().includes(q)) ||
               (log.authMethod?.toLowerCase().includes(q));
    });

    const filteredSessions = sessions.filter(sess => {
        if (!searchQuery.trim()) return true;
        const q = searchQuery.toLowerCase();
        return (sess.username?.toLowerCase().includes(q)) ||
               (sess.device?.toLowerCase().includes(q)) ||
               (sess.ipAddress?.toLowerCase().includes(q));
    });

    return (
        <div className="space-y-6">
            {/* Toast Notification */}
            {toast.show && (
                <div className={`fixed bottom-6 right-6 z-50 flex items-center gap-3 px-5 py-3.5 rounded-2xl shadow-xl text-white font-medium text-sm transition-all duration-300 ${
                    toast.type === 'error' ? 'bg-[#DF1F28]' : 'bg-emerald-600'
                }`}>
                    {toast.type === 'error' ? <AlertTriangle size={18} /> : <CheckCircle2 size={18} />}
                    <span>{toast.message}</span>
                </div>
            )}

            {/* Header */}
            <div className="bg-white p-5 sm:p-6 rounded-2xl border border-slate-200/90 shadow-2xs flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div>
                    <h2 className="text-lg sm:text-xl font-bold text-slate-900">
                        {i18n.language === 'en' ? 'Audit & Security Compliance' : 'ការត្រួតពិនិត្យ និងសុវត្ថិភាព'}
                    </h2>
                    <p className="text-xs sm:text-sm text-slate-500 font-medium mt-0.5">
                        {i18n.language === 'en'
                            ? 'Monitor, track, and filter system activities, login events, user changes, role assignments, and permission modifications.'
                            : 'តាមដាន និងច្រោះសកម្មភាពប្រព័ន្ធ ការចូលប្រើប្រាស់ ការផ្លាស់ប្តូរអ្នកប្រើ ការកំណត់តួនាទី និងសិទ្ធិ។'}
                    </p>
                </div>
                <div className="flex items-center gap-2">
                    <button
                        onClick={() => fetchAllData()}
                        className="p-2 text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-xl transition-all cursor-pointer"
                        title="Refresh all audit data"
                    >
                        <RefreshCw size={18} className={loading ? 'animate-spin text-[#253C95]' : ''} />
                    </button>
                </div>
            </div>

            {/* Tab Navigation */}
            <div className="flex items-center gap-2 p-1.5 bg-slate-100 rounded-2xl w-fit flex-nowrap overflow-x-auto max-w-full">
                <button
                    onClick={() => setActiveTab('AUDIT_LOGS')}
                    className={`flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-bold transition-all cursor-pointer whitespace-nowrap ${
                        activeTab === 'AUDIT_LOGS'
                            ? 'bg-white text-[#253C95] shadow-xs'
                            : 'text-slate-600 hover:text-slate-900'
                    }`}
                >
                    <FileText size={16} />
                    <span>{i18n.language === 'en' ? 'System Activities' : 'កំណត់ហេតុសកម្មភាព'}</span>
                    <span className={`px-2 py-0.5 rounded-full text-xs font-bold ${
                        activeTab === 'AUDIT_LOGS' ? 'bg-[#253C95]/10 text-[#253C95]' : 'bg-slate-200/80 text-slate-600'
                    }`}>
                        {filteredAuditLogs.length}
                    </span>
                </button>
                <button
                    onClick={() => setActiveTab('LOGIN_HISTORY')}
                    className={`flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-bold transition-all cursor-pointer whitespace-nowrap ${
                        activeTab === 'LOGIN_HISTORY'
                            ? 'bg-white text-[#253C95] shadow-xs'
                            : 'text-slate-600 hover:text-slate-900'
                    }`}
                >
                    <History size={16} />
                    <span>{i18n.language === 'en' ? 'Login Events' : 'ប្រវត្តិចូលប្រើ'}</span>
                    <span className={`px-2 py-0.5 rounded-full text-xs font-bold ${
                        activeTab === 'LOGIN_HISTORY' ? 'bg-[#253C95]/10 text-[#253C95]' : 'bg-slate-200/80 text-slate-600'
                    }`}>
                        {filteredLoginHistory.length}
                    </span>
                </button>
                <button
                    onClick={() => setActiveTab('SESSIONS')}
                    className={`flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-bold transition-all cursor-pointer whitespace-nowrap ${
                        activeTab === 'SESSIONS'
                            ? 'bg-white text-[#253C95] shadow-xs'
                            : 'text-slate-600 hover:text-slate-900'
                    }`}
                >
                    <Smartphone size={16} />
                    <span>{i18n.language === 'en' ? 'Active Sessions' : 'សម័យការងារសកម្ម'}</span>
                    <span className={`px-2 py-0.5 rounded-full text-xs font-bold ${
                        activeTab === 'SESSIONS' ? 'bg-[#253C95]/10 text-[#253C95]' : 'bg-slate-200/80 text-slate-600'
                    }`}>
                        {filteredSessions.length}
                    </span>
                </button>
            </div>

            {/* Filter & Search Bar */}
            <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-xs flex flex-col sm:flex-row items-stretch sm:items-center justify-between gap-3">
                {/* Search Input */}
                <div className="relative flex-1 max-w-md">
                    <Search size={18} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none" />
                    <input
                        type="text"
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        placeholder={
                            activeTab === 'LOGIN_HISTORY'
                                ? 'Search by device, IP, location, method...'
                                : activeTab === 'SESSIONS'
                                ? 'Search sessions by username, IP, device...'
                                : 'Search by action, user, IP address...'
                        }
                        className="w-full pl-10 pr-4 py-2 rounded-xl border border-slate-200 text-sm focus:border-[#253C95] focus:ring-2 focus:ring-[#253C95]/10 outline-none transition-all"
                    />
                </div>

                {/* Filter Dropdowns on the right */}
                <div className="flex flex-wrap items-center gap-3">
                    {/* Tab 1 Filter: Category */}
                    {activeTab === 'AUDIT_LOGS' && (
                        <div className="flex items-center gap-2 w-full sm:w-auto">
                            <Filter size={16} className="text-slate-400 shrink-0" />
                            <CustomSelect
                                value={activityFilter}
                                onChange={(val) => setActivityFilter(val)}
                                options={[
                                    { value: 'ALL', label: 'All System Activities' },
                                    { value: 'USER_CHANGES', label: 'User Changes' },
                                    { value: 'ROLE_ASSIGNMENTS', label: 'Role Assignments' },
                                    { value: 'PERMISSION_MODIFICATIONS', label: 'Permission Modifications' },
                                    { value: 'OAUTH_CLIENTS', label: 'OAuth2 Clients' },
                                    { value: 'SYSTEM_CONFIG', label: 'System Policies' }
                                ]}
                                className="w-full sm:w-60"
                            />
                        </div>
                    )}

                    {/* Tab 2 Filters: Status & Method */}
                    {activeTab === 'LOGIN_HISTORY' && (
                        <div className="flex flex-wrap items-center gap-3 w-full sm:w-auto">
                            <div className="flex items-center gap-2 flex-1 sm:flex-initial">
                                <Filter size={16} className="text-slate-400 shrink-0" />
                                <CustomSelect
                                    value={loginStatusFilter}
                                    onChange={(val) => setLoginStatusFilter(val)}
                                    options={[
                                        { value: 'ALL', label: 'All Status' },
                                        { value: 'SUCCESS', label: 'Successful' },
                                        { value: 'FAILED', label: 'Failed' }
                                    ]}
                                    className="w-full sm:w-36"
                                />
                            </div>
                            <div className="flex items-center gap-2 flex-1 sm:flex-initial">
                                <Globe size={16} className="text-slate-400 shrink-0" />
                                <CustomSelect
                                    value={loginMethodFilter}
                                    onChange={(val) => setLoginMethodFilter(val)}
                                    options={[
                                        { value: 'ALL', label: 'All Methods' },
                                        { value: 'PASSWORD', label: 'Password' },
                                        { value: 'GOOGLE', label: 'Google SSO' },
                                        { value: 'GITHUB', label: 'GitHub SSO' },
                                        { value: 'FACEBOOK', label: 'Facebook SSO' },
                                        { value: 'TELEGRAM', label: 'Telegram' }
                                    ]}
                                    className="w-full sm:w-40"
                                />
                            </div>
                        </div>
                    )}
                </div>
            </div>

            {/* Loading State */}
            {loading ? (
                <div className="bg-white p-14 rounded-2xl border border-slate-200/90 shadow-2xs flex flex-col items-center justify-center gap-2 text-slate-400">
                    <Loader2 size={32} className="animate-spin text-[#253C95]" />
                    <span className="text-sm font-medium">Loading audit data...</span>
                </div>
            ) : (
                <>
                    {/* --- TAB 1: SYSTEM AUDIT LOGS --- */}
                    {activeTab === 'AUDIT_LOGS' && (
                        <div className="bg-white rounded-2xl border border-slate-200/90 shadow-2xs overflow-hidden">
                            {filteredAuditLogs.length === 0 ? (
                                <div className="p-10 text-center text-slate-400 text-sm font-medium">
                                    No audit events match the selected filter.
                                </div>
                            ) : (
                                <div className="overflow-x-auto">
                                    <table className="w-full text-left text-sm border-collapse">
                                        <thead>
                                            <tr className="bg-slate-50/80 border-b border-slate-100 text-sm font-bold uppercase tracking-wider text-slate-500 whitespace-nowrap">
                                                <th className="px-5 py-3.5 whitespace-nowrap">Category / Action</th>
                                                <th className="px-5 py-3.5 whitespace-nowrap">Details & Target User</th>
                                                <th className="px-5 py-3.5 whitespace-nowrap">Origin IP</th>
                                                <th className="px-5 py-3.5 whitespace-nowrap text-right">Timestamp</th>
                                            </tr>
                                        </thead>
                                        <tbody className="divide-y divide-slate-100 font-medium text-slate-800">
                                            {filteredAuditLogs.map(item => {
                                                const badge = resolveEventBadge(item);
                                                return (
                                                    <tr key={item.id} className="hover:bg-slate-50/60 transition-colors">
                                                        <td className="px-5 py-3.5 whitespace-nowrap">
                                                            <div className="flex flex-col gap-1">
                                                                <span className={`inline-block w-fit px-2.5 py-0.5 rounded-md text-xs font-bold border ${badge.color}`}>
                                                                    {badge.label}
                                                                </span>
                                                                <span className="text-xs font-mono font-medium text-slate-500">
                                                                    {item.event || item.eventType}
                                                                </span>
                                                            </div>
                                                        </td>
                                                        <td className="px-5 py-3.5 text-slate-700">
                                                            <p className="font-semibold text-slate-900 leading-tight">
                                                                {item.description || item.details}
                                                            </p>
                                                            {item.username && (
                                                                <span className="inline-block mt-1 text-xs font-mono text-indigo-700 bg-indigo-50 px-2 py-0.5 rounded">
                                                                    @{item.username}
                                                                </span>
                                                            )}
                                                        </td>
                                                        <td className="px-5 py-3.5 font-mono text-slate-600 text-sm whitespace-nowrap">
                                                            {item.ipAddress || '127.0.0.1 (Localhost)'}
                                                        </td>
                                                        <td className="px-5 py-3.5 text-right text-sm font-mono text-slate-500 whitespace-nowrap">
                                                            {formatTimestamp(item.timestamp)}
                                                        </td>
                                                    </tr>
                                                );
                                            })}
                                        </tbody>
                                    </table>
                                </div>
                            )}
                        </div>
                    )}

                    {/* --- TAB 2: GLOBAL LOGIN HISTORY --- */}
                    {activeTab === 'LOGIN_HISTORY' && (
                        <div className="bg-white rounded-2xl border border-slate-200/90 shadow-2xs overflow-hidden">
                            {filteredLoginHistory.length === 0 ? (
                                <div className="p-10 text-center text-slate-400 text-sm font-medium">
                                    No login attempts match the selected filter.
                                </div>
                            ) : (
                                <div className="overflow-x-auto">
                                    <table className="w-full text-left text-sm border-collapse">
                                        <thead>
                                            <tr className="bg-slate-50/80 border-b border-slate-100 text-sm font-bold uppercase tracking-wider text-slate-500 whitespace-nowrap">
                                                <th className="px-5 py-3.5 whitespace-nowrap">Status</th>
                                                <th className="px-5 py-3.5 whitespace-nowrap">Device & Method</th>
                                                <th className="px-5 py-3.5 whitespace-nowrap">IP & Location</th>
                                                <th className="px-5 py-3.5 whitespace-nowrap text-right">Timestamp</th>
                                            </tr>
                                        </thead>
                                        <tbody className="divide-y divide-slate-100 font-medium text-slate-800">
                                            {filteredLoginHistory.map(log => (
                                                <tr key={log.id} className="hover:bg-slate-50/60 transition-colors">
                                                    <td className="px-5 py-3.5 whitespace-nowrap">
                                                        {log.status === 'SUCCESS' ? (
                                                            <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-sm font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
                                                                <CheckCircle2 size={14} className="text-emerald-500" /> Success
                                                            </span>
                                                        ) : (
                                                            <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-sm font-semibold bg-red-50 text-[#DF1F28] border border-red-200">
                                                                <XCircle size={14} className="text-[#DF1F28]" /> Failed
                                                            </span>
                                                        )}
                                                    </td>
                                                    <td className="px-5 py-3.5">
                                                        <p className="font-semibold text-slate-900 leading-tight">{log.device}</p>
                                                        {(() => {
                                                            const m = (log.method || log.authMethod || 'Password').toUpperCase();
                                                            let badgeCls = 'bg-slate-100 text-slate-600 border-slate-200';
                                                            if (m.includes('GOOGLE')) badgeCls = 'bg-blue-50 text-blue-700 border-blue-200';
                                                            else if (m.includes('GITHUB')) badgeCls = 'bg-slate-100 text-slate-900 border-slate-300 font-bold';
                                                            else if (m.includes('FACEBOOK')) badgeCls = 'bg-indigo-50 text-indigo-700 border-indigo-200';
                                                            else if (m.includes('TELEGRAM')) badgeCls = 'bg-sky-50 text-sky-700 border-sky-200';
                                                            return (
                                                                <span className={`inline-block mt-1 px-2.5 py-0.5 rounded text-xs font-bold uppercase border ${badgeCls}`}>
                                                                    {log.method || log.authMethod || 'Password'}
                                                                </span>
                                                            );
                                                        })()}
                                                    </td>
                                                    <td className="px-5 py-3.5">
                                                        <p className="font-mono text-sm text-slate-700 font-medium">{log.ipAddress}</p>
                                                        <p className="text-sm text-slate-400 mt-0.5">{log.location}</p>
                                                    </td>
                                                    <td className="px-5 py-3.5 text-right text-sm font-mono text-slate-500 whitespace-nowrap">
                                                        {formatTimestamp(log.timestamp)}
                                                    </td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                            )}
                        </div>
                    )}

                    {/* --- TAB 3: ACTIVE ENTERPRISE SESSIONS --- */}
                    {activeTab === 'SESSIONS' && (
                        <div>
                            {filteredSessions.length === 0 ? (
                                <div className="bg-white p-10 rounded-2xl border border-slate-200/90 text-center text-slate-400 text-sm font-medium">
                                    No active user sessions currently stored in Redis.
                                </div>
                            ) : (
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-3.5">
                                    {filteredSessions.map(sess => {
                                        const DeviceIcon = getDeviceIcon(sess.device);
                                        return (
                                            <div key={sess.sessionId} className="bg-white p-4 sm:p-5 rounded-2xl border border-slate-200/90 shadow-2xs flex items-center justify-between gap-4">
                                                <div className="flex items-center gap-3.5 min-w-0">
                                                    <div className="w-11 h-11 rounded-xl bg-[#253C95]/10 text-[#253C95] flex items-center justify-center shrink-0">
                                                        <DeviceIcon size={22} />
                                                    </div>
                                                    <div className="min-w-0">
                                                        <div className="flex items-center gap-2">
                                                            <p className="text-base font-bold text-slate-900 truncate">
                                                                @{sess.username}
                                                            </p>
                                                            {sess.isCurrent && (
                                                                <span className="px-2.5 py-0.5 rounded text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
                                                                    Your Session
                                                                </span>
                                                            )}
                                                        </div>
                                                        <p className="text-sm text-slate-600 mt-0.5 truncate">{sess.device}</p>
                                                        <p className="text-sm text-slate-400 font-mono mt-0.5">
                                                            {sess.ipAddress} &bull; {sess.lastAccessedAt ? formatTimestamp(sess.lastAccessedAt) : 'Active now'}
                                                        </p>
                                                    </div>
                                                </div>
                                                {!sess.isCurrent && (
                                                    <button
                                                        onClick={() => handleTerminateSession(sess.sessionId)}
                                                        className="px-3.5 py-2 rounded-xl border border-red-200 text-[#DF1F28] hover:bg-red-50 text-sm font-semibold transition-all cursor-pointer shrink-0"
                                                    >
                                                        Terminate
                                                    </button>
                                                )}
                                            </div>
                                        );
                                    })}
                                </div>
                            )}
                        </div>
                    )}
                </>
            )}
        </div>
    );
}
